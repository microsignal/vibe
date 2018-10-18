package io.microvibe.booster.txn.alipay.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConstants;
import io.microvibe.booster.commons.utils.StringUtils;
import io.microvibe.booster.core.api.ApiException;
import io.microvibe.booster.core.api.annotation.ApiName;
import io.microvibe.booster.core.api.model.BodyModel;
import io.microvibe.booster.core.api.model.HeadModel;
import io.microvibe.booster.core.api.model.RequestData;
import io.microvibe.booster.core.api.model.ResponseData;
import io.microvibe.booster.core.api.tools.DataKit;
import io.microvibe.booster.pay.alipay.config.AlipayAppConfig;
import io.microvibe.booster.pay.alipay.config.AlipayConfig;
import io.microvibe.booster.txn.alipay.AbstractAlipayApiService;
import io.microvibe.booster.txn.alipay.AlipayTxnCode;
import io.microvibe.booster.txn.alipay.support.CommonAlipayRequest;
import io.microvibe.booster.txn.alipay.support.CommonAlipayResponse;
import org.springframework.stereotype.Component;

/**
 * 通用的 Alipay 调用接口<br>
 * 直接调用 alipay 的任意接口, 未作接口请求校验与响应的解析<br>
 * 由调用方负责传入经过校验的参数, 并解析获得的响应结果<br>
 *
 * @author Qt
 * @since Aug 28, 2018
 */
@Component
@ApiName(AlipayTxnCode.PAY_ALIPAY_COMMON)
public class Alipay0000 extends AbstractAlipayApiService {

	/**
	 * 请求报文示例:
	 * <pre>
	 * {
	 *   head:{
	 *     alipayRoute:'',             // 暂不支持
	 *     alipayApiType:''            // API类型, default(默认),sdk,page
	 *   },
	 *   body:{
	 *     auth_token:'',              // access_token
	 *     app_auth_token:'',          // 第三方应用授权码
	 *     biz_content:{               // 业务参数(订单号、金额等)
	 *       ...,
	 *       out_trade_no:'',
	 *       trade_no:'',
	 *       subject:'',
	 *       total_amount:'',
	 *       body:'',
	 *       ...,
	 *     }
	 *   }
	 * }
	 * </pre>
	 * <p>
	 * 响应报文:
	 * <pre>
	 * {
	 *   head:{
	 *   },
	 *   body:{
	 *     alipayResponse: {           // 响应对象数据
	 *       ...,
	 *        code: '',
	 *        msg: '',
	 *        ...
	 *     }
	 *   }
	 * }
	 * </pre>
	 *
	 * @param requestData 请求报文
	 * @return
	 */
	@Override
	public ResponseData doExecute(RequestData requestData) {
		try {
			HeadModel head = requestData.getHead();

			// region apiType
			String apiType = StringUtils.trimToNull(head.getString("alipayApiType"));
			if (apiType == null) {
				apiType = "default";
			} else {
				apiType = apiType.toLowerCase();
			}
			// endregion

			AlipayConfig alipayConfig = AlipayConfig.get();

			BodyModel body = requestData.getBody();
			String accessToken = StringUtils.trimToNull(body.getString(AlipayConstants.ACCESS_TOKEN));
			String appAuthToken = StringUtils.trimToNull(body.getString(AlipayConstants.APP_AUTH_TOKEN));

			if (appAuthToken == null) {
				appAuthToken = AlipayAppConfig.currentAppAuthToken();
			}

			AlipayClient alipayClient = alipayService.createAlipayClient(alipayConfig);

			CommonAlipayRequest<CommonAlipayResponse> alipayRequest = CommonAlipayRequest.create();
			if (accessToken != null) {
				alipayRequest.putOtherTextParam(AlipayConstants.ACCESS_TOKEN, accessToken);
			}
			if (appAuthToken != null) {
				alipayRequest.putOtherTextParam(AlipayConstants.APP_AUTH_TOKEN, appAuthToken);
			}

			alipayRequest.setApiMethodName(body.getString(AlipayConstants.METHOD));
			alipayRequest.setTerminalType(body.getString(AlipayConstants.TERMINAL_TYPE));
			alipayRequest.setTerminalInfo(body.getString(AlipayConstants.TERMINAL_INFO));
			alipayRequest.setProdCode(body.getString(AlipayConstants.PROD_CODE));
			alipayRequest.setReturnUrl(body.getString(AlipayConstants.RETURN_URL));
			alipayRequest.setNotifyUrl(body.getString(AlipayConstants.NOTIFY_URL));
			if (org.apache.commons.lang3.StringUtils.isBlank(alipayRequest.getNotifyUrl())) {
				alipayRequest.setNotifyUrl(alipayConfig.notify_url);
			}
			JSONObject bizContent = body.getJSONObject(AlipayConstants.BIZ_CONTENT_KEY);
			alipayRequest.setBizContent(bizContent.toJSONString());


			CommonAlipayResponse alipayResponse;
			switch (apiType) {
				case "page":
					String httpMethod = StringUtils.trimToNull(body.getString("httpMethod"));
					if (httpMethod != null) {
						alipayResponse = alipayClient.pageExecute(alipayRequest, httpMethod);
					} else {
						alipayResponse = alipayClient.pageExecute(alipayRequest);
					}
					break;
				case "sdk":
					alipayResponse = alipayClient.sdkExecute(alipayRequest);
					break;
				default:
					alipayResponse = alipayClient.execute(alipayRequest, accessToken, appAuthToken);
			}

			if (!alipayResponse.isSuccess()) {
				throw new ApiException(alipayResponse.getCode() + ":" + alipayResponse.getMsg());
			}

			ResponseData responseData = DataKit.buildSuccessResponse();
			responseData.setBody("alipayResponse", alipayResponse);
			return responseData;
		} catch (AlipayApiException e) {
			throw new ApiException(e);
		}
	}

}
