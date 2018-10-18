package io.microvibe.booster.pay.alipay.controller;

import com.alipay.api.AlipayApiException;
import io.microvibe.booster.pay.alipay.service.AlipayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Qt
 * @since Aug 16, 2018
 */
@RestController
@RequestMapping("/api/pay/alipay")
public class AlipayApiController {

	@Autowired
	private AlipayService alipayService;

	/**
	 * 支付宝即时到账交易接口
	 *
	 * @param out_trade_no 商户订单号
	 * @param subject      商品名称
	 * @param total_amount 付款金额
	 * @param body         商品描述
	 * @throws IOException
	 */
	@PostMapping(value = "/web/pay")
	public void web_pay(HttpServletRequest request, HttpServletResponse response,
		String out_trade_no, String subject, String total_amount, String body
	) throws IOException, AlipayApiException {
		String sHtmlText = alipayService.doTradePagePay(out_trade_no, subject, total_amount, body).getBody();
		response.setHeader("Content-Type", "text/html;charset=UTF-8");
		response.getWriter().println(sHtmlText);
	}

	/**
	 * 交易查询
	 *
	 * @param out_trade_no 商户订单号
	 * @param trade_no     支付宝交易号
	 */
	@PostMapping("/web/trade/query")
	public String web_trade_query(HttpServletRequest request, HttpServletResponse response,
		String out_trade_no, String trade_no) throws AlipayApiException {
		String result = alipayService.doTradeQuery(out_trade_no, trade_no).getBody();
		return result;
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
	@PostMapping("/web/trade/refund")
	public String web_trade_refund(HttpServletRequest request, HttpServletResponse response,
		String out_trade_no, String trade_no, String refund_amount, String refund_reason,
		String out_request_no
	) throws AlipayApiException {
		return alipayService.doTradeRefund(out_trade_no, trade_no, refund_amount,
			refund_reason, out_request_no).getBody();
	}

	/**
	 * 退款查询
	 *
	 * @param out_trade_no   商户订单号(二选一)，商户网站订单系统中唯一订单号
	 * @param trade_no       支付宝交易号(二选一)
	 * @param out_request_no 请求退款接口时，传入的退款请求号，如果在退款请求时未传入，则该值为创建交易时的外部交易号，必填
	 * @return
	 * @throws AlipayApiException
	 */
	@PostMapping("/web/trade/refund/query")
	public String web_trade_refund_query(HttpServletRequest request, HttpServletResponse response,
		String out_trade_no, String trade_no, String out_request_no
	) throws AlipayApiException {
		return alipayService.doTradeRefundQuery(out_trade_no, trade_no, out_request_no).getBody();
	}

	/**
	 * 交易关闭
	 *
	 * @param out_trade_no 商户订单号(二选一)，商户网站订单系统中唯一订单号
	 * @param trade_no     支付宝交易号(二选一)
	 * @return
	 * @throws AlipayApiException
	 */
	@PostMapping("/web/trade/close")
	public String web_trade_close(HttpServletRequest request, HttpServletResponse response,
		String out_trade_no, String trade_no
	) throws AlipayApiException {
		return alipayService.doTradeClose(out_trade_no, trade_no).getBody();
	}

}
