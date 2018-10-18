package io.microvibe.booster.pay.alipay.config;

import io.microvibe.booster.commons.spring.ApplicationContextHolder;
import io.microvibe.booster.pay.alipay.entity.PayAlipayConfig;
import io.microvibe.booster.pay.alipay.service.PayAlipayConfigService;
import io.microvibe.booster.pay.config.PayProperties;
import lombok.Data;

/**
 * 功能：基础配置类<br>
 * 详细：设置帐户有关信息及返回路径<br>
 *
 * @author Qt
 * @since Aug 14, 2018
 */
@Data
public class AlipayConfig {
	private static final ThreadLocal<AlipayConfig> LOCAL = ThreadLocal.withInitial(() -> getStatic());

	// 数据库主配置ID
	public Long config_id = 0L;

	// region 基本信息

	// 开放授权
	// oauth2: https://openauth.alipay.com/oauth2
	public String oauth2Url = "https://openauth.alipay.com/oauth2";
	// 开放授权回调
	public String oauth2CallbackUrl;
	// 开放授权回调之后的默认跳转页面
	public String oauth2ResultUrl;

	// 支付宝网关
	public String gatewayUrl = "https://openapi.alipay.com/gateway.do";
	public String log_path = "/data/log/alipay";
	// 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
	public String app_id;
	// 商户私钥，您的PKCS8格式RSA2私钥, RSA公私钥生成：https://doc.open.alipay.com/doc2/detail.htm?spm=a219a.7629140.0.0.nBDxfy&treeId=58&articleId=103242&docType=1
	public String merchant_private_key;
	// 合作身份者ID，签约账号，以2088开头由16位纯数字组成的字符串，查看地址：https://b.alipay.com/order/pidAndKey.htm
	public String partner;
	// 收款支付宝账号，以2088开头由16位纯数字组成的字符串，一般情况下收款账号就是签约账号
	public String seller_id;
	// 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
	public String alipay_public_key;
	// 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	public String notify_url;
	// 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	public String return_url;
	// 签名方式
	public String sign_type = "RSA2";
	// 字符编码格式
	public String charset = "utf-8";
	public String input_charset = "utf-8";
	// 支付类型 ，无需修改
	public String payment_type = "1";

	// endregion

	/*
	// region 防钓鱼信息

	// 防钓鱼时间戳  若要使用请调用类文件submit中的query_timestamp函数
	public String anti_phishing_key;
	// 客户端的IP地址 非局域网的外网IP地址，如：221.0.0.1
	public String exter_invoke_ip;

	// endregion
	*/

	/**
	 * 指定使用配置项<br>
	 * <p>
	 * fixme 需考虑加入缓存
	 *
	 * @param configIdentity
	 */
	public static AlipayConfig use(String configIdentity) {
		PayAlipayConfigService service = ApplicationContextHolder.getBean(PayAlipayConfigService.class);
		PayAlipayConfig entity = service.getByIdentity(configIdentity);
		return use(entity);
	}

	private static AlipayConfig use(PayAlipayConfig entity) {
		if (entity == null) {
			throw new IllegalArgumentException("缺少配置项");
		}
		AlipayConfig config = new AlipayConfig();
		config.config_id = entity.getId();
		config.oauth2Url = entity.getOauth2Url();
		config.oauth2CallbackUrl = entity.getOauth2CallbackUrl();
		config.oauth2ResultUrl = entity.getOauth2ResultUrl();

		config.gatewayUrl = entity.getGatewayUrl();
		config.app_id = entity.getAppId();
		config.partner = entity.getPartner();
		config.seller_id = entity.getSellerId();
		config.merchant_private_key = entity.getMerchantPrivateKey();
		config.alipay_public_key = entity.getAlipayPublicKey();
		config.notify_url = entity.getNotifyUrl();
		config.return_url = entity.getReturnUrl();
		set(config);
		return config;
	}

	public static void set(AlipayConfig config) {
		LOCAL.set(config);
	}

	public static AlipayConfig get() {
		return LOCAL.get();
	}

	public static void clear() {
		LOCAL.remove();
	}

	public static AlipayConfig getStatic() {
		return Holder.config;
	}

	private static class Holder {
		static AlipayConfig config = new AlipayConfig();

		static {
			PayProperties.Alipay alipay = PayProperties.getStatic().getAlipay();
			config.config_id = 0L;
			config.oauth2Url = alipay.getOauth2Url();
			config.oauth2CallbackUrl = alipay.getOauth2CallbackUrl();
			config.oauth2ResultUrl = alipay.getOauth2ResultUrl();

			config.gatewayUrl = alipay.getGateway();
			config.app_id = alipay.getAppId();
			config.partner = alipay.getPartner();
			config.seller_id = alipay.getSellerId();
			config.merchant_private_key = alipay.getMerchantPrivateKey();
			config.alipay_public_key = alipay.getAlipayPublicKey();
			config.notify_url = alipay.getNotifyUrl();
			config.return_url = alipay.getReturnUrl();
			config.sign_type = alipay.getSignType();
			config.log_path = alipay.getLogPath();
			config.charset = alipay.getInputCharset();
			config.input_charset = alipay.getInputCharset();
			config.payment_type = "1";

			/*
			config.anti_phishing_key = alipay.getAntiPhishingKey();
			config.exter_invoke_ip = alipay.getExterInvokeIp();
			*/
		}
	}

}
