package io.microvibe.booster.pay.alipay.entity;

import io.microvibe.booster.pay.entity.BasePayEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Qt
 * @since Aug 30, 2018
 */
@Data
@Entity
@Table(name = "pay_alipay_config")
public class PayAlipayConfig extends BasePayEntity {

	private static final long serialVersionUID = 1L;

	// region 基本信息

	// 配置标识
	@Column(name = "identity")
	private String identity;

	// 开放授权
	@Column(name = "oauth2_url")
	private String oauth2Url;
	//开放授权回调
	@Column(name = "oauth2_callback_url")
	private String oauth2CallbackUrl;
	//开放授权回调之后的默认跳转页面
	@Column(name = "oauth2_result_url")
	private String oauth2ResultUrl;
	// 支付宝网关
	@Column(name = "gateway_url")
	private String gatewayUrl ;

	// 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
	@Column(name = "app_id")
	private String appId;
	// 商户私钥，您的PKCS8格式RSA2私钥, RSA公私钥生成：https://doc.open.alipay.com/doc2/detail.htm?spm=a219a.7629140.0.0.nBDxfy&treeId=58&articleId=103242&docType=1
	@Column(name = "merchant_private_key")
	private String merchantPrivateKey;

	// 合作身份者ID，签约账号，以2088开头由16位纯数字组成的字符串，查看地址：https://b.alipay.com/order/pidAndKey.htm
	@Column(name = "partner")
	private String partner;
	// 收款支付宝账号，以2088开头由16位纯数字组成的字符串，一般情况下收款账号就是签约账号
	@Column(name = "seller_id")
	private String sellerId;
	// 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
	@Column(name = "alipay_public_key")
	private String alipayPublicKey;
	// 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	@Column(name = "notify_url")
	private String notifyUrl;
	// 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	@Column(name = "return_url")
	private String returnUrl;

	@Column(name = "intro")
	private String intro;

	// endregion

}
