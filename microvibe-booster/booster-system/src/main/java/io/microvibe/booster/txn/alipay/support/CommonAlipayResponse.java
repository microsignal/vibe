package io.microvibe.booster.txn.alipay.support;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayResponse;
import com.alipay.api.internal.mapping.ApiField;
import lombok.Data;

/**
 * @author Qt
 * @since Aug 28, 2018
 */
@Data
public class CommonAlipayResponse extends AlipayResponse {

	/**
	 * 商家订单号
	 */
	@ApiField("out_trade_no")
	private String outTradeNo;

	/**
	 * 支付宝交易号
	 */
	@ApiField("trade_no")
	private String tradeNo;

	/**
	 * 交易状态：WAIT_BUYER_PAY（交易创建，等待买家付款）、TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）、TRADE_SUCCESS（交易支付成功）、TRADE_FINISHED（交易结束，不可退款）
	 */
	@ApiField("trade_status")
	private String tradeStatus;

	/**
	 * 收款支付宝账号对应的支付宝唯一用户号。
	 * 以2088开头的纯16位数字
	 */
	@ApiField("seller_id")
	private String sellerId;

	/**
	 * 买家支付宝账号
	 */
	@ApiField("buyer_logon_id")
	private String buyerLogonId;
	/**
	 * 买家在支付宝的用户id
	 */
	@ApiField("buyer_user_id")
	private String buyerUserId;

	/**
	 * 买家用户类型。CORPORATE:企业用户；PRIVATE:个人用户。
	 */
	@ApiField("buyer_user_type")
	private String buyerUserType;


	/**
	 * 商户门店编号
	 */
	@ApiField("store_id")
	private String storeId;

	/**
	 * 请求交易支付中的商户店铺的名称
	 */
	@ApiField("store_name")
	private String storeName;

	/**
	 * 商户机具终端编号
	 */
	@ApiField("terminal_id")
	private String terminalId;

	/**
	 * 支付币种订单金额
	 */
	@ApiField("pay_amount")
	private String payAmount;

	/**
	 * 订单支付币种
	 */
	@ApiField("pay_currency")
	private String payCurrency;

	/**
	 * 交易的订单金额，单位为元，两位小数。该参数的值为支付时传入的total_amount
	 */
	@ApiField("total_amount")
	private String totalAmount;
	/**
	 * 标价币种，该参数的值为支付时传入的trans_currency，支持英镑：GBP、港币：HKD、美元：USD、新加坡元：SGD、日元：JPY、加拿大元：CAD、澳元：AUD、欧元：EUR、新西兰元：NZD、韩元：KRW、泰铢：THB、瑞士法郎：CHF、瑞典克朗：SEK、丹麦克朗：DKK、挪威克朗：NOK、马来西亚林吉特：MYR、印尼卢比：IDR、菲律宾比索：PHP、毛里求斯卢比：MUR、以色列新谢克尔：ILS、斯里兰卡卢比：LKR、俄罗斯卢布：RUB、阿联酋迪拉姆：AED、捷克克朗：CZK、南非兰特：ZAR、人民币：CNY、新台币：TWD。当trans_currency 和 settle_currency 不一致时，trans_currency支持人民币：CNY、新台币：TWD
	 */
	@ApiField("trans_currency")
	private String transCurrency;

	/**
	 * 标价币种兑换支付币种汇率
	 */
	@ApiField("trans_pay_rate")
	private String transPayRate;

	public JSONObject getBodyAsJson() {
		return (JSONObject) JSONObject.toJSON(getBody());
	}
}
