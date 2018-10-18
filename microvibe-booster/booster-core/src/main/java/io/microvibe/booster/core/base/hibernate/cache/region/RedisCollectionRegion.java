package io.microvibe.booster.core.base.hibernate.cache.region;

import io.microvibe.booster.core.base.hibernate.cache.RedisRegionAccessStrategyFactory;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.CollectionRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.springframework.cache.CacheManager;

import java.util.Properties;

public class RedisCollectionRegion extends AbstractTransactionalDataRegion implements CollectionRegion {

	public RedisCollectionRegion(RedisRegionAccessStrategyFactory regionAccessStrategyFactory,
								 CacheManager cacheManager, String regionName, Properties properties, CacheDataDescription metadata,
								 int timeout) {
		super(regionAccessStrategyFactory, cacheManager, regionName, properties, metadata, timeout);
	}

	@Override
	public CollectionRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
		return getRegionAccessStrategyFactory().createCollectionRegionAccessStrategy(this, accessType);
	}

}
