package io.microvibe.booster.pay.alipay.controller;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import io.microvibe.booster.core.api.model.RequestData;
import io.microvibe.booster.core.api.model.ResponseData;
import io.microvibe.booster.core.api.support.ApiServiceSupports;
import io.microvibe.booster.core.api.tools.DataKit;
import io.microvibe.booster.pay.alipay.bean.AsyncNotify;
import io.microvibe.booster.pay.alipay.bean.SyncReturn;
import io.microvibe.booster.pay.alipay.config.AlipayConfig;
import io.microvibe.booster.pay.alipay.service.AlipayService;
import io.microvibe.booster.txn.alipay.AlipayTxnCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Qt
 * @since Aug 16, 2018
 */
@SuppressWarnings("All")
@RestController
@RequestMapping("/openapi/pay/alipay")
@Slf4j
public class AlipayOpenapiController {

	@Autowired
	private AlipayService alipayService;


	/**
	 * 用户授权接口
	 *
	 * <pre>
	 * URL拼接与scope详解
	 *
	 * url拼接规则：https://openauth.alipay.com/oauth2/publicAppAuthorize.htm?app_id=APPID&scope=SCOPE&redirect_uri=ENCODED_URL
	 *
	 * 1. app_id:             开发者应用的app_id； 相同支付宝账号下，不同的app_id获取的token切忌混用。
	 * 2. scope:              接口权限值，目前只支持 auth_user（获取用户信息、网站支付宝登录）、
	 *                        auth_base（用户信息授权）、auth_ecard（商户会员卡）、
	 *                        auth_invoice_info（支付宝闪电开票）、auth_puc_charge（生活缴费）五个值;
	 *                        多个scope时用”,”分隔，如scope为”auth_user,auth_ecard”时，此时获取到的access_token，
	 *                        既可以用来获取用户信息，又可以给用户发送会员卡。
	 * 3. redirect_uri        授权回调地址，是经过URLENCODE转义 的url链接（url必须以http或者https开头）；
	 *                        在请求之前，开发者需要先到开发者中心对应应用内，配置授权回调地址。
	 *                        redirect_uri与应用配置的授权回调地址域名部分必须一致。
	 *
	 * 4. state               商户自定义参数，用户授权后，重定向到redirect_uri时会原样回传给商户。
	 *                        为防止CSRF攻击，建议开发者请求授权时传入state参数，该参数要做到既不可预测，
	 *                        又可以证明客户端和当前第三方网站的登录认证状态存在关联。
	 *
	 * </pre>
	 * <pre>
	 * 使用方法:
	 * http://.../openapi/pay/alipay/oauth2?scope=auth_user,auth_base&redirect=http%3A%2F%2Fwww.baidu.com
	 * </pre>
	 *
	 * @return
	 */
	@RequestMapping("/oauth2")
	@ResponseBody
	public void oauth2_authorize(HttpServletRequest request, HttpServletResponse response) throws IOException {
		RequestData requestData = DataKit.buildRequest(AlipayTxnCode.PAY_ALIPAY_OAUTH2_PUBLIC, request);
		ResponseData responseData = ApiServiceSupports.doExecute(requestData);
		String redirect = responseData.getBody("redirect", String.class);
		String location = responseData.getBody("location", String.class);
		String app_id = responseData.getBody("app_id", String.class);
		String scope = responseData.getBody("scope", String.class);
		String redirect_uri = responseData.getBody("redirect_uri", String.class);
		String state = responseData.getBody("state", String.class);
		response.setHeader("Content-Type", "text/html;charset=UTF-8");
		StringBuilder sHtmlText = new StringBuilder();
		sHtmlText.append("<form id=\"redirect\" name=\"redirect\" action=\"" + location + "\" method=\"GET\">");
		sHtmlText.append("<input type=\"hidden\" name=\"app_id\" value=\"" + app_id + "\">");
		sHtmlText.append("<input type=\"hidden\" name=\"scope\" value=\"" + scope + "\">");
		sHtmlText.append("<input type=\"hidden\" name=\"redirect_uri\" value=\"" + redirect_uri + "\">");
		sHtmlText.append("<input type=\"hidden\" name=\"state\" value=\"" + state + "\">");
		sHtmlText.append("</form>");
		sHtmlText.append("<script>document.forms['redirect'].submit();</script>");
		response.getWriter().println(sHtmlText.toString());
	}

	@RequestMapping("/oauth2_result")
	@ResponseBody
	public void oauth2_result(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// FIXME 首页
		String location = "/index.html";
		response.setHeader("Content-Type", "text/html;charset=UTF-8");
		StringBuilder sHtmlText = new StringBuilder();
		sHtmlText.append("<form id=\"redirect\" name=\"redirect\" action=\"" + location + "\" method=\"GET\">");
		sHtmlText.append("</form>");
		sHtmlText.append("<script>document.forms['redirect'].submit();</script>");
		response.getWriter().println(sHtmlText.toString());
	}

