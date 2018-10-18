package io.microvibe.booster.txn.alipay;

/**
 * @author Qt
 * @since Aug 29, 2018
 */
public interface AlipayTxnCode {
	/**
	 * 通用的 `Alipay` 调用接口
	 */
	String PAY_ALIPAY_COMMON = "pay.alipay.common";
	/**
	 * 用户信息授权接口回调
	 */
	String PAY_ALIPAY_OAUTH2_CALLBACK = "pay.alipay.oauth2.callback";
	/**
	 * 用户信息授权接口
	 */
	String PAY_ALIPAY_OAUTH2_PUBLIC = "pay.alipay.oauth2.public";
	/**
	 * 第三方应用授权接口
	 */
	String PAY_ALIPAY_OAUTH2_APP = "pay.alipay.oauth2.app";
	/**
	 * 使用`app_auth_code`换取`app_auth_token`
	 */
	String PAY_ALIPAY_OPEN_AUTH_TOKEN_APP = "pay.alipay.open.auth.token.app";
	/**
	 * 查询 `app` 授权信息
	 */
	String PAY_ALIPAY_OPEN_AUTH_TOKEN_APP_QUERY = "pay.alipay.open.auth.token.app.query";
	/**
	 * 支付宝即时到账交易接口
	 */
	String PAY_ALIPAY_TRADE_PAGE_PAY = "pay.alipay.trade.page.pay";
	/**
	 * 统一收单交易支付接口
	 */
	String PAY_ALIPAY_TRADE_PAY = "pay.alipay.trade.pay";
	/**
	 * 退款
	 */
	String PAY_ALIPAY_TRADE_REFUND = "pay.alipay.trade.refund";
	/**
	 * 统一收单线下交易查询
	 */
	String PAY_ALIPAY_TRADE_QUERY = "pay.alipay.trade.query";
	/**
	 * 手机网站支付接口2.0
	 */
	String PAY_ALIPAY_TRADE_WAP_PAY = "pay.alipay.trade.wap.pay";
	/**
	 * app支付接口2.0
	 */
	String PAY_ALIPAY_TRADE_APP_PAY = "pay.alipay.trade.app.pay";
	/**
	 * 统一收单交易创建接口
	 */
	String PAY_ALIPAY_TRADE_CREATE = "pay.alipay.trade.create";
}
