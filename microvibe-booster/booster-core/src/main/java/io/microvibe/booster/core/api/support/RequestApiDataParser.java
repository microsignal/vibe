package io.microvibe.booster.core.api.support;

import com.alibaba.fastjson.JSONObject;
import io.microvibe.booster.core.api.ApiConstants;
import io.microvibe.booster.core.api.ApiException;
import io.microvibe.booster.core.api.ReplyCode;
import io.microvibe.booster.core.api.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class RequestApiDataParser {
	private static final Logger logger = LoggerFactory.getLogger(RequestApiDataParser.class);

	private static final ThreadLocal<RequestApiDataParser> local = new ThreadLocal<>();

	public static RequestApiDataParser parseXml(String requestStr) {
		RequestApiDataParser parser = local.get();
		if (parser == null || !parser.isXml || parser.requestStr == null || !parser.requestStr.equals(requestStr)) {
			parser = new RequestApiDataParser()._parseXml_(requestStr);
			local.set(parser);
			ApiDataType.current(ApiDataType.XML);
		}
		return parser;
	}

	public static RequestApiDataParser parseJson(String requestStr) {
		RequestApiDataParser parser = local.get();
		if (parser == null || parser.isXml || parser.requestStr == null || !parser.requestStr.equals(requestStr)) {
			parser = new RequestApiDataParser()._parseJson_(requestStr);
			local.set(parser);
			ApiDataType.current(ApiDataType.JSON);
		}
		return parser;
	}

	public static void clean() {
		current().ifPresent(o -> o._clean_());
		local.remove();
	}

	public static Optional<RequestApiDataParser> current() {
		return Optional.ofNullable(local.get());
	}

	private String requestStr;
	private boolean isXml = false;
	private RequestData requestApiData;
	private boolean hasError = false;
	private ResponseData error;

	private RequestApiDataParser() {
	}

	public RequestData getRequestApiData() {
		return requestApiData;
	}

	public boolean hasError() {
		return hasError;
	}

	public ResponseData getError() {
		return error;
	}

	private void _clean_() {
		this.requestStr = null;
		this.isXml = false;
		this.requestApiData = null;
		this.hasError = false;
		this.error = null;
	}

	private RequestApiDataParser _parseJson_(String requestStr) {
		try {
			this.requestStr = requestStr;
			this.isXml = false;
			this.requestApiData = JsonData.build(requestStr);
		} catch (ApiException e) {
			logger.warn(e.getMessage(), e);
			this.error = wrapJsonError(e.getReplyCode(), e.getMessage());
			this.hasError = true;
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			ApiException ce = new ApiException(ReplyCode.RequestParseError, e.getMessage());
			this.error = wrapJsonError(ce.getReplyCode(), ce.getMessage());
			this.hasError = true;
		}
		return this;
	}

	private RequestApiDataParser _parseXml_(String requestStr) {
		try {
			this.requestStr = requestStr;
			this.isXml = true;
			this.requestApiData = XmlData.build(requestStr);
		} catch (ApiException e) {
			logger.warn(e.getMessage(), e);
			this.error = wrapXmlError(e.getReplyCode(), e.getMessage());
			this.hasError = true;
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			ApiException ce = new ApiException(ReplyCode.RequestParseError, e.getMessage());
			this.error = wrapXmlError(ce.getReplyCode(), ce.getMessage());
			this.hasError = true;
		}
		return this;
	}

	private ResponseData wrapJsonError(ReplyCode replyCode, String message) {
		JSONObject jo = new JSONObject(true);
		JSONObject head = new JSONObject(true);
		head.put(ApiConstants.HEAD_SUCCESS, false);
		head.put(ApiConstants.HEAD_CODE, replyCode.getCode());
		head.put(ApiConstants.HEAD_MESSAGE, message);
		jo.put(ApiConstants.HEAD, head);
		return JsonData.build(jo).buildResponse();
	}

	private ResponseData wrapXmlError(ReplyCode replyCode, String message) {
		StringBuilder sb = new StringBuilder();
		sb.append("<root>");
		sb.append("<head>");
		sb.append("<").append(ApiConstants.HEAD_SUCCESS).append(">");
		sb.append("false");
		sb.append("</").append(ApiConstants.HEAD_SUCCESS).append(">");
		sb.append("<").append(ApiConstants.HEAD_CODE).append(">");
		sb.append(replyCode.getCode());
		sb.append("</").append(ApiConstants.HEAD_CODE).append(">");
		sb.append("<").append(ApiConstants.HEAD_MESSAGE).append(">");
		sb.append(message);
		sb.append("</").append(ApiConstants.HEAD_MESSAGE).append(">");
		sb.append("</head>");
		sb.append("</root>");
		return XmlData.build(sb.toString()).buildResponse();
	}


}
