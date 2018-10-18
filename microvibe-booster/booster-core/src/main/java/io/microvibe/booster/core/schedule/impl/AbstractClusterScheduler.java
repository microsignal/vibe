package io.microvibe.booster.core.schedule.impl;

import io.microvibe.booster.commons.spring.ApplicationContextHolder;
import io.microvibe.booster.core.schedule.ClusterScheduler;
import io.microvibe.booster.core.schedule.ScheduleContext;
import io.microvibe.booster.core.schedule.TaskCommand;
import org.springframework.context.ApplicationContext;

import java.util.UUID;

/**
 * @author Qt
 * @since Aug 06, 2018
 */
public abstract class AbstractClusterScheduler implements ClusterScheduler {

	protected String uuid;

	public AbstractClusterScheduler() {
		this.uuid = UUID.randomUUID().toString();
	}

	@Override
	public String getUuid() {
		return uuid;
	}

	@Override
	public void lock(String id) {
		if (!tryLock(id)) {
			throw new IllegalStateException(id);
		}
	}

	@Override
	public boolean tryLock(String id) {
		return true;
	}

	@Override
	public void unlock(String id) {
	}

	@Override
	public void keepAlive() {
	}

	@Override
	public void startCommandMonitor() {

	}

	@Override
	public void signalCommand(String task, TaskCommand command) {
	}
}
