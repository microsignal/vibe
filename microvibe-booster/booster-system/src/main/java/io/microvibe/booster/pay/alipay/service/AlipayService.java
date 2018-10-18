package io.microvibe.booster.pay.alipay.service;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import io.microvibe.booster.commons.utils.StringUtils;
import io.microvibe.booster.core.api.ApiException;
import io.microvibe.booster.core.api.tools.Assertion;
import io.microvibe.booster.pay.alipay.bean.PayInput;
import io.microvibe.booster.pay.alipay.config.AlipayAppConfig;
import io.microvibe.booster.pay.alipay.config.AlipayConfig;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author Qt
 * @since Aug 16, 2018
 */
@SuppressWarnings("All")
@Service
public class AlipayService {

	/**
	 * 创建 AlipayClient
	 *
	 * @param alipayConfig
	 * @return
	 */
	public AlipayClient createAlipayClient(AlipayConfig alipayConfig) {
		//获得初始化的AlipayClient
		return new DefaultAlipayClient(
			alipayConfig.gatewayUrl, alipayConfig.app_id, alipayConfig.merchant_private_key,
			"json", alipayConfig.charset, alipayConfig.alipay_public_key, alipayConfig.sign_type);
	}

	/**
	 * 创建 AlipayClient
	 *
	 * @return
	 */
	public AlipayClient createAlipayClient() {
		AlipayConfig alipayConfig = AlipayConfig.get();

		//获得初始化的AlipayClient
		return new DefaultAlipayClient(
			alipayConfig.gatewayUrl, alipayConfig.app_id, alipayConfig.merchant_private_key,
			"json", alipayConfig.charset, alipayConfig.alipay_public_key, alipayConfig.sign_type);
	}


