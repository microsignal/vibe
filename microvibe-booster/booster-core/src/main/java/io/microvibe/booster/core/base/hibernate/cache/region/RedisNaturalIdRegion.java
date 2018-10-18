package io.microvibe.booster.core.base.hibernate.cache.region;

import io.microvibe.booster.core.base.hibernate.cache.RedisRegionAccessStrategyFactory;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.NaturalIdRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.springframework.cache.CacheManager;

import java.util.Properties;

public class RedisNaturalIdRegion extends AbstractTransactionalDataRegion implements NaturalIdRegion {

	public RedisNaturalIdRegion(RedisRegionAccessStrategyFactory regionAccessStrategyFactory, CacheManager cacheManager,
								String regionName, Properties properties, CacheDataDescription metadata, int timeout) {
		super(regionAccessStrategyFactory, cacheManager, regionName, properties, metadata, timeout);
	}

	@Override
	public NaturalIdRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
		return getRegionAccessStrategyFactory().createNaturalIdRegionAccessStrategy(this, accessType);
	}

}
