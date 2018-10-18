package io.microvibe.booster.commons.cache;

import io.microvibe.booster.commons.cache.impl.RedisExpireableCache;
import io.microvibe.booster.commons.cache.impl.RedisPermanentCache;
import io.microvibe.booster.commons.redis.RedisContext;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.AbstractCacheManager;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RedisNativeCacheManager extends AbstractCacheManager implements CacheManager {

	private RedisContext redisContext;
	private Collection<? extends RedisNativeCache> caches;
	private long defaultExpiration = 3600 * 4;//默认过期时间
	private Map<String, Long> expires = null;

	public void setRedisContext(RedisContext redisContext) {
		this.redisContext = redisContext;
	}

	public void setCaches(Collection<? extends RedisNativeCache> caches) {
		this.caches = caches;
	}

	@Override
	protected Collection<? extends RedisNativeCache> loadCaches() {
		if (redisContext != null) {
			if (caches != null) {
				return caches;
			}
		}
		// cannot connect redis
		return Collections.emptySet();
	}

	@Override
	public RedisNativeCache getCache(String name) {
		return (RedisNativeCache) super.getCache(name);
	}

	@Override
	protected RedisNativeCache getMissingCache(String name) {
		if (redisContext != null) {
			int expireTime = (int) defaultExpiration;
			if (this.expires != null) {
				Long expire = this.expires.get(name);
				if (expire != null && expire.intValue() >= 0) {
					expireTime = expire.intValue();
				}
			}
			if (expireTime > 0) {
				RedisExpireableCache cache = new RedisExpireableCache();
				cache.setName(name);
				cache.setRedisContext(redisContext);
				cache.setExpireTime(expireTime);
				cache.afterPropertiesSet();
				return RedisNativeCacheWrapper.wrap(cache);
			} else {
				RedisPermanentCache cache = new RedisPermanentCache();
				cache.setName(name);
				cache.setRedisContext(redisContext);
				cache.afterPropertiesSet();
				return RedisNativeCacheWrapper.wrap(cache);
			}
		}
		// cannot connect redis
		return null;
	}

	public long getDefaultExpiration() {
		return defaultExpiration;
	}

	public Map<String, Long> getExpires() {
		return expires;
	}

	public void setDefaultExpiration(long defaultExpireTime) {
		this.defaultExpiration = defaultExpireTime;
	}

	public void setExpires(Map<String, Long> expires) {
		this.expires = (expires != null ? new ConcurrentHashMap<String, Long>(expires) : null);
	}

	public void addExpires(String cacheName, Long expire) {
		if (this.expires == null) {
			this.expires = new ConcurrentHashMap<String, Long>();
		}
		this.expires.put(cacheName, expire);
	}

	public void addExpires(Map<String, Long> expires) {
		if (this.expires == null) {
			this.expires = new ConcurrentHashMap<String, Long>(expires);
		} else {
			this.expires.putAll(expires);
		}
	}
}
