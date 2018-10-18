package io.microvibe.booster.txn.alipay.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.response.AlipayTradeRefundResponse;
import io.microvibe.booster.core.api.ApiException;
import io.microvibe.booster.core.api.annotation.ApiName;
import io.microvibe.booster.core.api.model.BodyModel;
import io.microvibe.booster.core.api.model.RequestData;
import io.microvibe.booster.core.api.model.ResponseData;
import io.microvibe.booster.txn.alipay.AbstractAlipayApiService;
import io.microvibe.booster.txn.alipay.AlipayTxnCode;
import org.springframework.stereotype.Component;

/**
 * 退款
 *
 * @author Qt
 * @since Aug 26, 2018
 */
@Component
@ApiName(AlipayTxnCode.PAY_ALIPAY_TRADE_REFUND)
public class Alipay0102 extends AbstractAlipayApiService {

	/**
	 * <pre>
	 * requestData.body.out_trade_no   商户订单号(二选一)，商户网站订单系统中唯一订单号
	 * requestData.body.trade_no       支付宝交易号(二选一)
	 * requestData.body.refund_amount  需要退款的金额，该金额不能大于订单金额，必填
	 * requestData.body.refund_reason  退款的原因说明
	 * requestData.body.out_request_no 标识一次退款请求，同一笔交易多次退款需要保证唯一，如需部分退款，则此参数必传
	 * </pre>
	 *
	 * @param requestData 请求报文
	 * @return
	 */
	@Override
	public ResponseData doExecute(RequestData requestData) {
		try {
			BodyModel requestBody = requestData.getBody();
			String out_trade_no = requestBody.getString("out_trade_no");
			String trade_no = requestBody.getString("trade_no");
			String refund_amount = requestBody.getString("refund_amount");
			String refund_reason = requestBody.getString("refund_reason");
			String out_request_no = requestBody.getString("out_request_no");

			AlipayTradeRefundResponse alipayResponse = alipayService.doTradeRefund(out_trade_no, trade_no, refund_amount,
				refund_reason, out_request_no);

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
