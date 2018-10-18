package io.microvibe.booster.txn.alipay.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.response.AlipayTradePagePayResponse;
import io.microvibe.booster.core.api.ApiException;
import io.microvibe.booster.core.api.annotation.ApiName;
import io.microvibe.booster.core.api.model.BodyModel;
import io.microvibe.booster.core.api.model.RequestData;
import io.microvibe.booster.core.api.model.ResponseData;
import io.microvibe.booster.txn.alipay.AbstractAlipayApiService;
import io.microvibe.booster.txn.alipay.AlipayTxnCode;
import org.springframework.stereotype.Component;

/**
 * 支付宝即时到账交易接口
 *
 * @author Qt
 * @since Aug 26, 2018
 */
@Component
@ApiName(AlipayTxnCode.PAY_ALIPAY_TRADE_PAGE_PAY)
public class Alipay0100 extends AbstractAlipayApiService {

	@Override
	public ResponseData doExecute(RequestData requestData) {
		try {
			BodyModel requestBody = requestData.getBody();
			String out_trade_no = requestBody.getString("out_trade_no");
			String subject = requestBody.getString("subject");
			String total_amount = requestBody.getString("total_amount");
			String body = requestBody.getString("body");
			AlipayTradePagePayResponse alipayResponse = alipayService.doTradePagePay(out_trade_no, subject, total_amount, body);

			if (!alipayResponse.isSuccess()) {
				throw new ApiException(alipayResponse.getCode() + ":" + alipayResponse.getMsg());
			}
			ResponseData responseData = requestData.buildResponse();
			responseData.setBodyAsString(alipayResponse.getBody());
			return responseData;
		} catch (AlipayApiException e) {
			throw new ApiException(e);
		}
	}
}
