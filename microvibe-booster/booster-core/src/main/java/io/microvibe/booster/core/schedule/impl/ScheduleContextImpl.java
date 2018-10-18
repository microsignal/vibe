package io.microvibe.booster.core.schedule.impl;

import io.microvibe.booster.core.base.mybatis.annotation.AfterMybatisScanner;
import io.microvibe.booster.core.schedule.*;
import io.microvibe.booster.core.schedule.err.ScheduleException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.util.MethodInvoker;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * @author Qt
 * @since Aug 06, 2018
 */
@Component
@AfterMybatisScanner
@Slf4j
public class ScheduleContextImpl implements ScheduleContext, ApplicationListener<ContextRefreshedEvent> {
	private Map<String, Set<ScheduledFuture<?>>> taskPool = new ConcurrentHashMap<>();
	private Map<String, TaskDefinition> enabledTasks = new ConcurrentHashMap<>();
	private Map<String, TaskDefinition> disabledTasks = new ConcurrentHashMap<>();


	@Autowired
	private ApplicationContext applicationContext;
	@Autowired(required = false)
	private TaskScheduler taskScheduler;

	@Autowired(required = false)
	private TaskDefinitionDao taskDefinitionDao;
	@Autowired(required = false)
	private ClusterScheduler clusterScheduler;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (!(event instanceof ContextRefreshedEvent)) {
			return;
		}
		// root application context 没有parent.
		if (((ContextRefreshedEvent) event).getApplicationContext().getParent() != null) {
			return;
		}
		// scanning controller methods
		try {
			ApplicationContext context = ((ContextRefreshedEvent) event).getApplicationContext();
			initTask();
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
		}
	}

	private void initTask() {
		if (taskDefinitionDao != null) {
			List<TaskDefinition> tasks = taskDefinitionDao.findAll();
			for (TaskDefinition task : tasks) {
				Boolean enabled = task.getEnabled();
				if (!Boolean.TRUE.equals(enabled)) {
					// 未启用
					disabledTasks.put(task.getTaskId(), task);
					continue;
				}
				try {
					add(task.getTaskId(), taskScheduler.schedule(createTask(task), new CronTrigger(task.getCron())));
					enabledTasks.put(task.getTaskId(), task);
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			}
		}
		if (clusterScheduler == null) {
			this.clusterScheduler = ClusterScheduler.getClusterScheduler();
			clusterScheduler.keepAlive();
			clusterScheduler.startCommandMonitor();
		}
	}

	private Runnable createTask(TaskDefinition taskDefinition) {
		ScheduledMethodInvoker scheduledMethodInvoker = new ScheduledMethodInvoker(taskDefinition, clusterScheduler);
		MethodInvoker methodInvoker = new MethodInvoker();
		methodInvoker.setTargetObject(scheduledMethodInvoker);
		methodInvoker.setTargetMethod("invoke");
		try {
			methodInvoker.prepare();
		} catch (ClassNotFoundException e) {
			throw new ScheduleException(e);
		} catch (NoSuchMethodException e) {
			throw new ScheduleException(e);
		}
		return new Runnable() {
			@Override
			public void run() {
				try {
					methodInvoker.invoke();
				} catch (Exception e) {
					log.error("任务[" + taskDefinition.getTaskId() + "]执行异常", e);
					throw new ScheduleException(e);
				}
			}
		};

	}

	/**
	 * 启动或更新任务
	 */
	private boolean doStart(TaskDefinition taskDefinition) {
		String taskId = taskDefinition.getTaskId();
		Boolean enabled = taskDefinition.getEnabled();
		if (enabledTasks.containsKey(taskId)) {
			if (!Boolean.TRUE.equals(enabled)) {
				doStop(taskId);
				disabledTasks.put(taskId, taskDefinition);
				enabledTasks.remove(taskId);
				return true;
			} else {
				TaskDefinition old = enabledTasks.get(taskId);
				if (!old.getClassName().equalsIgnoreCase(taskDefinition.getClassName())
					|| old.getMethodName().equalsIgnoreCase(taskDefinition.getMethodName())
					|| old.getCron().equalsIgnoreCase(taskDefinition.getCron())) {
					doStop(taskId);
					add(taskId, taskScheduler.schedule(createTask(taskDefinition), new CronTrigger(taskDefinition.getCron())));
					return true;
				}
			}
		} else {
			if (Boolean.TRUE.equals(enabled)) {
				enabledTasks.put(taskId, taskDefinition);
				disabledTasks.remove(taskId);
				add(taskId, taskScheduler.schedule(createTask(taskDefinition), new CronTrigger(taskDefinition.getCron())));
				return true;
			}
		}
		return false;
	}

	private void add(String taskId, ScheduledFuture<?> future) {
		Set<ScheduledFuture<?>> scheduledFutures = taskPool.get(taskId);
		if (scheduledFutures == null) {
			scheduledFutures = new LinkedHashSet<>();
			taskPool.put(taskId, scheduledFutures);
		}
		scheduledFutures.add(future);
	}

	@Override
	public void start(String taskId) {
		if (taskDefinitionDao != null) {
			TaskDefinition taskDefinition = taskDefinitionDao.getById(taskId);
			if (taskDefinition != null) {
				if (doStart(taskDefinition)) {
					// clusterScheduler.signalCommand(taskId, TaskCommand.START);
					if (taskDefinition.getStatus() == null || taskDefinition.getStatus() == TaskRunningState.TERMINATED) {
						taskDefinitionDao.updateRunningState(taskId, TaskRunningState.WAITING, "启动任务");
					}
				}
			}
		}
		clusterScheduler.signalCommand(taskId, TaskCommand.START);
	}


	@Override
	public void stop(String taskId) {
		doStop(taskId);
		clusterScheduler.signalCommand(taskId, TaskCommand.STOP);
		if (taskDefinitionDao != null) {
			taskDefinitionDao.updateRunningState(taskId, TaskRunningState.TERMINATED, "终止任务");
		}
	}

	@Override
	public void execute(String taskId, TaskCommand command) {
		switch (command) {
			case START: {
				if (taskDefinitionDao != null) {
					TaskDefinition taskDefinition = taskDefinitionDao.getById(taskId);
					if (taskDefinition != null) {
						doStart(taskDefinition);
					}
				}
				break;
			}
			case STOP: {
				doStop(taskId);
				break;
			}
			default:
		}
	}

	private void doStop(String taskId) {
		Set<ScheduledFuture<?>> scheduledFutures = taskPool.get(taskId);
		if (scheduledFutures != null) {
			Iterator<ScheduledFuture<?>> iterator = scheduledFutures.iterator();
			while (iterator.hasNext()) {
				iterator.next().cancel(true);
				iterator.remove();
			}
		}
		if (enabledTasks.containsKey(taskId)) {
			disabledTasks.put(taskId, enabledTasks.get(taskId));
			enabledTasks.remove(taskId);
		}
	}

}
