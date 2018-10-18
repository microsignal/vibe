package io.microvibe.booster.core.base.hibernate.cache.region;

import io.microvibe.booster.core.base.hibernate.cache.RedisRegionAccessStrategyFactory;
import org.hibernate.cache.spi.QueryResultsRegion;
import org.springframework.cache.CacheManager;

import java.util.Properties;

public class RedisQueryResultsRegion extends AbstractGeneralDataRegion implements QueryResultsRegion {

	public RedisQueryResultsRegion(RedisRegionAccessStrategyFactory regionAccessStrategyFactory,
								   CacheManager cacheManager, String regionName, Properties properties, int timeout) {
		super(regionAccessStrategyFactory, cacheManager, regionName, properties, timeout);
	}
}
