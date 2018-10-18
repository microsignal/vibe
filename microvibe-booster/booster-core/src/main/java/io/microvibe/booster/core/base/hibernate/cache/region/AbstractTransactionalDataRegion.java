package io.microvibe.booster.core.base.hibernate.cache.region;

import io.microvibe.booster.core.base.hibernate.cache.RedisRegionAccessStrategyFactory;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.TransactionalDataRegion;
import org.springframework.cache.CacheManager;

import java.util.Properties;

public abstract class AbstractTransactionalDataRegion extends AbstractRegion implements TransactionalDataRegion {

	private CacheDataDescription metadata;

	public AbstractTransactionalDataRegion(RedisRegionAccessStrategyFactory regionAccessStrategyFactory,
										   CacheManager cacheManager, String regionName, Properties properties, CacheDataDescription metadata,
										   int timeout) {
		super(regionAccessStrategyFactory, cacheManager, regionName, properties, timeout);
		this.metadata = metadata;
	}

	@Override
	public boolean isTransactionAware() {
		return false;
	}

	@Override
	public CacheDataDescription getCacheDataDescription() {
		return metadata;
	}

}
