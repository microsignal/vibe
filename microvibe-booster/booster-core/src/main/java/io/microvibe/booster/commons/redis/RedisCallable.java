package io.microvibe.booster.commons.redis;

import redis.clients.jedis.Jedis;

public interface RedisCallable<T> {

	T call(Jedis jedis);

}
