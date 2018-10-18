package io.microvibe.booster.txn.alipay.impl;

import io.microvibe.booster.commons.utils.StringUtils;
import io.microvibe.booster.core.api.ApiException;
import io.microvibe.booster.core.api.annotation.ApiName;
import io.microvibe.booster.core.api.model.BodyModel;
import io.microvibe.booster.core.api.model.RequestData;
import io.microvibe.booster.core.api.model.ResponseData;
import io.microvibe.booster.core.base.web.security.JWTContext;
import io.microvibe.booster.pay.alipay.config.AlipayConfig;
import io.microvibe.booster.txn.alipay.AbstractAlipayApiService;
import io.microvibe.booster.txn.alipay.AlipayTxnCode;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.Base64;

/**
 * 用户信息授权接口
 * <pre>
 * request:{
 *     head:{},
 *     body:{
 *        redirect:'',//授权成功后的重定向地址
 *
 *     }
 * }
 * </pre>
 *
 * @author Qt
 * @since Aug 26, 2018
 */
@Component
@ApiName(AlipayTxnCode.PAY_ALIPAY_OAUTH2_PUBLIC)
public class Alipay0002 extends AbstractAlipayApiService {

	/**
	 * 用户授权接口
	 *
	 * <pre>
	 * URL拼接与scope详解
	 *
	 * url拼接规则：https://openauth.alipay.com/oauth2/publicAppAuthorize.htm?app_id=APPID&scope=SCOPE&redirect_uri=ENCODED_URL
	 *
	 * 1. app_id:             开发者应用的app_id； 相同支付宝账号下，不同的app_id获取的token切忌混用。
	 * 2. scope:              接口权限值，目前只支持 auth_user（获取用户信息、网站支付宝登录）、
	 *                        auth_base（用户信息授权）、auth_ecard（商户会员卡）、
	 *                        auth_invoice_info（支付宝闪电开票）、auth_puc_charge（生活缴费）五个值;
	 *                        多个scope时用”,”分隔，如scope为”auth_user,auth_ecard”时，此时获取到的access_token，
	 *                        既可以用来获取用户信息，又可以给用户发送会员卡。
	 * 3. redirect_uri        授权回调地址，是经过URLENCODE转义 的url链接（url必须以http或者https开头）；
	 *                        在请求之前，开发者需要先到开发者中心对应应用内，配置授权回调地址。
	 *                        redirect_uri与应用配置的授权回调地址域名部分必须一致。
	 *
	 * 4. state               商户自定义参数，用户授权后，重定向到redirect_uri时会原样回传给商户。
	 *                        为防止CSRF攻击，建议开发者请求授权时传入state参数，该参数要做到既不可预测，
	 *                        又可以证明客户端和当前第三方网站的登录认证状态存在关联。
	 *
	 * </pre>
	 * <pre>
	 * 使用方法:
	 * http://.../openapi/pay/alipay/oauth2?scope=auth_user,auth_base&redirect=http%3A%2F%2Fwww.baidu.com
	 * </pre>
	 *
	 * @return
	 */
	@Override
	public ResponseData doExecute(RequestData requestData) {
		try {
			BodyModel requestBody = requestData.getBody();

			AlipayConfig alipayConfig = AlipayConfig.get();
			String app_id = alipayConfig.getApp_id();

			String scope = StringUtils.trimToNull(requestBody.getString("scope"));
			if (scope == null) {
				scope = "auth_user,auth_base";
			}
			String state;
			String redirectUrl = StringUtils.trimToNull(requestBody.getString("redirect"));
			if (redirectUrl != null) {
				String str = "redirect:" + redirectUrl;
				str = Base64.getEncoder().encodeToString(str.getBytes());
				String sign = JWTContext.getGlobalRsaJwtContext().sign(str);
				state = str + "." + sign;
			} else {
				String rand = RandomStringUtils.randomAlphanumeric(8);
				String sign = JWTContext.getGlobalRsaJwtContext().sign(rand);
				state = rand + "." + sign;
			}

			StringBuilder redirect = new StringBuilder();
			redirect.append(alipayConfig.getOauth2Url());
			redirect.append("/publicAppAuthorize.htm?app_id=").append(app_id);
			redirect.append("&scope=").append(URLEncoder.encode(scope, "utf-8"));
			redirect.append("&redirect_uri=").append(alipayConfig.getOauth2CallbackUrl());
			redirect.append("&state=").append(URLEncoder.encode(state, "utf-8"));

			ResponseData responseData = requestData.buildResponse();
			responseData.setBody("redirect", redirect.toString());
			responseData.setBody("location", alipayConfig.getOauth2Url() + "/publicAppAuthorize.htm");
			responseData.setBody("app_id", app_id);
			responseData.setBody("scope", scope);
			responseData.setBody("redirect_uri", alipayConfig.getOauth2CallbackUrl());
			responseData.setBody("state", state);
			return responseData;
		} catch ( Exception e) {
			throw new ApiException(e);
		}
	}
}
