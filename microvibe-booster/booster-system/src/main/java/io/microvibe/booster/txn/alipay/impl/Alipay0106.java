package io.microvibe.booster.txn.alipay.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeCreateRequest;
import com.alipay.api.response.AlipayTradeCreateResponse;
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
 * alipay.trade.create(统一收单交易创建接口)
 *
 * @author Qt
 * @since Aug 26, 2018
 */
@Component
@ApiName(AlipayTxnCode.PAY_ALIPAY_TRADE_CREATE)
@Slf4j
public class Alipay0106 extends AbstractAlipayApiService {

	@Override
	public ResponseData doExecute(RequestData requestData) {
		try {
			BodyModel requestBody = requestData.getBody();


			AlipayConfig alipayConfig = AlipayConfig.get();
			String appAuthToken = AlipayAppConfig.currentAppAuthToken();

			AlipayClient alipayClient = alipayService.createAlipayClient(alipayConfig);

			AlipayTradeCreateRequest request = new AlipayTradeCreateRequest();

			PayInput payInput = requestBody.toJavaObject(PayInput.class);

			Assertion.isNotBlank("缺少商户订单号", payInput.getOut_trade_no());
			Assertion.isNotBlank("缺少订付款金额", payInput.getTotal_amount());
			Assertion.isNotBlank("缺少订单名称", payInput.getSubject());


			request.setReturnUrl(alipayConfig.return_url);
			request.setNotifyUrl(alipayConfig.notify_url);
			request.setBizContent(JSONObject.toJSONString(payInput));

			AlipayTradeCreateResponse response = alipayClient.execute(request, null, appAuthToken);
			if (log.isInfoEnabled()) {
				log.info("response: {}", JSONObject.toJSONString(response));
			}
			if (!response.isSuccess()) {
				throw new ApiException(response.getCode() + ":" + response.getMsg());
			}
			log.info("out_trade_no: {}, trade_no: {}", response.getOutTradeNo(),response.getTradeNo());

			ResponseData responseData = requestData.buildResponse();
			responseData.setBody("alipayResponse", response);
			return responseData;
		} catch (AlipayApiException e) {
			throw new ApiException(e);
		}
	}
}
