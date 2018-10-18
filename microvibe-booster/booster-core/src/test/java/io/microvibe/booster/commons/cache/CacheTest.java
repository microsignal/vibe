package io.microvibe.booster.commons.cache;

import io.microvibe.booster.commons.spring.ApplicationContextHolder;
import io.microvibe.booster.commons.utils.IOUtils;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.pool.sizeof.ReflectionSizeOf;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Ignore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.ehcache.EhCacheCache;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassRelativeResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Ignore
public class CacheTest {

	public static void main(String[] args) {
		ApplicationContext context = ApplicationContextHolder.getApplicationContext();
		Map map = new HashMap();
		for (int i = 0; i < 100; i++) {
			map.put("" + i, RandomStringUtils.randomAlphanumeric(128));
		}
		System.out.println(map);
		System.out.println(new ReflectionSizeOf().sizeOf(map));
		System.out.println(new ReflectionSizeOf()
			.deepSizeOf(3, false, map).getCalculated());


		EhCacheCacheManager cacheManager = context.getBean(EhCacheCacheManager.class);
		Cache cache = cacheManager.getCache("sysDictCache");
		for (int i = 0; i < 4000; i++) {
			cache.put("" + i, RandomStringUtils.randomAlphabetic(10));
		}
		Ehcache ehcache = (Ehcache) cache.getNativeCache();
		System.out.println(ehcache.getKeys());
		System.out.printf("size: %s%n", ehcache.getSize());
		for (int i = 0; i < 10; i++) {
			int j = new Random().nextInt(4000);
			System.out.printf("get(%s): %s%n", j, ehcache.get("" + j));
		}
		if (context instanceof Closeable) {
			IOUtils.close(((Closeable) context));
		}
	}

	@Configuration
	static class Config {

		@Bean
		@ConditionalOnMissingBean(EhCacheManagerFactoryBean.class)
		EhCacheManagerFactoryBean ehCacheManagerFactory() {
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
		@ConditionalOnMissingBean(EhCacheCacheManager.class)
		EhCacheCacheManager ehCacheManager() {
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

	}
}
