package io.microvibe.booster.core.env;

import com.alibaba.fastjson.JSON;
import io.microvibe.booster.commons.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.cache.internal.StandardQueryCache;
import org.hibernate.cache.spi.UpdateTimestampsCache;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class CacheEnv implements InitializingBean {

	private static final String CACHE_EXPIRES_PROPERTIES = "cacheExpires.properties";
	private static final String DEFAULT_CACHE_NAME = "default";
	@Autowired
	ShiroEnv shiroEnv;

	@Value("${cache.default.expire:7200}")
	long defaultExpiration;// 3600 * 2

	private Map<String, Long> cacheExpires = new LinkedHashMap<>();

	public CacheEnv() {
		super();
	}

	public static void main(String[] args) {
		CacheEnv env = new CacheEnv();
		env.shiroEnv = new ShiroEnv();
		env.initStandard();
		env.initCustom();
		System.out.println(JSON.toJSONString(env.cacheExpires,true));
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		initStandard();
		initCustom();
	}

	private void initStandard() {
		cacheExpires.put(StandardQueryCache.class.getName(), Long.valueOf(3600 * 24));// 24h
		cacheExpires.put(UpdateTimestampsCache.class.getName(), Long.valueOf(3600 * 24));// 24h

		if(shiroEnv != null) {
			cacheExpires.put(shiroEnv.getSessionCacheName(), Long.valueOf(3600 * 24));// 24h
			cacheExpires.put(shiroEnv.getAuthenticationCacheName(), Long.valueOf(3600 * 24));// 24h
			cacheExpires.put(shiroEnv.getAuthorizationCacheName(), Long.valueOf(3600 * 24));// 24h
		}

		cacheExpires.put(CacheNames.API_OAUTH_KEY_CACHE_NAME, Long.valueOf(1200L));// 20min
		cacheExpires.put(CacheNames.API_OAUTH_TOKEN_CACHE_NAME, Long.valueOf(3600 * 2));// 2h
		cacheExpires.put(CacheNames.API_ACCESS_TOKEN_CACHE_NAME, Long.valueOf(3600 * 2));// 2h
		cacheExpires.put(CacheNames.API_APP_KEY_CACHE_NAME, Long.valueOf(0));// 0s

		cacheExpires.put(CacheNames.SYS_USER_AUTHC_CACHE_NAME, Long.valueOf(3600 * 12));// 12h

		cacheExpires.put(CacheNames.PASSWORD_RETRY_CACHE_NAME, Long.valueOf(600L));// 10min
		cacheExpires.put(CacheNames.CAPTCHA_CACHE_NAME, Long.valueOf(600L));
	}

	private void initCustom() {
		// load properties
		try {
			InputStream in = IOUtils.getInputStream(CACHE_EXPIRES_PROPERTIES);
			Properties props = new Properties();
			props.load(in);
			for (String key : props.stringPropertyNames()) {
				String val = StringUtils.trimToNull(props.getProperty(key));
				if (val != null) {
					try {
						cacheExpires.put(key, Long.valueOf(val));
					} catch (NumberFormatException e) {
					}
				}
			}
			Long expire = cacheExpires.get(DEFAULT_CACHE_NAME);
			if (expire != null) {
				defaultExpiration = expire.longValue();
			}
		} catch (IOException e) {
		}
	}

	public long getDefaultExpiration() {
		return defaultExpiration;
	}

	public Map<String, Long> getCacheExpires() {
		return Collections.unmodifiableMap(cacheExpires);
	}

}
