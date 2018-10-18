package io.microvibe.booster.core.schedule;

import io.microvibe.booster.commons.spring.ApplicationContextHolder;
import io.microvibe.booster.core.schedule.impl.LocalClusterScheduler;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

public interface ClusterScheduler {

	public static ClusterScheduler getClusterScheduler() {
		ClusterScheduler clusterScheduler;
		ApplicationContext context = ApplicationContextHolder.getApplicationContext();
		if (context != null) {
			try {
				clusterScheduler = context.getBean(ClusterScheduler.class);
			} catch (BeansException e) {
				clusterScheduler = LocalClusterScheduler.instance();
			}
		} else {
			clusterScheduler = LocalClusterScheduler.instance();
		}
		return clusterScheduler;
	}

	String getUuid();

	void lock(String id);

	boolean tryLock(String id);

	void unlock(String id);

	void keepAlive();

	void startCommandMonitor();

	void signalCommand(String task, TaskCommand command);
}
