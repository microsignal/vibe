package io.microvibe.booster.core.base.hibernate.cache.region;

import io.microvibe.booster.core.base.hibernate.cache.RedisRegionAccessStrategyFactory;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.EntityRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.springframework.cache.CacheManager;

import java.util.Properties;

public class RedisEntityRegion extends AbstractTransactionalDataRegion implements EntityRegion {

	public RedisEntityRegion(RedisRegionAccessStrategyFactory regionAccessStrategyFactory, CacheManager cacheManager,
							 String regionName, Properties properties, CacheDataDescription metadata,
							 int timeout) {
		super(regionAccessStrategyFactory, cacheManager, regionName, properties, metadata, timeout);
	}

	@Override
	public EntityRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
		return getRegionAccessStrategyFactory().createEntityRegionAccessStrategy(this, accessType);
	}

}
