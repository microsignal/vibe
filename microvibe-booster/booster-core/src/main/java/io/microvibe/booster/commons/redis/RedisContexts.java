package io.microvibe.booster.commons.redis;

import io.microvibe.booster.commons.spring.ApplicationContextHolder;
import redis.clients.jedis.Jedis;

public class RedisContexts {

	private static final ThreadLocal<Jedis> jedisLocal = new ThreadLocal<Jedis>();
	private static RedisContext context;

	public static void run(RedisRunnable runnable) {
		try {
			Jedis jedis = getCurrentJedis();
			runnable.run(jedis);
		} finally {
			closeCurrentJedis();
		}
	}

	public static <T> T call(RedisCallable<T> callable) {
		try {
			Jedis jedis = getCurrentJedis();
			return callable.call(jedis);
		} finally {
			closeCurrentJedis();
		}
	}

	public static RedisContext getInstance() {
		if (context != null) {
			return context;
		}
		synchronized (RedisContexts.class) {
			if (context != null) {
				return context;
			}
			if (ApplicationContextHolder.getApplicationContext() != null) {
				return context = ApplicationContextHolder.getBean(RedisContext.class);
			}
			RedisContext context = new RedisContext();
			RedisContextConfig config = new RedisContextConfig();
			context.setRedisContextConfig(config);
			context.afterPropertiesSet();
			return RedisContexts.context = context;
		}
	}

	public static Jedis getCurrentJedis() {
		Jedis jedis = jedisLocal.get();
		if (jedis == null || !jedis.isConnected()) {
			jedis = getInstance().getJedis();
			jedisLocal.set(jedis);
		}
		return jedis;
	}

	public static void closeCurrentJedis() {
		Jedis jedis = jedisLocal.get();
		if (jedis != null) {
			jedisLocal.remove();
			if (jedis.isConnected()) {
				try {
					jedis.close();
				} catch (Exception e) {
				}
			}
		}
	}

	public static void closePool() {
		getInstance().closePool();
	}

}
