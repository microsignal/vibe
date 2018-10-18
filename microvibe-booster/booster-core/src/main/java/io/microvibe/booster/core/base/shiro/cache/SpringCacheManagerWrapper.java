package io.microvibe.booster.core.base.shiro.cache;

import io.microvibe.booster.commons.cache.RedisNativeCache;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.springframework.cache.support.SimpleValueWrapper;

import java.util.Collection;
import java.util.Set;

/**
 * 包装Spring cache抽象
 */
public class SpringCacheManagerWrapper implements CacheManager {

	private org.springframework.cache.CacheManager cacheManager;

	/**
	 * 设置spring cache manager
	 *
	 * @param cacheManager
	 */
	public void setCacheManager(org.springframework.cache.CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	@Override
	public Cache<Object, Object> getCache(String name) throws CacheException {
		org.springframework.cache.Cache springCache = cacheManager.getCache(name);
		return new SpringCacheWrapper(springCache);
	}

	static class SpringCacheWrapper implements Cache<Object, Object> {
		private org.springframework.cache.Cache springCache;

		SpringCacheWrapper(org.springframework.cache.Cache springCache) {
			this.springCache = springCache;
		}

		@Override
		public Object get(Object key) throws CacheException {
			Object value = springCache.get(key);
			if (value instanceof SimpleValueWrapper) {
				return ((SimpleValueWrapper) value).get();
			}
			return value;
		}

		@Override
		public Object put(Object key, Object value) throws CacheException {
			springCache.put(key, value);
			return value;
		}

		@Override
		public Object remove(Object key) throws CacheException {
			springCache.evict(key);
			return null;
		}

		@Override
		public void clear() throws CacheException {
			springCache.clear();
		}

		@Override
		public int size() {
			if (springCache instanceof RedisNativeCache) {
				return ((RedisNativeCache) springCache).size();
			}
			throw new UnsupportedOperationException("invoke spring cache abstract size method not supported");
		}

		@Override
		public Set<Object> keys() {
			if (springCache instanceof RedisNativeCache) {
				return ((RedisNativeCache) springCache).keys();
			}
			throw new UnsupportedOperationException("invoke spring cache abstract keys method not supported");
		}

		@Override
		public Collection<Object> values() {
			if (springCache instanceof RedisNativeCache) {
				return ((RedisNativeCache) springCache).values();
			}
			throw new UnsupportedOperationException("invoke spring cache abstract values method not supported");
		}
	}
}
