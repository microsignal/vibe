package io.microvibe.booster.config.cache;

import io.microvibe.booster.commons.cache.RedisNativeCacheManager;
import io.microvibe.booster.commons.redis.RedisContext;
import io.microvibe.booster.commons.redis.RedisContextConfig;
import io.microvibe.booster.commons.spring.AfterApplicationContextHolder;
import io.microvibe.booster.config.task.AfterTaskConfig;
import io.microvibe.booster.core.env.CacheEnv;
import io.microvibe.booster.core.env.RedisEnv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCache;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassRelativeResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@EnableCaching
public class CacheConfig {
	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	@Autowired
	RedisEnv redisEnv;
	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	@Autowired
	CacheEnv cacheEnv;

	@Bean("cacheManager")
	@Primary
	public CacheManager cacheManager() {
		if (redisEnv.isEnabled()) {
			return redisCacheManager();
		} else {
			return ehCacheManager();
		}
	}

	@Bean
	public EhCacheManagerFactoryBean ehCacheManagerFactory() {
		EhCacheManagerFactoryBean bean = new EhCacheManagerFactoryBean();
		ResourcePatternResolver resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(new ClassRelativeResourceLoader(getClass()));
		Resource resource = resourcePatternResolver.getResource("classpath:ehcache.xml");
		if (resource.isReadable()) {
			bean.setConfigLocation(resource);
		}
		bean.setShared(true);
		return bean;
	}

	@Bean
	public EhCacheCacheManager ehCacheManager() {
		EhCacheCacheManager bean = new EhCacheCacheManager() {
			@Override
			protected Cache getMissingCache(String name) {
				Cache cache = super.getMissingCache(name);
				if (cache == null) {
					net.sf.ehcache.CacheManager cacheManager = getCacheManager();
					synchronized (cacheManager) {
						net.sf.ehcache.Cache ehcache = cacheManager.getCache(name);
						if (ehcache == null) {
							cacheManager.addCacheIfAbsent(name);
							ehcache = cacheManager.getCache(name);
							cache = new EhCacheCache(ehcache);
						}
					}
				}
				return cache;
			}
		};
		net.sf.ehcache.CacheManager cacheManager = ehCacheManagerFactory().getObject();
		bean.setCacheManager(cacheManager);
		return bean;
	}

	@Bean
	@AfterTaskConfig
	@AfterApplicationContextHolder
	public RedisNativeCacheManager redisCacheManager() {
		RedisNativeCacheManager cacheManager = new RedisNativeCacheManager();
		cacheManager.setDefaultExpiration(cacheEnv.getDefaultExpiration());//
		cacheManager.setExpires(cacheEnv.getCacheExpires());
		cacheManager.setRedisContext(redisContext());
		return cacheManager;
	}

	@Bean
	public RedisContext redisContext() {
		RedisContext redisContext = new RedisContext();
		redisContext.setRedisContextConfig(redisContextConfig());
		return redisContext;
	}

	@Bean
	public RedisContextConfig redisContextConfig() {
		RedisContextConfig config = new RedisContextConfig();
		config.setHost(redisEnv.getHostName());
		config.setPort(redisEnv.getPort());
		config.setDatabase(redisEnv.getDatabase());
		config.setPassword(redisEnv.getPassword());
		config.setPublicKey(redisEnv.getPublicKey());
		config.setMaxIdle(redisEnv.getMaxIdle());
		config.setMaxWaitMillis(redisEnv.getMaxWaitMillis());
		config.setTestOnBorrow(redisEnv.isTestOnBorrow());
		return config;
	}

	@Bean
	JedisConnectionFactory springRedisConnectionFactory() {
		JedisConnectionFactory factory = new JedisConnectionFactory();
		factory.setHostName(redisEnv.getHostName());
		factory.setPort(redisEnv.getPort());
		factory.setDatabase(redisEnv.getDatabase());
		factory.setPassword(redisEnv.getPassword());

		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxIdle(redisEnv.getMaxIdle());
		poolConfig.setMaxWaitMillis(redisEnv.getMaxWaitMillis());
		poolConfig.setTestOnBorrow(redisEnv.isTestOnBorrow());

		factory.setPoolConfig(poolConfig);
		return factory;
	}

	@Bean
	@SuppressWarnings({"rawtypes", "unchecked"})
	public RedisTemplate springRedisTemplate() {
		JedisConnectionFactory connectionFactory = springRedisConnectionFactory();
		RedisTemplate redisTemplate = new RedisTemplate();
		redisTemplate.setConnectionFactory(connectionFactory);
		redisTemplate.setKeySerializer(new JdkSerializationRedisSerializer());
		redisTemplate.setHashKeySerializer(new JdkSerializationRedisSerializer());
		return redisTemplate;
	}

	@Bean
	@SuppressWarnings("rawtypes")
	public RedisCacheManager springRedisCacheManager() {
		RedisTemplate redisTemplate = springRedisTemplate();
		RedisCacheManager bean = new RedisCacheManager(redisTemplate);
		bean.setUsePrefix(true);
		bean.setDefaultExpiration(cacheEnv.getDefaultExpiration());//
		bean.setExpires(cacheEnv.getCacheExpires());
		return bean;
	}
}
