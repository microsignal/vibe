package io.microvibe.booster.core.api.support;

import io.microvibe.booster.core.api.*;
import io.microvibe.booster.core.api.annotation.ApiIgnored;
import io.microvibe.booster.core.api.model.RequestData;
import io.microvibe.booster.core.api.model.ResponseData;
import io.microvibe.booster.core.api.strategy.AuthzValidator;
import io.microvibe.booster.core.api.strategy.SessionAuthcValidator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ApiIgnored
public class ApiServiceSupport implements ApiService, JsonApiService, XmlApiService {
	private static final Logger logger = LoggerFactory.getLogger(ApiServiceSupport.class);

	@Autowired
	ApiServiceRegistry apiServiceRegistry;
	@Autowired
	private AuthzValidator authzValidator;
	@Autowired
	private SessionAuthcValidator sessionAuthcValidator;

	public ApiServiceSupport() {
	}

	@Override
	public ResponseData execute(RequestData request) {
		try {
			String txnCode = StringUtils.trimToNull(request.getHead().getTxnCode());
			if (txnCode == null) {
				throw new ApiException(ReplyCode.RequestCodeEmpty);
			}
			ApiServiceWrapper wrapper = apiServiceRegistry.getApiService(txnCode);
			if (wrapper == null) {
				throw new ApiException(ReplyCode.RequestCodeUnsupported);
			}

			ApiService service = wrapper.getApiService();
			if (service == null) {
				throw new ApiException(ReplyCode.RequestCodeUnsupported);
			}

			// check http session
			if (wrapper.isSessionAuthcRequired()) {
				if (!sessionAuthcValidator.validate()) {
					throw new ApiException(ReplyCode.TxnSessionUnauthenticated);
				}
			}

			// check authz
			if (wrapper.isAuthzRequired()) {
				String accessToken = request.getHead(ApiConstants.ACCESS_TOKEN, String.class);
				String authzPermission = wrapper.getAuthzPermission();
				authzValidator.validate(accessToken, authzPermission);
			}

			// run
			ResponseData response = service.execute(request);
			if (response.getHead(ApiConstants.HEAD_CODE) == null) {
				response.setHead(ApiConstants.HEAD_SUCCESS, true);
				response.setHead(ApiConstants.HEAD_CODE, ReplyCode.Success.getCode());
				response.setHead(ApiConstants.HEAD_MESSAGE, ReplyCode.Success.getMessage());
			}
			return response;
		} catch (ApiException e) {
			logger.error(e.getMessage(), e);
			ResponseData response = request.buildResponse();
			response.setHead(ApiConstants.HEAD_SUCCESS, false);
			response.setHead(ApiConstants.HEAD_CODE, e.getReplyCode().getCode());
			response.setHead(ApiConstants.HEAD_MESSAGE, e.getMessage());
			return response;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			ResponseData response = request.buildResponse();
			ApiException ce = new ApiException(e);
			response.setHead(ApiConstants.HEAD_SUCCESS, false);
			response.setHead(ApiConstants.HEAD_CODE, ce.getReplyCode().getCode());
			response.setHead(ApiConstants.HEAD_MESSAGE, ce.getMessage());
			return response;
		}
	}

	@Override
	public ResponseData doJson(String requestStr) {
		logger.info("json request: {}", requestStr);
		try {
			RequestApiDataParser parser = RequestApiDataParser.parseJson(requestStr);
			if (parser.hasError()) {
				return parser.getError();
			} else {
				ResponseData response = execute(parser.getRequestApiData());
				return response;
			}
		} finally {
			RequestApiDataParser.clean();
		}
	}

	@Override
	public String doJsonAsString(String requestStr) {
		return doJson(requestStr).toString();
	}

	@Override
	public ResponseData doXml(String requestStr) {
		logger.info("xml request: {}", requestStr);
		try {
			RequestApiDataParser parser = RequestApiDataParser.parseXml(requestStr);
			if (parser.hasError()) {
				return parser.getError();
			} else {
				ResponseData response = execute(parser.getRequestApiData());
				return response;
			}
		} finally {
			RequestApiDataParser.clean();
		}
	}

	@Override
	public String doXmlAsString(String requestStr) {
		return doXml(requestStr).toString();
	}

    /*@Override
    public String doJson(String requestStr) {
        logger.info("json request: {}", requestStr);
        try {
            RequestData request = JsonData.parseRequest(requestStr);
            ResponseData response = execute(request);
            return response.toString();
        } catch (ApiException e) {
            logger.error(e.getMessage(), e);
            String responseStr = wrapJsonError(e.getReplyCode(), e.getMessage());
            return responseStr;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            ApiException ce = new ApiException(e);
            String responseStr = wrapJsonError(ce.getReplyCode(), ce.getMessage());
            return responseStr;
        }
    }

    private String wrapJsonError(ReplyCode replyCode, String message) {
        JSONObject jo = new JSONObject(true);
        jo.put(ApiConstants.REPLY_CODE, replyCode.getCode());
        jo.put(ApiConstants.REPLY_INFO, message);
        return jo.toJSONString();
    }

    @Override
    public String doXml(String requestStr) {
        try {
            logger.info("xml request: {}", requestStr);
            RequestData request = XmlData.parseRequest(requestStr);
            ResponseData response = execute(request);
            return response.toString();
        } catch (ApiException e) {
            logger.error(e.getMessage(), e);
            String responseStr = wrapXmlError(e.getReplyCode(), e.getMessage());
            return responseStr;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            ApiException ce = new ApiException(e);
            String responseStr = wrapXmlError(ce.getReplyCode(), ce.getMessage());
            return responseStr;
        }
    }

    private String wrapXmlError(ReplyCode replyCode, String message) {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        sb.append("<head>");
        sb.append("<").append(ApiConstants.REPLY_CODE).append(">");
        sb.append(replyCode.getCode());
        sb.append("</").append(ApiConstants.REPLY_CODE).append(">");
        sb.append("<").append(ApiConstants.REPLY_INFO).append(">");
        sb.append(message);
        sb.append("</").append(ApiConstants.REPLY_INFO).append(">");
        sb.append("</head>");
        sb.append("</xml>");
        return sb.toString();
    }*/
}