	@RequestMapping("/oauth2_callback")
	@ResponseBody
	public void oauth2_callback(HttpServletRequest request, HttpServletResponse response) throws IOException {
		RequestData requestData = DataKit.buildRequest(AlipayTxnCode.PAY_ALIPAY_OAUTH2_CALLBACK, request);
		ResponseData responseData = ApiServiceSupports.doExecute(requestData);
		String redirect = responseData.getBody("redirect", String.class);
		response.setHeader("Content-Type", "text/html;charset=UTF-8");
		StringBuilder sHtmlText = new StringBuilder();
		sHtmlText.append("<form id=\"redirect\" name=\"redirect\" action=\"" + redirect + "\" method=\"GET\">");
		sHtmlText.append("</form>");
		sHtmlText.append("<script>document.forms['redirect'].submit();</script>");
		response.getWriter().println(sHtmlText.toString());
	}

	/**
	 * 支付宝页面跳转同步通知页面
	 *
	 * @param syncReturn
	 * @param request
	 * @return
	 */
	@GetMapping("/sync_return")
	public String sync_return(SyncReturn syncReturn, HttpServletRequest request) throws IOException, AlipayApiException {
		log.info("Alipay sync return: {}", syncReturn);
		//获取支付宝GET过来反馈信息
		Map<String, String> params = new HashMap<>();
		Map<String, String[]> requestParams = request.getParameterMap();
		for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
			String name = iter.next();
			String[] values = requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
					: valueStr + values[i] + ",";
			}
			// valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
			params.put(name, valueStr);
		}
		if (log.isInfoEnabled()) {
			log.info(JSONObject.toJSONString(params));
		}
		AlipayConfig alipayConfig = AlipayConfig.get();

		boolean signVerified = AlipaySignature.rsaCheckV1(params,
			alipayConfig.alipay_public_key, alipayConfig.charset, alipayConfig.sign_type); //调用SDK验证签名
		if (signVerified) {

			//判断该笔订单是否在商户网站中已经做过处理
			//如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
			//如果有做过处理，不执行商户的业务程序

			log.info("Verify sync notify success!");
			log.info("syncReturn: {}", syncReturn);
			return "同步通知页面暂不支持";// fixme
		} else {
			log.info("同步通知验证支付宝签名失败!");
			return "验证支付宝签名失败";
		}

	}

	@PostMapping("/aync_notify")
	public String aync_notify(AsyncNotify asyncNotify, HttpServletRequest request) throws IOException, AlipayApiException {
		log.info("Alipay aync notify: {}", asyncNotify);
		Map<String, String> params = new HashMap<>();
		Map<String, String[]> requestParams = request.getParameterMap();
		for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
			String name = iter.next();
			String[] values = requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
					: valueStr + values[i] + ",";
			}
			// 异步通知时不需要解决乱码问题,否则反而会乱
			params.put(name, valueStr);
		}
		if (log.isInfoEnabled()) {
			log.info(JSONObject.toJSONString(params));
		}
		AlipayConfig alipayConfig = AlipayConfig.get();

		/* 实际验证过程建议商户务必添加以下校验：
		1、需要验证该通知数据中的out_trade_no是否为商户系统中创建的订单号，
		2、判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额），
		3、校验通知中的seller_id（或者seller_email) 是否为out_trade_no这笔单据的对应的操作方（有的时候，一个商户可能有多个seller_id/seller_email）
		4、验证app_id是否为该商户本身。
		*/
		boolean signVerified = AlipaySignature.rsaCheckV1(params,
			alipayConfig.alipay_public_key, alipayConfig.charset, alipayConfig.sign_type); //调用SDK验证签名
		if (signVerified) {

			if (asyncNotify.getTrade_status().equals("TRADE_FINISHED")) {
				//判断该笔订单是否在商户网站中已经做过处理
				//如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
				//请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
				//如果有做过处理，不执行商户的业务程序

				//注意：
				//退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知

				// fixme 更新为交易终结, 不可退款

			} else if (asyncNotify.getTrade_status().equals("TRADE_SUCCESS")) {
				//判断该笔订单是否在商户网站中已经做过处理
				//如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
				//请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
				//如果有做过处理，不执行商户的业务程序

				//注意：
				//付款完成后，支付宝系统发送该交易状态通知

				// fixme 变更订单状态
			}

			log.info("异步通知验证支付宝签名成功");
			return "success";
		} else {
			log.info("异步通知验证支付宝签名失败!");
			return "fail";
		}
	}

}
