package io.microvibe.booster.pay.alipay.config;

import io.microvibe.booster.commons.spring.ApplicationContextHolder;
import io.microvibe.booster.pay.alipay.entity.PayAlipayAppConfig;
import io.microvibe.booster.pay.alipay.service.PayAlipayAppConfigService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Qt
 * @since Aug 28, 2018
 */
@Data
@Slf4j
public class AlipayAppConfig {

	private static final ThreadLocal<AlipayAppConfig> LOCAL = new ThreadLocal<>();
	private String appAuthToken;

	public static void set(AlipayAppConfig config) {
		LOCAL.set(config);
	}

	public static AlipayAppConfig get() {
		return LOCAL.get();
	}

	public static void clear() {
		LOCAL.remove();
	}

	public static String currentAppAuthToken() {
		AlipayAppConfig alipayAppConfig = get();
		if (alipayAppConfig == null) {
			return null;
		}
		String appAuthToken = alipayAppConfig.getAppAuthToken();
		return appAuthToken;
	}

	/**
	 * 指定使用配置项<br>
	 * <p>
	 * fixme 需考虑加入缓存
	 *
	 * @param authAppId
	 */
	public static void useAuthApp(String authAppId) {
		AlipayConfig alipayConfig = AlipayConfig.get();
		PayAlipayAppConfigService service = ApplicationContextHolder.getBean(PayAlipayAppConfigService.class);
		PayAlipayAppConfig entity = service.getByConfigIdAndAppId(alipayConfig.config_id, authAppId);
		use(entity);
	}

	/**
	 * 指定使用配置项<br>
	 * <p>
	 * fixme 需考虑加入缓存
	 *
	 * @param configIdentity
	 */
	public static AlipayAppConfig use(String configIdentity) {
		PayAlipayAppConfigService service = ApplicationContextHolder.getBean(PayAlipayAppConfigService.class);
		PayAlipayAppConfig entity = service.getByIdentity(configIdentity);
		return use(entity);
	}

	public static AlipayAppConfig use(PayAlipayAppConfig entity) {
		if (entity == null) {
			throw new IllegalArgumentException("缺少配置项");
		} else {
			AlipayAppConfig config = new AlipayAppConfig();
			config.setAppAuthToken(entity.getAppAuthToken());
			set(config);
			return config;
		}
	}
}
