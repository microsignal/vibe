package io.microvibe.booster.commons.schedule;

import io.microvibe.booster.commons.spring.ApplicationContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.mockito.Mockito;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newScheduledThreadPool;

@Slf4j
public class Schedules {

	public static final String POOL_SIZE_KEY = "system.schedule.poolSize";
	public static final String POOL_SIZE_ENV = "SYSTEM_SCHEDULE_POOLSIZE";
	public static final int DEFAULT_POOL_SIZE = 10;
	private static ScheduledExecutorService executor;

	private static void init() {
		int poolSize = 0;
		if (ApplicationContextHolder.hasApplicationContext()) {
			ApplicationContext context = ApplicationContextHolder.getApplicationContext();
			try {
				// 尝试获取上下文中注册的 ScheduledExecutorService 对象
				executor = context.getBean(ScheduledExecutorService.class);
				return;
			} catch (Exception e) {
				log.debug(e.getMessage(), e);
			}
			try {
				// 尝试获取上下文中注册的 ThreadPoolTaskScheduler 对象, 间接得到`executor`
				ThreadPoolTaskScheduler scheduler = context.getBean(ThreadPoolTaskScheduler.class);
				executor = scheduler.getScheduledExecutor();
				if (executor == null) { // mock
					executor = Mockito.mock(ScheduledExecutorService.class);
				}
				return;
			} catch (Exception e) {
				log.debug(e.getMessage(), e);
			}

			try {
				// 无法上下文中获取时, 准备构造参数
				Environment env = context.getEnvironment();
				String size = env.getProperty(POOL_SIZE_KEY);
				if (size != null) {
					poolSize = Integer.decode(size);
				}
			} catch (RuntimeException e) {
				log.debug(e.getMessage(), e);
			}
		}
		if (poolSize <= 0) {
			try {
				String size = System.getProperty(POOL_SIZE_KEY);
				if (size != null) {
					poolSize = Integer.decode(size);
				}
			} catch (RuntimeException e) {
			}
		}
		if (poolSize <= 0) {
			try {
				String size = System.getenv(POOL_SIZE_ENV);
				if (size != null) {
					poolSize = Integer.decode(size);
				}
			} catch (RuntimeException e) {
			}
		}
		if (poolSize <= 0) {
			poolSize = DEFAULT_POOL_SIZE;
		}
		// 构造 executor
		executor = newScheduledThreadPool(poolSize);
	}

	public static ScheduledExecutorService executor() {
		if (executor == null) {
			synchronized (Schedules.class) {
				if (executor == null) {
					init();
				}
			}
		}
		return executor;
	}

	public static void shutdown() {
		ScheduledExecutorService pool = executor;
		if (pool == null) {
			return;
		}
		pool.shutdown(); // Disable new tasks from being submitted
		try {
			// Wait a while for existing tasks to terminate
			if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
				pool.shutdownNow(); // Cancel currently executing tasks
				// Wait a while for tasks to respond to being cancelled
				if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
					System.err.println("Pool did not terminate");
				}
			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			pool.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
		executor = null;
	}

}
