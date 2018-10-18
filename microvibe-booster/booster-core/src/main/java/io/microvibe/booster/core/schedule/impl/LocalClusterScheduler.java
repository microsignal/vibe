package io.microvibe.booster.core.schedule.impl;

import io.microvibe.booster.core.schedule.ClusterScheduler;
import io.microvibe.booster.core.schedule.TaskCommand;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Qt
 * @since Aug 06, 2018
 */
public class LocalClusterScheduler extends AbstractClusterScheduler implements ClusterScheduler {


	private static Map<String, Long> alived = new ConcurrentHashMap<>();
	private static Map<String, String> locked = new ConcurrentHashMap<>();
	private static volatile LocalClusterScheduler instance;
	private final Lock lock = new ReentrantLock();

	public static LocalClusterScheduler instance() {
		if (instance != null) {
			return instance;
		}
		synchronized (LocalClusterScheduler.class) {
			if (instance == null) {
				instance = new LocalClusterScheduler();
			}
			return instance;
		}
	}


	@Override
	public boolean tryLock(String id) {
		lock.lock();
		try {
			String lockedId = locked.get(id);
			if (lockedId == null || lockedId.equalsIgnoreCase(getUuid())) {
				locked.put(id, getUuid());
				alived.put(getUuid(), System.currentTimeMillis());
				return true;
			} else {
				if (alived.containsKey(lockedId)) {
					return false;
				} else {
					locked.put(id, getUuid());
					alived.put(getUuid(), System.currentTimeMillis());
					return true;
				}
			}
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void unlock(String id) {
		lock.lock();
		try {
			String lockedId = locked.get(id);
			if (getUuid().equalsIgnoreCase(lockedId)) {
				locked.remove(id);
			}
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void keepAlive() {
		alived.put(getUuid(), System.currentTimeMillis());
	}

	@Override
	public void startCommandMonitor() {

	}

	@Override
	public void signalCommand(String task, TaskCommand command) {

	}
}
