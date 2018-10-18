package io.microvibe.booster.config.task;

import io.microvibe.booster.commons.spring.AfterApplicationContextHolder;
import io.microvibe.booster.config.ProfileNames;
import io.microvibe.booster.core.env.TaskEnv;
import io.microvibe.booster.core.schedule.impl.RedisClusterScheduler;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @author Qt
 * @since Aug 11, 2018
 */
@SuppressWarnings("ALL")
@Configuration
@EnableAsync
public class TaskConfig {
	public static final String TASK_EXECUTOR_BEAN_ID = "executor";
	public static final String TASK_SCHEDULER_BEAN_ID = "scheduler";
	public static final String[] TASK_BEANS = new String[]{TASK_EXECUTOR_BEAN_ID, TASK_SCHEDULER_BEAN_ID};
	@Autowired
	TaskEnv taskEnv;


	@Bean({TASK_EXECUTOR_BEAN_ID, "taskExecutor"})
	@Primary
	@Profile(ProfileNames.SUT)
	TaskExecutor taskExecutorForTest() {
		ThreadPoolTaskExecutor taskExecutor = Mockito.mock(ThreadPoolTaskExecutor.class);
		return taskExecutor;
	}

	@Bean({TASK_SCHEDULER_BEAN_ID, "taskScheduler"})
	@Primary
	@Profile(ProfileNames.SUT)
	TaskScheduler taskSchedulerForTest() {
		ThreadPoolTaskScheduler taskScheduler = Mockito.mock(ThreadPoolTaskScheduler.class);
		return taskScheduler;
	}

	@Bean({TASK_EXECUTOR_BEAN_ID, "taskExecutor"})
	@AfterApplicationContextHolder
	TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(taskEnv.getCorePoolSize());
		taskExecutor.setMaxPoolSize(taskEnv.getMaxPoolSize());
		taskExecutor.setQueueCapacity(taskEnv.getQueueCapacity());
		return taskExecutor;
	}

	@Bean({TASK_SCHEDULER_BEAN_ID, "taskScheduler"})
	@AfterApplicationContextHolder
	TaskScheduler taskScheduler() {
		ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
		taskScheduler.setPoolSize(taskEnv.getSchedulerPoolSize());
		return taskScheduler;
	}

	@Bean
	@AfterApplicationContextHolder
	RedisClusterScheduler clusterScheduler() {
		return new RedisClusterScheduler();
	}
}
