package io.microvibe.booster.txn.alipay.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import io.microvibe.booster.commons.utils.StringUtils;
import io.microvibe.booster.core.api.ApiConstants;
import io.microvibe.booster.core.api.ApiException;
import io.microvibe.booster.core.api.annotation.ApiName;
import io.microvibe.booster.core.api.model.BodyModel;
import io.microvibe.booster.core.api.model.HeadModel;
import io.microvibe.booster.core.api.model.RequestData;
import io.microvibe.booster.core.api.model.ResponseData;
import io.microvibe.booster.core.api.support.ApiServiceSupports;
import io.microvibe.booster.core.base.web.security.JWTContext;
import io.microvibe.booster.pay.alipay.config.AlipayConfig;
import io.microvibe.booster.txn.alipay.AbstractAlipayApiService;
import io.microvibe.booster.txn.alipay.AlipayTxnCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * 用户信息授权接口回调
 *
 * @author Qt
 * @since Aug 26, 2018
 */
@Component
@ApiName(AlipayTxnCode.PAY_ALIPAY_OAUTH2_CALLBACK)
@Slf4j
public class Alipay0001 extends AbstractAlipayApiService {

	@Override
	public ResponseData doExecute(RequestData requestData) {
		try {
			BodyModel requestBody = requestData.getBody();
			ResponseData responseData = requestData.buildResponse();

			String app_id = StringUtils.trimToNull(requestBody.getString("app_id"));
			String auth_code = StringUtils.trimToNull(requestBody.getString("auth_code"));
			String app_auth_code = StringUtils.trimToNull(requestBody.getString("app_auth_code"));

			AlipayConfig alipayConfig = AlipayConfig.get();
			if (!alipayConfig.getApp_id().equals(app_id)) {
				throw new IllegalArgumentException("应用ID不合法");
			}
			if (auth_code == null || app_auth_code == null) {
				throw new IllegalArgumentException("未得到授权令牌");
			}

			String redirectUrl = "redirect:" + alipayConfig.getOauth2ResultUrl();
			if (app_auth_code != null) {
				// region 第三方应用授权

				// 间接调用接口,使用app_auth_code换取app_auth_token
				RequestData tokenRequestData = requestData.clone();
				tokenRequestData.getHead().setTxnCode(AlipayTxnCode.PAY_ALIPAY_OPEN_AUTH_TOKEN_APP);
				ResponseData tokenResponseData = ApiServiceSupports.doExecute(tokenRequestData);
				HeadModel tokenResponseHead = tokenResponseData.getHead();
				if (!tokenResponseHead.isSuccess()) {
					// 失败消息传递
					responseData.setHead(ApiConstants.HEAD_CODE, tokenResponseHead.getCode());
					responseData.setHead(ApiConstants.HEAD_SUCCESS, tokenResponseHead.isSuccess());
					responseData.setHead(ApiConstants.HEAD_MESSAGE, tokenResponseHead.getMessage());
				}

				// endregion
			} else {
				// region 用户授权

				String scope = StringUtils.trimToNull(requestBody.getString("scope"));
				String error_scope = StringUtils.trimToNull(requestBody.getString("error_scope"));
				String state = StringUtils.trimToNull(requestBody.getString("state"));

				Set<String> scopes = new HashSet<>();
				if (scope == null) {
					throw new IllegalArgumentException("未得到任何授权");
				} else {
					String[] arr = StringUtils.split(scope, ",");
					for (String s : arr) {
						scopes.add(s);
					}
				}
				// state
				if (state != null) {
					String[] arr = StringUtils.split(state, ".");
					String content = arr[0];
					String sign = arr[1];
					if (!JWTContext.getGlobalRsaJwtContext().verify(content, sign)) {
						throw new IllegalArgumentException("参数签名不合法");
					}
					if (content.startsWith("redirect:")) {
						redirectUrl = content;
					}
				}

				if (scopes.contains("auth_user")) {//获取用户信息
					AlipayClient alipayClient = alipayService.createAlipayClient(alipayConfig);

					AlipaySystemOauthTokenRequest oauthTokenRequest = new AlipaySystemOauthTokenRequest();
					oauthTokenRequest.setCode(auth_code);
					oauthTokenRequest.setGrantType("authorization_code");
					try {
						AlipaySystemOauthTokenResponse oauthTokenResponse = alipayClient.execute(oauthTokenRequest);
						String accessToken = oauthTokenResponse.getAccessToken();
						String refreshToken = oauthTokenResponse.getRefreshToken();
						String userId = oauthTokenResponse.getUserId();
						// FIXME 保存用户信息
						log.info("accessToken : {}", accessToken);
						log.info("refreshToken : {}", refreshToken);
						log.info("user_id : {}", userId);

					} catch (AlipayApiException e) {
						log.error(e.getErrMsg(), e);
						throw new ApiException(e, e.getErrMsg());
					}
				}
				// endregion
			}

			responseData.setBody("redirect",redirectUrl);
			return responseData;
		} catch (Exception e) {
			throw new ApiException(e);
		}
	}
}
