package io.microvibe.booster.txn.alipay.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import io.microvibe.booster.core.api.ApiException;
import io.microvibe.booster.core.api.annotation.ApiName;
import io.microvibe.booster.core.api.model.BodyModel;
import io.microvibe.booster.core.api.model.RequestData;
import io.microvibe.booster.core.api.model.ResponseData;
import io.microvibe.booster.pay.alipay.bean.PayInput;
import io.microvibe.booster.pay.alipay.config.AlipayAppConfig;
import io.microvibe.booster.pay.alipay.config.AlipayConfig;
import io.microvibe.booster.txn.alipay.AbstractAlipayApiService;
import io.microvibe.booster.txn.alipay.AlipayTxnCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * alipay.trade.app.pay(app支付接口2.0)
 * <p>
 * 外部商户APP唤起快捷SDK创建订单并支付
 *
 * @author Qt
 * @since Aug 26, 2018
 */
@Component
@ApiName(AlipayTxnCode.PAY_ALIPAY_TRADE_APP_PAY)
@Slf4j
public class Alipay0105 extends AbstractAlipayApiService {

	@Override
	public ResponseData doExecute(RequestData requestData) {
		try {
			BodyModel requestBody = requestData.getBody();

			PayInput payInput = requestBody.toJavaObject(PayInput.class);

			AlipayConfig alipayConfig = AlipayConfig.get();
			String appAuthToken = AlipayAppConfig.currentAppAuthToken();
			AlipayClient alipayClient = alipayService.createAlipayClient(alipayConfig);
			AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();

			request.setReturnUrl(alipayConfig.return_url);
			request.setNotifyUrl(alipayConfig.notify_url);
			request.setBizContent(JSONObject.toJSONString(payInput));

			AlipayTradeAppPayResponse response = alipayClient.execute(request, null, appAuthToken);
			if (log.isInfoEnabled()) {
				log.info("response: {}", JSONObject.toJSONString(response));
			}
			if (!response.isSuccess()) {
				throw new ApiException(response.getCode() + ":" + response.getMsg());
			}
			log.info("out_trade_no: {}, trade_no: {}", response.getOutTradeNo(), response.getTradeNo());
			log.info("total_amount: {}, seller_id: {}", response.getTotalAmount(), response.getSellerId());

			ResponseData responseData = requestData.buildResponse();
			responseData.setBody("alipayResponse", response);
			return responseData;
		} catch (AlipayApiException e) {
			throw new ApiException(e);
		}
	}
}
