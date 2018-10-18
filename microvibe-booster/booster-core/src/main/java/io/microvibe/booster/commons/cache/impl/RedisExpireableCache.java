package io.microvibe.booster.commons.cache.impl;

import io.microvibe.booster.commons.cache.RedisNativeCache;
import io.microvibe.booster.commons.redis.RedisContext;
import io.microvibe.booster.commons.schedule.Schedules;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Tuple;
import redis.clients.util.SafeEncoder;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Redis 有时效(过期机制)的缓存实现
 *
 * @author Qt
 * @since Oct 23, 2017
 */
@Slf4j
public class RedisExpireableCache extends RedisCacheAdaptor implements InitializingBean, RedisNativeCache, Cache {

	//private static Timer timer = new Timer(true);
	//private static long maxTimerPeriod = 1000 * 7200;// 2h
	public static final int MIN_SCHEDULE_PERIOD = 10; // 10s
	public static final int MAX_SCHEDULE_PERIOD = 1200; // 20min
	private RedisContext redisContext;
	private String name = getClass().getName();
	private String prefix;
	private byte[] prefixBytes;
	private int expireTime = 0;// seconds

	private byte[] keysetBytes;

	public void setRedisContext(RedisContext redisContext) {
		this.redisContext = redisContext;
	}

	public void setExpireTime(int expireTime) {
		this.expireTime = expireTime;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public void afterPropertiesSet() {
		if (StringUtils.isEmpty(name)) {
			throw new BeanInitializationException("cache name must not be null!");
		}
		prefix = StringUtils.trimToNull(prefix);
		if (prefix == null) {
			prefix = name;
		}
		prefixBytes = SafeEncoder.encode(prefix + ":");
		keysetBytes = SafeEncoder.encode(prefix + "~keys");
		if (expireTime > 0) {
			int period = Integer.max(MIN_SCHEDULE_PERIOD, expireTime >> 2);
			period = Integer.min(period, MAX_SCHEDULE_PERIOD);
			Schedules.executor().scheduleAtFixedRate(() -> {
				try {
					// 定时清理过期元素, 主键值对已由redis自动清除,主要是清除zset集合
					log.info("开始清理Redis过期元素: {}", getName());
					cleanExpiredKeys();
					log.info("清理Redis过期元素完毕: {}", getName());
				} catch (Exception e) {
					log.warn(e.getMessage(), e);
				}
			}, period, period, TimeUnit.SECONDS);
			/*
			long period = Math.min(expireTime * 1000 >> 3, maxTimerPeriod);
			timer = new Timer(true);
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					// 定时清理过期元素, 主键值对已由redis自动清除,主要是清除zset集合
					cleanExpiredKeys();
				}
			}, period, period);
			*/
		}
	}

	private Object deserialize(byte[] bytes) {
		return Serializers.deserialize(bytes);
	}

	private byte[] serialize(Object value) {
		return Serializers.serialize(value);
	}

	private byte[] toKeyBytes(Object key) {
		return Serializers.join(prefixBytes, serialize(key));
	}

	private Object fromKeyBytes(byte[] bytes) {
		return deserialize(Serializers.trimPrefix(bytes, prefixBytes.length));
	}

	private Object getObject(Object key) {
		byte[] bys = redisContext.call(jedis -> {
			byte[] keyBytes = toKeyBytes(key);
			byte[] val = jedis.get(keyBytes);
			if (val != null) {
				if (expireTime > 0) {
					// 更新过期时间
					long currentSeconds = System.currentTimeMillis() / 1000;
					jedis.expire(keyBytes, expireTime);
					jedis.zadd(keysetBytes, currentSeconds, keyBytes);
				}
			}
			return val;
		});
		if (bys == null) {
			return null;
		}
		return deserialize(bys);
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public RedisNativeCache getNativeCache() {
		return this;
	}

	@Override
	public ValueWrapper get(Object key) {
		Object object = getObject(key);
		return object == null ? null : new SimpleValueWrapper(object);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(Object key, Class<T> type) {
		return (T) getObject(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(Object key, Callable<T> valueLoader) {
		T t = (T) getObject(key);
		if (t == null) {
			try {
				t = valueLoader.call();
				put(key, t);
			} catch (Exception e) {
				throw new ValueRetrievalException(key, valueLoader, e);
			}
		}
		return t;
	}

	@Override
	public void put(Object key, Object value) {
		redisContext.run(jedis -> {
			byte[] keyBytes = toKeyBytes(key);
			Transaction multi = jedis.multi();
			long currentSeconds = System.currentTimeMillis() / 1000;
			if (expireTime > 0) {
				multi.setex(keyBytes, expireTime, serialize(value));
			} else {
				multi.set(keyBytes, serialize(value));
			}
			multi.zadd(keysetBytes, currentSeconds, keyBytes);
			multi.exec();
		});
	}

	@Override
	public ValueWrapper putIfAbsent(Object key, Object value) {
		Object v = getObject(key);
		if (v == null) {
			put(key, value);
		}
		return new SimpleValueWrapper(v);
	}

	@Override
	public void evict(Object key) {
		redisContext.run(jedis -> {
			byte[] keyBytes = toKeyBytes(key);
			Transaction multi = jedis.multi();
			multi.del(keyBytes);
			multi.zrem(keysetBytes, keyBytes);
			multi.exec();
		});
	}

	@Override
	public void clear() {
		redisContext.run(jedis -> {
            /*
            long currentSeconds = System.currentTimeMillis() / 1000;
            Long zcard = jedis.zcard(keysetBytes);
            System.out.println(zcard.intValue());
            Set<byte[]> keyset = jedis.zrangeByScore(keysetBytes, 0, currentSeconds);
            Transaction multi = jedis.multi();
            for (byte[] key : keyset) {
                System.out.println(SafeEncoder.encode(key));
                multi.del(key);
                multi.zrem(keysetBytes, key);
            }
            multi.exec();
            */
			while (true) {
				ScanResult<Tuple> zscan = jedis.zscan(keysetBytes, ScanParams.SCAN_POINTER_START_BINARY);
				List<Tuple> result = zscan.getResult();
				for (Tuple tuple : result) {
					byte[] key = tuple.getBinaryElement();
					Transaction multi = jedis.multi();
					multi.del(key);
					multi.zrem(keysetBytes, key);
					multi.exec();
				}
				String stringCursor = zscan.getStringCursor();
				if (stringCursor.equals(ScanParams.SCAN_POINTER_START)) {
					break;
				}
			}
		});
	}

	private void cleanExpiredKeys() {
		redisContext.run(jedis -> {
			long currentSeconds = System.currentTimeMillis() / 1000;
			long expiredTimeSeconds = currentSeconds - expireTime;// 过期元素的时点
			while (true) {
				ScanResult<Tuple> zscan = jedis.zscan(keysetBytes, ScanParams.SCAN_POINTER_START_BINARY);
				List<Tuple> result = zscan.getResult();
				for (Tuple tuple : result) {
					byte[] key = tuple.getBinaryElement();
					long score = (long) tuple.getScore();
					if (score < expiredTimeSeconds) {// 已过期
						Transaction multi = jedis.multi();
						multi.del(key);
						multi.zrem(keysetBytes, key);
						multi.exec();
					}
				}
				String stringCursor = zscan.getStringCursor();
				if (stringCursor.equals(ScanParams.SCAN_POINTER_START)) {
					break;
				}
			}
		});
	}

	@Override
	public int size() {
		return redisContext.call(jedis -> jedis.zcard(keysetBytes).intValue());
	}

	@Override
	public Set<Object> keys() {
		return redisContext.call(jedis -> {
			Set<Object> set = new LinkedHashSet<>();
			while (true) {
				ScanResult<Tuple> zscan = jedis.zscan(keysetBytes, ScanParams.SCAN_POINTER_START_BINARY);
				List<Tuple> result = zscan.getResult();
				for (Tuple tuple : result) {
					byte[] key = tuple.getBinaryElement();
					Object oKey = fromKeyBytes(key);
					set.add(oKey);
				}
				String stringCursor = zscan.getStringCursor();
				if (stringCursor.equals(ScanParams.SCAN_POINTER_START)) {
					break;
				}
			}
			return set;
		});
	}

	@Override
	public Map<Object, Object> toMap() {
		return redisContext.call(jedis -> {
			Map<Object, Object> map = new LinkedHashMap<>();
			while (true) {
				ScanResult<Tuple> zscan = jedis.zscan(keysetBytes, ScanParams.SCAN_POINTER_START_BINARY);
				List<Tuple> result = zscan.getResult();
				for (Tuple tuple : result) {
					byte[] key = tuple.getBinaryElement();
					byte[] val = jedis.get(key);
					if (val != null) {
						Object oKey = fromKeyBytes(key);
						map.put(oKey, deserialize(val));
					}
				}
				String stringCursor = zscan.getStringCursor();
				if (stringCursor.equals(ScanParams.SCAN_POINTER_START)) {
					break;
				}
			}
			return map;
		});
	}

	@Override
	public Collection<Object> values() {
		return toMap().values();
	}
}
