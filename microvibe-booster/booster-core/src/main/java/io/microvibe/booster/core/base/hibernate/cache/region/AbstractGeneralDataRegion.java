package io.microvibe.booster.core.base.hibernate.cache.region;

import io.microvibe.booster.core.base.hibernate.cache.RedisRegionAccessStrategyFactory;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.GeneralDataRegion;
import org.hibernate.engine.spi.SessionImplementor;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;

import java.util.Properties;

public abstract class AbstractGeneralDataRegion extends AbstractRegion implements GeneralDataRegion {

	public AbstractGeneralDataRegion(RedisRegionAccessStrategyFactory regionAccessStrategyFactory,
									 CacheManager cacheManager, String regionName, Properties properties, int timeout) {
		super(regionAccessStrategyFactory, cacheManager, regionName, properties, timeout);
	}

	@Override
	public Object get(SessionImplementor session, Object key) throws CacheException {
		if (key == null) {
			return null;
		}
		ValueWrapper valueWrapper = getCache().get(key);
		if (valueWrapper == null) {
			return null;
		}
		return valueWrapper.get();
	}

	@Override
	public void put(SessionImplementor session, Object key, Object value) throws CacheException {
		getCache().put(key, value);
	}

	@Override
	public void evict(Object key) throws CacheException {
		getCache().evict(key);
	}

	@Override
	public void evictAll() throws CacheException {
		getCache().clear();
	}

}
