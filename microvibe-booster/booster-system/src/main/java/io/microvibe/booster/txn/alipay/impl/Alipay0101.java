package io.microvibe.booster.txn.alipay.impl;


import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePayRequest;
import com.alipay.api.response.AlipayTradePayResponse;
import io.microvibe.booster.core.api.ApiException;
import io.microvibe.booster.core.api.annotation.ApiName;
import io.microvibe.booster.core.api.model.BodyModel;
import io.microvibe.booster.core.api.model.RequestData;
import io.microvibe.booster.core.api.model.ResponseData;
import io.microvibe.booster.core.api.tools.Assertion;
import io.microvibe.booster.pay.alipay.bean.PayInput;
import io.microvibe.booster.pay.alipay.config.AlipayAppConfig;
import io.microvibe.booster.pay.alipay.config.AlipayConfig;
import io.microvibe.booster.txn.alipay.AbstractAlipayApiService;
import io.microvibe.booster.txn.alipay.AlipayTxnCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 统一收单交易支付接口
 *
 * @author Qt
 * @since Aug 26, 2018
 */
@Component
@ApiName(AlipayTxnCode.PAY_ALIPAY_TRADE_PAY)
@Slf4j
public class Alipay0101 extends AbstractAlipayApiService {

	@Override
	public ResponseData doExecute(RequestData RequestData) {
		try {
			BodyModel requestBody = RequestData.getBody();

			PayInput payInput = requestBody.toJavaObject(PayInput.class);

			Assertion.isNotEmpty("缺少商户订单号", payInput.getOut_trade_no());
			Assertion.isNotEmpty("缺少支付场景", payInput.getScene());
			Assertion.isNotEmpty("缺少支付授权码", payInput.getAuth_code());
//			Assertion.isNotEmpty("缺少订付款金额", payInput.getTotal_amount());
			Assertion.isNotEmpty("缺少订单名称", payInput.getSubject());
			Assertion.isNotEmpty("缺少收款支付宝用户ID", payInput.getSeller_id());
			Assertion.isNotEmpty("缺少用户付款中途退出返回商户网站的地址", payInput.getQuit_url());

			AlipayConfig alipayConfig = AlipayConfig.get();
			String appAuthToken = AlipayAppConfig.currentAppAuthToken();
			AlipayClient alipayClient = alipayService.createAlipayClient(alipayConfig);
			AlipayTradePayRequest request = new AlipayTradePayRequest();

			request.setReturnUrl(alipayConfig.return_url);
			request.setNotifyUrl(alipayConfig.notify_url);
			request.setBizContent(JSONObject.toJSONString(payInput));

			AlipayTradePayResponse response = alipayClient.execute(request, null, appAuthToken);
			if (log.isInfoEnabled()) {
				log.info("response: {}", JSONObject.toJSONString(response));
			}
			if (!response.isSuccess()) {
				throw new ApiException(response.getCode() + ":" + response.getMsg());
			}
			log.info("out_trade_no: {}, trade_no: {}", response.getOutTradeNo(), response.getTradeNo());

			ResponseData ResponseData = RequestData.buildResponse();
			ResponseData.setBody("alipayResponse", response);
			return ResponseData;
		} catch (AlipayApiException e) {
			throw new ApiException(e);
		}
	}
}
