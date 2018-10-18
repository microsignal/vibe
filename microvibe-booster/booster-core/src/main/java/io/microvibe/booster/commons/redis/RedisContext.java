package io.microvibe.booster.commons.redis;

import io.microvibe.booster.commons.crypto.RSA;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.InitializingBean;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.util.Pool;

import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RedisContext implements InitializingBean {

	private static final Lock lock = new ReentrantLock();

	private boolean initialized = false;
	private RedisContextConfig redisContextConfig = new RedisContextConfig();
	private Pool<Jedis> jedisPool;

	public RedisContextConfig getRedisContextConfig() {
		return redisContextConfig;
	}

	public void setRedisContextConfig(RedisContextConfig redisContextConfig) {
		this.redisContextConfig = redisContextConfig;
	}

	@Override
	public void afterPropertiesSet() {
		build();
	}

	private void build() {
		lock.lock();
		try {
			if (!initialized) {
				String host = redisContextConfig.getHost();
				int port = redisContextConfig.getPort();
				String password = StringUtils.trimToNull(redisContextConfig.getPassword());
				if (password != null) {
					String publicKey = StringUtils.trimToNull(redisContextConfig.getPublicKey());
					if (publicKey != null) {
						try {
							password = RSA.decrypt(RSA.getPublicKey(publicKey), password);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

				int timeout = redisContextConfig.getTimeout();
				int database = redisContextConfig.getDatabase();

				if (redisContextConfig.isSentinel()) {
					Set<String> sentinels = redisContextConfig.getSentinels();
					String masterName = redisContextConfig.getMasterName();
					GenericObjectPoolConfig config = new GenericObjectPoolConfig();
					// JedisSentinelPool redisSentinelJedisPool = new JedisSentinelPool(clusterName, sentinels,
					// password);
					if (redisContextConfig.getMaxTotal() != null) {
						config.setMaxTotal(redisContextConfig.getMaxTotal().intValue());
					}
					if (redisContextConfig.getMaxWaitMillis() != null) {
						config.setMaxWaitMillis(redisContextConfig.getMaxWaitMillis().longValue());
					}
					if (redisContextConfig.getMaxIdle() != null) {
						config.setMaxIdle(redisContextConfig.getMaxIdle().intValue());
					}
					if (redisContextConfig.getMinIdle() != null) {
						config.setMinIdle(redisContextConfig.getMinIdle().intValue());
					}
					if (redisContextConfig.getTimeBetweenEvictionRunsMillis() != null) {
						config.setTimeBetweenEvictionRunsMillis(
							redisContextConfig.getTimeBetweenEvictionRunsMillis().longValue());
					}
					config.setTestOnBorrow(redisContextConfig.isTestOnBorrow());
					config.setTestWhileIdle(true);
					config.setTestOnReturn(false);
					jedisPool = new JedisSentinelPool(masterName, sentinels, config, timeout, password, database);
				} else {
					JedisPoolConfig config = new JedisPoolConfig();
					if (redisContextConfig.getMaxTotal() != null) {
						config.setMaxTotal(redisContextConfig.getMaxTotal().intValue());
					}
					if (redisContextConfig.getMaxWaitMillis() != null) {
						config.setMaxWaitMillis(redisContextConfig.getMaxWaitMillis().longValue());
					}
					if (redisContextConfig.getMaxIdle() != null) {
						config.setMaxIdle(redisContextConfig.getMaxIdle().intValue());
					}
					if (redisContextConfig.getMinIdle() != null) {
						config.setMinIdle(redisContextConfig.getMinIdle().intValue());
					}
					if (redisContextConfig.getTimeBetweenEvictionRunsMillis() != null) {
						config.setTimeBetweenEvictionRunsMillis(
							redisContextConfig.getTimeBetweenEvictionRunsMillis().longValue());
					}
					config.setTestOnBorrow(redisContextConfig.isTestOnBorrow());
					config.setTestWhileIdle(true);
					config.setTestOnReturn(false);
					jedisPool = new JedisPool(config, host, port, timeout, password, database);
				}
				initialized = true;
			}
		} finally {
			lock.unlock();
		}
	}

	public Pool<Jedis> getJedisPool() {
		return jedisPool;
	}

	public Jedis getJedis() {
		Jedis resource = jedisPool.getResource();
		return resource;
	}

	public void closePool() {
		if (!jedisPool.isClosed()) {
			jedisPool.close();
		}
	}

	public void run(RedisRunnable runnable) {
		Jedis jedis = getJedis();
		try {
			runnable.run(jedis);
		} finally {
			jedis.close();
		}
	}

	public <T> T call(RedisCallable<T> callable) {
		Jedis jedis = getJedis();
		try {
			return callable.call(jedis);
		} finally {
			jedis.close();
		}
	}
}
