package io.microvibe.booster.core.base.hibernate.cache.strategy;

import io.microvibe.booster.core.base.hibernate.cache.region.AbstractRegion;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.access.RegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.engine.spi.SessionImplementor;
import org.springframework.cache.Cache.ValueWrapper;

public class AbstractRegionAccessStrategy<T extends AbstractRegion> implements RegionAccessStrategy {

	T region;
	SessionFactoryOptions settings;

	public AbstractRegionAccessStrategy(T region, SessionFactoryOptions settings) {
		super();
		this.region = region;
		this.settings = settings;
	}

	protected T region() {
		return region;
	}

	@Override
	public Object get(SessionImplementor session, Object key, long txTimestamp) throws CacheException {
		ValueWrapper valueWrapper = region.getCache().get(key);
		return valueWrapper == null ? null : valueWrapper.get();
	}

	@Override
	public boolean putFromLoad(SessionImplementor session, Object key, Object value, long txTimestamp, Object version)
		throws CacheException {
		return putFromLoad(session, key, value, txTimestamp, version, settings.isMinimalPutsEnabled());
	}

	@Override
	public boolean putFromLoad(SessionImplementor session, Object key, Object value, long txTimestamp, Object version,
							   boolean minimalPutOverride) throws CacheException {
		if (minimalPutOverride && region.contains(key)) {
			return false;
		} else {
			region.getCache().put(key, value);
			return true;
		}
	}

	@Override
	public SoftLock lockItem(SessionImplementor session, Object key, Object version) throws CacheException {
		return null;
	}

	@Override
	public SoftLock lockRegion() throws CacheException {
		return null;
	}

	@Override
	public void unlockItem(SessionImplementor session, Object key, SoftLock lock) throws CacheException {
		evict(key);
	}

	@Override
	public void unlockRegion(SoftLock lock) throws CacheException {
		evictAll();
	}

	@Override
	public void remove(SessionImplementor session, Object key) throws CacheException {
		evict(key);
	}

	@Override
	public void removeAll() throws CacheException {
		evictAll();
	}

	@Override
	public void evict(Object key) throws CacheException {
		region.getCache().evict(key);
	}

	@Override
	public void evictAll() throws CacheException {
		region.getCache().clear();
	}

}
