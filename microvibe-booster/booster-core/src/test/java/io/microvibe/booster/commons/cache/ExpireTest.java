package io.microvibe.booster.commons.cache;

import io.microvibe.booster.commons.cache.impl.RedisExpireableCache;
import io.microvibe.booster.commons.redis.RedisContext;
import io.microvibe.booster.commons.redis.RedisContexts;
import io.microvibe.booster.commons.schedule.ExpireableMap;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class ExpireTest {

	@Test
	public void test001() throws InterruptedException {
		ExpireableMap cache = new ExpireableMap(10);
		for (int i = 0; i < 100; i++) {
			String key = RandomStringUtils.randomAlphanumeric(10);
			cache.put(key,0 );
			Thread.sleep(1000);
			System.out.println(cache.keySet());
		}
	}
	@Test
	public void test002() throws InterruptedException {
		RedisContext redisContext = RedisContexts.getInstance();
		RedisExpireableCache cache = new RedisExpireableCache();
		cache.setName("test");
		cache.setRedisContext(redisContext);
		cache.setExpireTime(10);
		cache.afterPropertiesSet();

		for (int i = 0; i < 100; i++) {
			String key = RandomStringUtils.randomAlphanumeric(10);
			cache.put(key,0 );
			Thread.sleep(1000);
			System.out.println(cache.keys());
		}
	}

	@Test
	public void test003() throws InterruptedException {
		ExpireableMap cache = new ExpireableMap(10);
		for (int i = 0; i < 100; i++) {
			String key = "key-"+(i % 12);
			cache.put(key,0 );
			Thread.sleep(900);
			System.out.println(cache.keySet());
		}
	}

}
