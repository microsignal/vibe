package io.microvibe.booster.pay.alipay.bean;

import lombok.Data;

/**
 * @author Qt
 * @since Aug 16, 2018
 */
@Data
public class PayInput {
	//商户订单号，商户网站订单系统中唯一订单号，必填
	String out_trade_no;
	//付款金额，必填
	String total_amount;
	//订单名称，必填
	String subject;
	//商品描述，可空
	String body;

	// 支付场景
	//   条码支付，取值：bar_code
	//   声波支付，取值：wave_code
	String scene;
	// 支付授权码，25~30开头的长度为16~24位的数字，实际字符串长度以开发者获取的付款码长度为准
	String auth_code;

	// 收款支付宝用户ID。 如果该值为空，则默认为商户签约账号对应的支付宝用户ID
	String seller_id;
	// 用户付款中途退出返回商户网站的地址
	String quit_url;



}
