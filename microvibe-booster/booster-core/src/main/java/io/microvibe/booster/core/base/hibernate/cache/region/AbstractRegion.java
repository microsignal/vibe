package io.microvibe.booster.core.base.hibernate.cache.region;

import io.microvibe.booster.core.base.hibernate.cache.RedisRegionAccessStrategyFactory;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.Region;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

@SuppressWarnings({"unused", "rawtypes"})
public abstract class AbstractRegion implements Region {

	private static final AtomicLong CURRENT = new AtomicLong(System.currentTimeMillis());
	private RedisRegionAccessStrategyFactory regionAccessStrategyFactory;
	private CacheManager cacheManager;
	private Cache cache;
	private String regionName;
	private Properties properties;
	private CacheDataDescription metadata;
	private int timeout;

	public AbstractRegion(RedisRegionAccessStrategyFactory regionAccessStrategyFactory, CacheManager cacheManager,
						  String regionName, Properties properties, int timeout) {
		this.regionAccessStrategyFactory = regionAccessStrategyFactory;
		this.cacheManager = cacheManager;
		this.regionName = regionName;
		this.properties = properties;
		this.cache = cacheManager.getCache(regionName);
		this.timeout = timeout;
	}

	public RedisRegionAccessStrategyFactory getRegionAccessStrategyFactory() {
		return regionAccessStrategyFactory;
	}

	public CacheManager getCacheManager() {
		return cacheManager;
	}

	public Cache getCache() {
		return cache;
	}

	@Override
	public String getName() {
		return regionName;
	}

	@Override
	public void destroy() throws CacheException {
		cache.clear();
	}

	@Override
	public boolean contains(Object key) {
		return cache.get(key) != null;
	}

	@Override
	public long getSizeInMemory() {
		return 0;
	}

	@Override
	public long getElementCountInMemory() {
		return 0;
	}

	@Override
	public long getElementCountOnDisk() {
		return 0;
	}

	@Override
	public Map toMap() {
		return Collections.EMPTY_MAP;
	}

	@Override
	public long nextTimestamp() {
		return CURRENT.getAndIncrement();
	}

	public long preTimestamp() {
		return CURRENT.get();
	}

	@Override
	public int getTimeout() {
		return this.timeout;
	}

}
