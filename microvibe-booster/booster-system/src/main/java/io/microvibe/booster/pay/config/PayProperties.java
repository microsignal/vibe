package io.microvibe.booster.pay.config;

import com.esotericsoftware.yamlbeans.YamlReader;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStreamReader;

/**
 * @author Qt
 * @since Aug 14, 2018
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Data
public class PayProperties {

	private static PayProperties staticInstance;

	static {
		initStatic();
	}

	private Alipay alipay;

	private static void initStatic() {
		try {
			YamlReader reader = new YamlReader(
				new InputStreamReader(PayProperties.class.getClassLoader()
					.getResourceAsStream("config/pay/pay.yml")));
			PayProperties.staticInstance = reader.read(PayProperties.class);
			reader.close();
		} catch (Exception e) {
			log.error("Load pay.yml error!", e);
		}
	}


	public static PayProperties getStatic() {
		if (staticInstance == null) {
			synchronized (PayProperties.class) {
				if (staticInstance == null) {
					initStatic();
				}
			}
		}
		return staticInstance;
	}

	@Data
	public static class Alipay {

		private String oauth2Url;
		private String oauth2CallbackUrl;
		private String oauth2ResultUrl;

		private String gateway;
		private String appId;
		private String partner;
		private String sellerId;
		private String merchantPrivateKey;
		private String alipayPublicKey;
		private String notifyUrl;
		private String returnUrl;
		private String signType;
		private String logPath;
		private String inputCharset;
		private String antiPhishingKey;
		private String exterInvokeIp;
	}

	@Data
	public static class Wechat {

		private String key;
		private String appid;
		private String mchid;
		private String submchid;
		private String certLocalPath;
		private String certPassword;
		private String useThreadToDoReport;
		private String ip;
		private String notifyUrl;
	}
}