	/**
	 * WEB支付
	 * <ol>
	 * <li>request.param.out_trade_no 商户订单号
	 * </li>
	 * <li>request.param.subject      商品名称
	 * </li>
	 * <li>request.param.total_amount 付款金额
	 * </li>
	 * <li>request.param.body         商品描述
	 * </li>
	 * </ol>
	 *
	 * @param request
	 * @return
	 * @throws AlipayApiException
	 * @throws IOException
	 */
	public AlipayTradePagePayResponse doTradePagePay(HttpServletRequest request) throws AlipayApiException, IOException {
		PayInput payInput = new PayInput();
		//商户订单号，商户网站订单系统中唯一订单号，必填
		payInput.setOut_trade_no(new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8"));
		//订单名称，必填
		payInput.setSubject(new String(request.getParameter("subject").getBytes("ISO-8859-1"), "UTF-8"));
		//付款金额，必填
		payInput.setTotal_amount(new String(request.getParameter("total_amount").getBytes("ISO-8859-1"), "UTF-8"));
		//商品描述，可空
		payInput.setBody(new String(request.getParameter("body").getBytes("ISO-8859-1"), "UTF-8"));
		return doTradePagePay(payInput);
	}

	/**
	 * WEB支付
	 *
	 * @param out_trade_no 商户订单号
	 * @param subject      商品名称
	 * @param total_amount 付款金额
	 * @param body         商品描述
	 * @return
	 * @throws AlipayApiException
	 */
	public AlipayTradePagePayResponse doTradePagePay(String out_trade_no, String subject, String total_amount, String body) throws AlipayApiException {
		PayInput payInput = new PayInput();
		payInput.setOut_trade_no(out_trade_no);//商户订单号，商户网站订单系统中唯一订单号，必填
		payInput.setSubject(subject);//订单名称，必填
		payInput.setTotal_amount(total_amount);  //付款金额，必填
		payInput.setBody(body);  //商品描述，可空
		return doTradePagePay(payInput);
	}

	/**
	 * WEB支付
	 *
	 * @param payInput
	 * @return
	 * @throws AlipayApiException
	 */
	public AlipayTradePagePayResponse doTradePagePay(PayInput payInput) throws AlipayApiException {
		AlipayConfig alipayConfig = AlipayConfig.get();
		AlipayClient alipayClient = createAlipayClient(alipayConfig);

		//设置请求参数
		AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
		alipayRequest.setReturnUrl(alipayConfig.return_url);
		alipayRequest.setNotifyUrl(alipayConfig.notify_url);

		Assertion.isNotBlank("缺少商户订单号", payInput.getOut_trade_no());
		Assertion.isNotBlank("缺少订付款金额", payInput.getTotal_amount());
		Assertion.isNotBlank("缺少订单名称", payInput.getSubject());

		alipayRequest.setBizContent("{\"out_trade_no\":\"" + payInput.getOut_trade_no() + "\","
			+ "\"total_amount\":\"" + payInput.getTotal_amount() + "\","
			+ "\"subject\":\"" + payInput.getSubject() + "\","
			+ "\"body\":\"" + payInput.getBody() + "\","
			+ "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

		//请求
		AlipayTradePagePayResponse payResponse = alipayClient.pageExecute(alipayRequest);
		return payResponse;
	}

	private JSONObject createJsonBy(String out_trade_no, String trade_no) {
		JSONObject json = new JSONObject();
		// 二选一
		if (StringUtils.isNotBlank(out_trade_no)) {
			json.put("out_trade_no", out_trade_no);
		} else if (StringUtils.isNotBlank(trade_no)) {
			json.put("trade_no", trade_no);
		} else {
			throw new ApiException("缺少商户订单号或支付宝交易号");
		}
		return json;
	}


	/**
	 * 交易查询
	 *
	 * @param out_trade_no 商户订单号，商户网站订单系统中唯一订单号
	 * @param trade_no     支付宝交易号
	 */
	public AlipayTradeQueryResponse doTradeQuery(String out_trade_no, String trade_no) throws AlipayApiException {
		AlipayClient alipayClient = createAlipayClient();

		//设置请求参数
		AlipayTradeQueryRequest alipayRequest = new AlipayTradeQueryRequest();

		JSONObject json = createJsonBy(out_trade_no, trade_no);

		alipayRequest.setBizContent(json.toJSONString());


		//请求
		AlipayTradeQueryResponse response = alipayClient.execute(alipayRequest
			,null,AlipayAppConfig.currentAppAuthToken());
		return response;
	}

	/**
	 * 交易关闭
	 *
	 * @param refund_amount 需要退款的金额，该金额不能大于订单金额，必填
	 * @param trade_no      支付宝交易号(二选一)
	 * @return
	 */
	public AlipayTradeCloseResponse doTradeClose(String out_trade_no, String trade_no) throws AlipayApiException {
		AlipayClient alipayClient = createAlipayClient();

		//设置请求参数
		AlipayTradeCloseRequest alipayRequest = new AlipayTradeCloseRequest();

		JSONObject json = createJsonBy(out_trade_no, trade_no);

		alipayRequest.setBizContent(json.toJSONString());

		//请求
		AlipayTradeCloseResponse response = alipayClient.execute(alipayRequest
			,null,AlipayAppConfig.currentAppAuthToken());
		return response;
	}

	/**
	 * 退款
	 *
	 * @param out_trade_no   商户订单号(二选一)，商户网站订单系统中唯一订单号
	 * @param trade_no       支付宝交易号(二选一)
	 * @param refund_amount  需要退款的金额，该金额不能大于订单金额，必填
	 * @param refund_reason  退款的原因说明
	 * @param out_request_no 标识一次退款请求，同一笔交易多次退款需要保证唯一，如需部分退款，则此参数必传
	 * @return
	 * @throws AlipayApiException
	 */
	public AlipayTradeRefundResponse doTradeRefund(String out_trade_no, String trade_no, String refund_amount, String refund_reason, String out_request_no) throws AlipayApiException {
		AlipayClient alipayClient = createAlipayClient();

		//设置请求参数
		AlipayTradeRefundRequest alipayRequest = new AlipayTradeRefundRequest();

		JSONObject json = createJsonBy(out_trade_no, trade_no);
		if (StringUtils.isBlank(refund_amount)) {
			throw new ApiException("缺少需要退款的金额");
		}

		json.put("refund_amount", refund_amount);
		json.put("refund_reason", refund_reason);
		json.put("out_request_no", out_request_no);

		alipayRequest.setBizContent(json.toJSONString());

		//请求
		AlipayTradeRefundResponse response = alipayClient.execute(alipayRequest
			,null,AlipayAppConfig.currentAppAuthToken());
		return response;
	}


	/**
	 * 退款查询
	 *
	 * @param out_trade_no   商户订单号(二选一)，商户网站订单系统中唯一订单号
	 * @param trade_no       支付宝交易号(二选一)
	 * @param out_request_no 退款请求号, 请求退款接口时，传入的退款请求号，如果在退款请求时未传入，则该值为创建交易时的外部交易号，必填
	 * @return
	 */
	public AlipayTradeFastpayRefundQueryResponse doTradeRefundQuery(String out_trade_no, String trade_no, String out_request_no) throws AlipayApiException {
		AlipayClient alipayClient = createAlipayClient();

		//设置请求参数
		AlipayTradeFastpayRefundQueryRequest alipayRequest = new AlipayTradeFastpayRefundQueryRequest();
		JSONObject json = createJsonBy(out_trade_no, trade_no);

		if (StringUtils.isBlank(out_request_no)) {
			throw new ApiException("缺少退款请求号");
		}

		json.put("out_request_no", out_request_no);

		alipayRequest.setBizContent(json.toJSONString());

		//请求
		AlipayTradeFastpayRefundQueryResponse result = alipayClient.execute(alipayRequest
			,null,AlipayAppConfig.currentAppAuthToken());
		return result;
	}

}
