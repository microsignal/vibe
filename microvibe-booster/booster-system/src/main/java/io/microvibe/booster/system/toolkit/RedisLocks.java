package io.microvibe.booster.system.toolkit;

import io.microvibe.booster.commons.redis.RedisContexts;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Qt
 * @since Aug 06, 2018
 */
@Slf4j
public class RedisLocks {
	private static final Lock LOCK = new ReentrantLock();
	private static final Condition MONITOR = LOCK.newCondition();
	private static final ThreadLocal<String> lockClientId = ThreadLocal.withInitial(() -> UUID.randomUUID().toString());
	private static final ThreadLocal<Boolean> locked = ThreadLocal.withInitial(() -> false);
	private static final String LOCK_SUCCESS = "OK";
	private static final Long RELEASE_SUCCESS = 1L;
	public static int LOCK_EXPIRE_TIME = 60;//seconds

	public static boolean tryLock(String lockKey) {
		boolean success = RedisContexts.call(jedis -> {
			return tryLock(jedis, lockKey, lockClientId.get(), LOCK_EXPIRE_TIME);
		});
		if (success) {
			locked.set(true);
		}
		return success;
	}

	public static boolean tryLock(String lockKey, long timeout, TimeUnit unit) {
		timeout = unit.toMillis(timeout);
		for (int i = 0; i < timeout; i += 10) {
			boolean success = tryLock(lockKey);
			if (success) {
				return success;
			}
			LOCK.lock();
			try {
				MONITOR.await(10, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
			} finally {
				LOCK.unlock();
			}
		}
		return false;
	}

	public static void lock(String lockKey, long timeout, TimeUnit unit) {
		if (!tryLock(lockKey, timeout, unit)) {
			throw new IllegalStateException();
		}
	}

	public static void lock(String lockKey) {
		lock(lockKey, LOCK_EXPIRE_TIME, TimeUnit.SECONDS);
	}

	public static boolean unlock(String lockKey) {
		try {
			if (locked.get()) {
				boolean success = RedisContexts.call(jedis -> {
					return unlock(jedis, lockKey, lockClientId.get());
				});
				if (success) {
					LOCK.lock();
					try {
						MONITOR.signalAll();
					} finally {
						LOCK.unlock();
					}
				}
				return success;
			} else {
				locked.remove();
				return false;
			}
		} finally {
			lockClientId.remove();
		}
	}

	/**
	 * 释放分布式锁
	 *
	 * @param jedis    Redis客户端
	 * @param lockKey  锁
	 * @param clientId 请求标识
	 * @return 是否释放成功
	 */
	public static boolean unlock(Jedis jedis, String lockKey, String clientId) {
		String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
		Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(clientId));
		if (RELEASE_SUCCESS.equals(result)) {
			return true;
		}
		return false;
	}

	/**
	 * 尝试获取分布式锁
	 *
	 * @param jedis      Redis客户端
	 * @param lockKey    锁
	 * @param clientId   请求标识
	 * @param expireTime 超期时间
	 * @return 是否获取成功
	 */
	public static boolean tryLock(Jedis jedis, String lockKey, String clientId, int expireTime) {
		//String result = jedis.set(lockKey, clientId, "NX", "PX", expireTime);
		String script = "return redis.call('set', KEYS[1], KEYS[2], 'EX', KEYS[3], 'NX')";
		Object result = jedis.eval(script, 3, lockKey, clientId, String.valueOf(expireTime));
		if (LOCK_SUCCESS.equals(result)) {
			return true;
		}
		return false;
	}

}
