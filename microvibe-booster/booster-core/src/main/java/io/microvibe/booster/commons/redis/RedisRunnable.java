package io.microvibe.booster.commons.redis;

import redis.clients.jedis.Jedis;

public interface RedisRunnable {

	void run(Jedis jedis);

}
