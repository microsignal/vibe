package io.microvibe.booster.core.base.hibernate.cache;

import io.microvibe.booster.core.base.hibernate.cache.region.RedisCollectionRegion;
import io.microvibe.booster.core.base.hibernate.cache.region.RedisEntityRegion;
import io.microvibe.booster.core.base.hibernate.cache.region.RedisNaturalIdRegion;
import io.microvibe.booster.core.base.hibernate.cache.strategy.RedisCollectionRegionAccessStrategy;
import io.microvibe.booster.core.base.hibernate.cache.strategy.RedisEntityRegionAccessStrategy;
import io.microvibe.booster.core.base.hibernate.cache.strategy.RedisNaturalIdRegionAccessStrategy;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;

public class RedisRegionAccessStrategyFactory {

	private RedisCacheRegionFactory redisCacheRegionFactory;

	public RedisRegionAccessStrategyFactory(RedisCacheRegionFactory redisCacheRegionFactory) {
		this.redisCacheRegionFactory = redisCacheRegionFactory;
	}

	public EntityRegionAccessStrategy createEntityRegionAccessStrategy(RedisEntityRegion redisEntityRegion,
																	   AccessType accessType) {
		return new RedisEntityRegionAccessStrategy(redisEntityRegion, redisCacheRegionFactory.getSettings());
	}

	public NaturalIdRegionAccessStrategy createNaturalIdRegionAccessStrategy(RedisNaturalIdRegion redisNaturalIdRegion,
																			 AccessType accessType) {
		return new RedisNaturalIdRegionAccessStrategy(redisNaturalIdRegion, redisCacheRegionFactory.getSettings());
	}

	public CollectionRegionAccessStrategy createCollectionRegionAccessStrategy(
		RedisCollectionRegion redisCollectionRegion, AccessType accessType) {
		return new RedisCollectionRegionAccessStrategy(redisCollectionRegion, redisCacheRegionFactory.getSettings());
	}

}
