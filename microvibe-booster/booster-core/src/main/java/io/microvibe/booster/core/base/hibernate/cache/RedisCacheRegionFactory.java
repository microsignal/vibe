package io.microvibe.booster.core.base.hibernate.cache;

import io.microvibe.booster.commons.spring.ApplicationContextHolder;
import io.microvibe.booster.core.base.hibernate.cache.region.*;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.*;
import org.hibernate.cache.spi.access.AccessType;
import org.springframework.cache.CacheManager;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

public class RedisCacheRegionFactory implements RegionFactory {
	private static final long serialVersionUID = 1L;
	private static final AtomicLong CURRENT = new AtomicLong(System.currentTimeMillis());
	private RedisRegionAccessStrategyFactory regionAccessStrategyFactory;
	private SessionFactoryOptions settings;
	private CacheManager cacheManager;

	public RedisCacheRegionFactory() {
		super();
		regionAccessStrategyFactory = new RedisRegionAccessStrategyFactory(this);
	}

	@Override
	public void start(SessionFactoryOptions settings, Properties properties) throws CacheException {
		this.settings = settings;
		this.cacheManager = ApplicationContextHolder.getBean(CacheManager.class);
	}

	public SessionFactoryOptions getSettings() {
		return settings;
	}

	public CacheManager getCacheManager() {
		return cacheManager;
	}

	public RedisRegionAccessStrategyFactory getRegionAccessStrategyFactory() {
		return regionAccessStrategyFactory;
	}

	@Override
	public void stop() {
		cacheManager = null;
	}

	@Override
	public boolean isMinimalPutsEnabledByDefault() {
		return true;
	}

	@Override
	public AccessType getDefaultAccessType() {
		return AccessType.READ_WRITE;
	}

	@Override
	public long nextTimestamp() {
		return CURRENT.incrementAndGet();
	}

	@Override
	public EntityRegion buildEntityRegion(String regionName, Properties properties, CacheDataDescription metadata)
		throws CacheException {
		return new RedisEntityRegion(getRegionAccessStrategyFactory(), getCacheManager(), regionName, properties,
			metadata,
			300);
	}

	@Override
	public NaturalIdRegion buildNaturalIdRegion(String regionName, Properties properties, CacheDataDescription metadata)
		throws CacheException {
		return new RedisNaturalIdRegion(getRegionAccessStrategyFactory(), getCacheManager(), regionName, properties,
			metadata,
			300);
	}

	@Override
	public CollectionRegion buildCollectionRegion(String regionName, Properties properties,
												  CacheDataDescription metadata) throws CacheException {
		return new RedisCollectionRegion(getRegionAccessStrategyFactory(), getCacheManager(), regionName, properties,
			metadata,
			300);
	}

	@Override
	public QueryResultsRegion buildQueryResultsRegion(String regionName, Properties properties) throws CacheException {
		return new RedisQueryResultsRegion(getRegionAccessStrategyFactory(), getCacheManager(), regionName, properties,
			300);
	}

	@Override
	public TimestampsRegion buildTimestampsRegion(String regionName, Properties properties) throws CacheException {
		return new RedisTimestampsRegion(getRegionAccessStrategyFactory(), getCacheManager(), regionName, properties,
			300);
	}

}
