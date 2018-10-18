package io.microvibe.booster.core.accessor;

import io.microvibe.booster.commons.spring.ApplicationContextHolder;
import io.microvibe.booster.commons.spring.ApplicationContextHolder;
import io.microvibe.booster.core.env.CacheNames;
import io.microvibe.booster.core.env.ShiroEnv;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

public class CacheAccessor implements CacheNames {

	/**
	 * 获取缓存管理器
	 *
	 * @return 缓存管理器
	 */
	public static CacheManager getCacheManager() {
		return Holder.holder.cacheManager;
	}

	/**
	 * 根据缓存名获取缓存对象
	 *
	 * @param cacheName 缓存名
	 * @return 缓存对象
	 */
	public static Cache getCache(String cacheName) {
		return getCacheManager().getCache(cacheName);
	}

	/**
	 * 缓存shiro会话缓存
	 *
	 * @return
	 */
	public static Cache getShiroSessionCache() {
		return getCacheManager().getCache(Holder.holder.shiroEnv.getSessionCacheName());
	}

	public static Cache getShiroAuthcCache() {
		return getCacheManager().getCache(Holder.holder.shiroEnv.getAuthenticationCacheName());
	}

	public static Cache getShiroAuthzCache() {
		return getCacheManager().getCache(Holder.holder.shiroEnv.getAuthorizationCacheName());
	}

	public static Cache getApiOauthKeyCache() {
		return getCacheManager().getCache(API_OAUTH_KEY_CACHE_NAME);
	}

	public static Cache getApiOauthTokenCache() {
		return getCacheManager().getCache(API_OAUTH_TOKEN_CACHE_NAME);
	}

	public static Cache getApiAccessTokenCache() {
		return getCacheManager().getCache(API_ACCESS_TOKEN_CACHE_NAME);
	}

	public static Cache getApiAppKeyCache() {
		return getCacheManager().getCache(API_APP_KEY_CACHE_NAME);
	}

	public static Cache getSysUserAuthcCache() {
		return getCacheManager().getCache(SYS_USER_AUTHC_CACHE_NAME);
	}

	public static Cache getPasswordRetryCache() {
		return getCacheManager().getCache(PASSWORD_RETRY_CACHE_NAME);
	}

	public static Cache getCaptchaCache() {
		return getCacheManager().getCache(CAPTCHA_CACHE_NAME);
	}

	private static class Holder {
		private static Holder holder = new Holder();

		private CacheManager cacheManager;
		private ShiroEnv shiroEnv;

		private Holder() {
			cacheManager = ApplicationContextHolder.getBean(CacheManager.class);
			shiroEnv = ApplicationContextHolder.getBean(ShiroEnv.class);
		}
	}

}
