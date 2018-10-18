package io.microvibe.booster.core.base.hibernate.cache.strategy;

import io.microvibe.booster.core.base.hibernate.cache.region.RedisNaturalIdRegion;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.internal.DefaultCacheKeysFactory;
import org.hibernate.cache.spi.NaturalIdRegion;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.persister.entity.EntityPersister;

public class RedisNaturalIdRegionAccessStrategy extends AbstractRegionAccessStrategy<RedisNaturalIdRegion>
	implements NaturalIdRegionAccessStrategy {

	public RedisNaturalIdRegionAccessStrategy(RedisNaturalIdRegion region, SessionFactoryOptions settings) {
		super(region, settings);
	}

	@Override
	public Object generateCacheKey(Object[] naturalIdValues, EntityPersister persister, SessionImplementor session) {
		return DefaultCacheKeysFactory.staticCreateNaturalIdKey(naturalIdValues, persister, session);
	}

	@Override
	public Object[] getNaturalIdValues(Object cacheKey) {
		return DefaultCacheKeysFactory.staticGetNaturalIdValues(cacheKey);
	}

	@Override
	public NaturalIdRegion getRegion() {
		return region();
	}

	@Override
	public boolean insert(SessionImplementor session, Object key, Object value) throws CacheException {
		return false;
	}

	@Override
	public boolean afterInsert(SessionImplementor session, Object key, Object value) throws CacheException {
		return false;
	}

	@Override
	public boolean update(SessionImplementor session, Object key, Object value) throws CacheException {
		remove(session, key);
		return false;
	}

	@Override
	public boolean afterUpdate(SessionImplementor session, Object key, Object value, SoftLock lock)
		throws CacheException {
		unlockItem(session, key, lock);
		return false;
	}

}
