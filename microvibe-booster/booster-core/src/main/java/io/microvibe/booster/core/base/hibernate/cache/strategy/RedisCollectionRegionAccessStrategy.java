package io.microvibe.booster.core.base.hibernate.cache.strategy;

import io.microvibe.booster.core.base.hibernate.cache.region.RedisCollectionRegion;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.internal.DefaultCacheKeysFactory;
import org.hibernate.cache.spi.CollectionRegion;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.persister.collection.CollectionPersister;

public class RedisCollectionRegionAccessStrategy extends AbstractRegionAccessStrategy<RedisCollectionRegion>
	implements CollectionRegionAccessStrategy {

	public RedisCollectionRegionAccessStrategy(RedisCollectionRegion region, SessionFactoryOptions settings) {
		super(region, settings);
	}

	@Override
	public Object generateCacheKey(Object id, CollectionPersister persister, SessionFactoryImplementor factory,
								   String tenantIdentifier) {
		return DefaultCacheKeysFactory.staticCreateCollectionKey(id, persister, factory, tenantIdentifier);
	}

	@Override
	public Object getCacheKeyId(Object cacheKey) {
		return DefaultCacheKeysFactory.staticGetCollectionId(cacheKey);
	}

	@Override
	public CollectionRegion getRegion() {
		return region();
	}

}
