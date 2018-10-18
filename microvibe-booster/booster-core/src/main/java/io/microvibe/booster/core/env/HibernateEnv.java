package io.microvibe.booster.core.env;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HibernateEnv {

	@Value("${hibernate.query.substitutions:true 1, false 0}")
	String querySubstitutions;
	@Value("${hibernate.default_batch_fetch_size:20}")
	int defaultBatchFetchSize;
	@Value("${hibernate.max_fetch_depth:2}")
	int maxFetchDepth;
	@Value("${hibernate.generate_statistics:true}")
	boolean generateStatistics;
	@Value("${hibernate.bytecode.use_reflection_optimizer:true}")
	boolean bytecodeUseReflectionOptimizer;
	@Value("${hibernate.cache.use_second_level_cache:true}")
	boolean useSecondLevelCache;
	@Value("${hibernate.cache.use_query_cache:true}")
	boolean useQueryCache;
	@Value("${hibernate.cache.region.factory_class:io.microvibe.booster.core.base.hibernate.cache.RedisCacheRegionFactory}")
	String cacheRegionFactoryClass;

	@Value("${hibernate.cache.use_structured_entries:true}")
	boolean cacheUseStructuredEntries;

	public String getQuerySubstitutions() {
		return querySubstitutions;
	}

	public int getDefaultBatchFetchSize() {
		return defaultBatchFetchSize;
	}

	public int getMaxFetchDepth() {
		return maxFetchDepth;
	}

	public boolean isGenerateStatistics() {
		return generateStatistics;
	}

	public boolean isBytecodeUseReflectionOptimizer() {
		return bytecodeUseReflectionOptimizer;
	}

	public boolean isUseSecondLevelCache() {
		return useSecondLevelCache;
	}

	public boolean isUseQueryCache() {
		return useQueryCache;
	}

	public String getCacheRegionFactoryClass() {
		return cacheRegionFactoryClass;
	}

	public boolean isCacheUseStructuredEntries() {
		return cacheUseStructuredEntries;
	}

}
