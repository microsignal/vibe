package io.microvibe.booster.txn.alipay.impl;

import io.microvibe.booster.core.api.ApiException;
import io.microvibe.booster.core.api.annotation.ApiName;
import io.microvibe.booster.core.api.model.BodyModel;
import io.microvibe.booster.core.api.model.RequestData;
import io.microvibe.booster.core.api.model.ResponseData;
import io.microvibe.booster.pay.alipay.config.AlipayConfig;
import io.microvibe.booster.txn.alipay.AbstractAlipayApiService;
import io.microvibe.booster.txn.alipay.AlipayTxnCode;
import org.springframework.stereotype.Component;

/**
 * 第三方应用授权接口
 *
 * 参见: https://docs.open.alipay.com/20160728150111277227/intro
 *
 * @author Qt
 * @since Aug 26, 2018
 */
@Component
@ApiName(AlipayTxnCode.PAY_ALIPAY_OAUTH2_APP)
public class Alipay0003 extends AbstractAlipayApiService {

	/**
	 * <pre>
	 * 第一步：创建应用
	 * 第二步：配置密钥
	 * 第三步：应用授权URL拼装
	 *   拼接规则:
	 *     https://openauth.alipay.com/oauth2/appToAppAuth.htm?app_id=2015101400446982&redirect_uri=http%3A%2F%2Fexample.com
	 *
	 *     参数           参数名称            类型       必填       描述               范例
	 *     app_id        开发者应用的AppId    String     是      开发者应用的AppId      2015101400446982
	 *     redirect_uri  回调页面            String     是     参数需要UrlEncode       http%3A%2F%2Fexample.com
	 *
	 *   TIPS： 授权链接中配置的redirect_uri内容需要与应用中配置的授权回调地址完全一样，否则无法正常授权。
	 *
	 * 第四步：获取app_auth_code
	 *   商户授权成功后，pc或者钱包客户端会跳转至开发者定义的回调页面（即redirect_uri参数对应的url），
	 *   在回调页面请求中会带上当次授权的授权码app_auth_code和开发者的app_id
	 *
	 * 第五步：使用app_auth_code换取app_auth_token
	 *   接口名称：alipay.open.auth.token.app
	 *
	 *   开发者通过app_auth_code可以换取app_auth_token、授权商户的userId以及授权商户AppId。
	 *
	 *   注意:  应用授权的app_auth_code唯一的；
	 *         app_auth_code使用一次后失效，一天（从生成app_auth_code开始的24小时）未被使用自动过期；
	 *         app_auth_token永久有效。
	 *
	 *    开发者代替商户发起请求时，POST公共请求参数中的app_id应填写开发者的app_id；
	 *    如果业务参数biz_content中需要AppId，则应填写商户的AppId。
	 *
	 *
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

			StringBuilder redirect = new StringBuilder();
			redirect.append(alipayConfig.getOauth2Url());
			redirect.append("/appToAppAuth.htm?app_id=").append(app_id);
			redirect.append("&redirect_uri=").append(alipayConfig.getOauth2CallbackUrl());

			ResponseData responseData = requestData.buildResponse();
			responseData.setBodyAsString(redirect.toString());
			return responseData;
		} catch (Exception e) {
			throw new ApiException(e);
		}
	}
}
