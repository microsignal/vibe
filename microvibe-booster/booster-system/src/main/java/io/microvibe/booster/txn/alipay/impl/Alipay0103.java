package io.microvibe.booster.txn.alipay.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.response.AlipayTradeQueryResponse;
import io.microvibe.booster.core.api.ApiException;
import io.microvibe.booster.core.api.annotation.ApiName;
import io.microvibe.booster.core.api.model.BodyModel;
import io.microvibe.booster.core.api.model.RequestData;
import io.microvibe.booster.core.api.model.ResponseData;
import io.microvibe.booster.txn.alipay.AbstractAlipayApiService;
import io.microvibe.booster.txn.alipay.AlipayTxnCode;
import org.springframework.stereotype.Component;


/**
 * alipay.trade.query(统一收单线下交易查询)
 *
 * @author Qt
 * @since Aug 26, 2018
 */
@Component
@ApiName(AlipayTxnCode.PAY_ALIPAY_TRADE_QUERY)
public class Alipay0103 extends AbstractAlipayApiService {

	@Override
	public ResponseData doExecute(RequestData requestData) {
		try {
			BodyModel requestBody = requestData.getBody();
			String out_trade_no = requestBody.getString("out_trade_no");
			String trade_no = requestBody.getString("trade_no");

			AlipayTradeQueryResponse alipayResponse = alipayService.doTradeQuery(out_trade_no, trade_no);

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
