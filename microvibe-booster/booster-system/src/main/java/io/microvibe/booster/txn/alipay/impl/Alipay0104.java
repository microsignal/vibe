package io.microvibe.booster.txn.alipay.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import io.microvibe.booster.core.api.ApiException;
import io.microvibe.booster.core.api.annotation.ApiName;
import io.microvibe.booster.core.api.model.BodyModel;
import io.microvibe.booster.core.api.model.RequestData;
import io.microvibe.booster.core.api.model.ResponseData;
import io.microvibe.booster.core.api.tools.Assertion;
import io.microvibe.booster.core.api.tools.DataKit;
import io.microvibe.booster.pay.alipay.bean.PayInput;
import io.microvibe.booster.pay.alipay.config.AlipayAppConfig;
import io.microvibe.booster.pay.alipay.config.AlipayConfig;
import io.microvibe.booster.txn.alipay.AbstractAlipayApiService;
import io.microvibe.booster.txn.alipay.AlipayTxnCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * alipay.trade.wap.pay(手机网站支付接口2.0)
 *
 * 外部商户创建订单并支付,<a href="https://docs.open.alipay.com/api_1/alipay.trade.wap.pay/">参见官方文档</a>
 *
 * @author Qt
 * @since Aug 26, 2018
 */
@Component
@ApiName(AlipayTxnCode.PAY_ALIPAY_TRADE_WAP_PAY)
@Slf4j
public class Alipay0104 extends AbstractAlipayApiService {

	public static void main(String[] args) {
		RequestData requestData = DataKit.buildRequest("", "{out_trade_no:'123'}");
		PayInput payInput = requestData.getBody().toJavaObject(PayInput.class);
		System.out.println(payInput);
		System.out.println(JSONObject.toJSONString(payInput));

	}

	@Override
	public ResponseData doExecute(RequestData requestData) {
		try {
			BodyModel requestBody = requestData.getBody();

			PayInput payInput = requestBody.toJavaObject(PayInput.class);

			Assertion.isNotBlank("缺少商户订单号", payInput.getOut_trade_no());
			Assertion.isNotBlank("缺少订付款金额", payInput.getTotal_amount());
			Assertion.isNotBlank("缺少订单名称", payInput.getSubject());
			Assertion.isNotBlank("缺少收款支付宝用户ID", payInput.getSeller_id());
			Assertion.isNotBlank("缺少用户付款中途退出返回商户网站的地址", payInput.getQuit_url());

			AlipayConfig alipayConfig = AlipayConfig.get();
			String appAuthToken = AlipayAppConfig.currentAppAuthToken();
			AlipayClient alipayClient = alipayService.createAlipayClient(alipayConfig);
			AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();

			request.setReturnUrl(alipayConfig.return_url);
			request.setNotifyUrl(alipayConfig.notify_url);
			request.setBizContent(JSONObject.toJSONString(payInput));


			AlipayTradeWapPayResponse response = alipayClient.execute(request, null, appAuthToken);
			if (log.isInfoEnabled()) {
				log.info("response: {}", JSONObject.toJSONString(response));
			}
			if (!response.isSuccess()) {
				throw new ApiException(response.getCode() + ":" + response.getMsg());
			}
			log.info("out_trade_no: {}, trade_no: {}", response.getOutTradeNo(), response.getTradeNo());

			ResponseData responseData = requestData.buildResponse();
			responseData.setBody("alipayResponse", response);
			return responseData;
		} catch (AlipayApiException e) {
			throw new ApiException(e);
		}
	}
}
