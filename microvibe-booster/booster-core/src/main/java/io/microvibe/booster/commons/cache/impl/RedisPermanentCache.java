package io.microvibe.booster.commons.cache.impl;

import io.microvibe.booster.commons.cache.RedisNativeCache;
import io.microvibe.booster.commons.redis.RedisContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import redis.clients.util.SafeEncoder;

import java.util.*;
import java.util.concurrent.Callable;

/**
 * Redis无过期时间的永久缓存实现
 *
 * @author Qt
 * @since Oct 23, 2017
 */
public class RedisPermanentCache extends RedisCacheAdaptor implements InitializingBean, RedisNativeCache, Cache {

	private RedisContext redisContext;
	private String name = getClass().getName();
	private String prefix;
	private byte[] nameBytes;

	public void setRedisContext(RedisContext redisContext) {
		this.redisContext = redisContext;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getPrefix() {
		return prefix;
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
		nameBytes = SafeEncoder.encode(prefix);
	}

	private Object deserialize(byte[] bytes) {
		return Serializers.deserialize(bytes);
	}

	private byte[] serialize(Object value) {
		return Serializers.serialize(value);
	}

    /*private byte[] wrapKeyBytes(Object key) {
        if (key instanceof String) {
            return SafeEncoder.encode((String) key);
        } else {
            return SafeEncoder.encode(JsonUtil.toJson(key));
        }
    }*/

	private Object getObject(Object key) {
		byte[] bys = redisContext.call(jedis -> jedis.hget(nameBytes, serialize(key)));
		if (bys == null) {
			return null;
		}
		/*return SerializationUtils.deserialize(bys);*/
		return deserialize(bys);
	}

	@Override
	public String getName() {
		return name;
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
				if (t != null) {
					put(key, t);
				}
			} catch (Exception e) {
				throw new ValueRetrievalException(key, valueLoader, e);
			}
		}
		return t;
	}

	@Override
	public void put(Object key, Object value) {
		redisContext.run(
			jedis -> jedis.hset(nameBytes, serialize(key), serialize(value)));
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
		redisContext.run(jedis -> jedis.hdel(nameBytes, serialize(key)));
	}

	@Override
	public void clear() {
		redisContext.run(jedis -> jedis.del(nameBytes));
	}

	@Override
	public int size() {
		return redisContext.call(jedis -> jedis.hlen(nameBytes).intValue());
	}

	@Override
	public Set<Object> keys() {
		Set<byte[]> keys = redisContext.call(jedis -> jedis.hkeys(nameBytes));
		Set<Object> set = new LinkedHashSet<>();
		for (byte[] key : keys) {
			set.add(deserialize(key));
		}
		return set;
	}

	@Override
	public Map<Object, Object> toMap() {
		Set<Object> keys = keys();
		Map<Object, Object> map = new LinkedHashMap<>();
		for (Object key : keys) {
			map.put(key, get(key, Object.class));
		}
		return map;
	}

	@Override
	public Collection<Object> values() {
		Set<Object> keys = keys();
		Set<Object> values = new LinkedHashSet<>();
		for (Object key : keys) {
			values.add(get(key, Object.class));
		}
		return values;
	}
}
