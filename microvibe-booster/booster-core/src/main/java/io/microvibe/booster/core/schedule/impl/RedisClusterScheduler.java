package io.microvibe.booster.core.schedule.impl;

import com.alibaba.fastjson.JSONObject;
import io.microvibe.booster.commons.redis.RedisContexts;
import io.microvibe.booster.commons.spring.ApplicationContextHolder;
import io.microvibe.booster.core.schedule.ClusterScheduler;
import io.microvibe.booster.core.schedule.ScheduleContext;
import io.microvibe.booster.core.schedule.TaskCommand;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationContext;
import redis.clients.jedis.JedisPubSub;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Qt
 * @since Aug 06, 2018
 */
public class RedisClusterScheduler extends AbstractClusterScheduler implements ClusterScheduler {
	private static final String KEY_PREFIX = "ClusterScheduler:";
	private static final int HEART_TIME = 10; //seconds
	private final Lock lock = new ReentrantLock();
	private final Condition keepAliveCond = lock.newCondition();
	private Thread commandMonitorThread;
	private Thread keepAliveThread;
	private boolean keepAlived = false;
	@Setter
	private int heartTime = HEART_TIME;// seconds;
	@Setter
	@Getter
	private boolean keepAlive = true;

	@Override
	public boolean tryLock(String id) {
//		lock.lock();
//		try {
			return RedisContexts.call(jedis -> {
				String lockKey = lockKey(id);
				String uuid = jedis.get(lockKey);
				if (uuid == null /*|| getUuid().equalsIgnoreCase(uuid)*/) {
					// current scheduler locking
					jedis.set(lockKey, getUuid());
					jedis.setex(aliveKey(), getHeartTime() << 2, Long.toString(System.currentTimeMillis()));
					return true;
				} else {
					Boolean exists = jedis.exists(aliveKey(uuid));
					if (Boolean.TRUE.equals(exists)) {
						// scheduler locking
						return false;
					} else {
						// locking scheduler dead
						jedis.set(lockKey, getUuid());
						jedis.setex(aliveKey(), getHeartTime() << 2, Long.toString(System.currentTimeMillis()));
						return true;
					}
				}
			});
//		} finally {
//			lock.unlock();
//		}
	}

	@Override
	public void unlock(String id) {
//		lock.lock();
//		try {
			RedisContexts.run(jedis -> {
				String lockKey = lockKey(id);
				String uuid = jedis.get(lockKey);
				if (getUuid().equalsIgnoreCase(uuid)) {
					jedis.del(lockKey);
				}
			});
//		} finally {
//			lock.unlock();
//		}
	}

	@Override
	public void keepAlive() {
		if (!keepAlive || keepAlived) {
			return;
		}
		lock.lock();
		try {
			keepAliveThread = new Thread(() -> {
				while (keepAlive) {
					doKeepAlive();
					try {
						if (lock.tryLock(getHeartTime(), TimeUnit.SECONDS)) {
							keepAliveCond.await(getHeartTime(), TimeUnit.SECONDS);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						lock.unlock();
					}
				}
				keepAlived = false;
			});
			keepAliveThread.setDaemon(true);
			keepAliveThread.start();
			keepAlived = true;
		} finally {
			lock.unlock();
		}
	}

	public void doKeepAlive() {
		RedisContexts.run(jedis -> {
			jedis.setex(aliveKey(), getHeartTime() << 2, Long.toString(System.currentTimeMillis()));
		});
	}

	private String aliveKey() {
		return aliveKey(getUuid());
	}

	private String aliveKey(String id) {
		return keyPrefix() + id + ":alive";
	}

	private String lockKey(String id) {
		return keyPrefix() + id + ":lock";
	}

	@Override
	public void startCommandMonitor() {
		lock.lock();
		try {
			if (commandMonitorThread != null) {
				return;
			}
			commandMonitorThread = new Thread(() -> {
				RedisContexts.run(jedis -> {
					jedis.subscribe(new JedisPubSub() {
						@Override
						public void onMessage(String channel, String message) {
							JSONObject json = JSONObject.parseObject(message);
							String uuid = json.getString("uuid");
							if (getUuid().equalsIgnoreCase(uuid)) {
								String task = json.getString("task");
								TaskCommand command = json.getObject("command", TaskCommand.class);
								ApplicationContext context = ApplicationContextHolder.getApplicationContext();
								ScheduleContext scheduleContext = context.getBean(ScheduleContext.class);
								scheduleContext.execute(task, command);
							}
						}
					}, commandKey());

				});
			});
			commandMonitorThread.setDaemon(true);
			commandMonitorThread.start();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void signalCommand(String task, TaskCommand command) {
		RedisContexts.run(jedis -> {
			Set<String> keys = jedis.keys(keyPrefix() + "*:alive");
			for (String key : keys) {
				key = key.substring(keyPrefix().length(), key.length() - ":alive".length());
				if (getUuid().equals(key)) {
					continue;
				}
				JSONObject json = new JSONObject();
				json.put("uuid", key);
				json.put("task", task);
				json.put("command", command);
				jedis.publish(commandKey(), json.toJSONString());
			}
		});
	}

	private String commandKey() {
		return keyPrefix() + "command";
	}

	private String keyPrefix() {
		return KEY_PREFIX;
	}

	public int getHeartTime() {
		if (heartTime > 0) {
			return heartTime;
		}
		return HEART_TIME;
	}
}
