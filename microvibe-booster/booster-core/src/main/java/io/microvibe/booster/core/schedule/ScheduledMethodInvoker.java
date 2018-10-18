package io.microvibe.booster.core.schedule;

import io.microvibe.booster.commons.spring.ApplicationContextHolder;
import io.microvibe.booster.core.schedule.annotation.ClusterScheduled;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.MethodInvoker;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringValueResolver;

import java.lang.reflect.Method;

/**
 * @author Qt
 * @since Aug 06, 2018
 */
@Slf4j
public class ScheduledMethodInvoker {

	private Object target;
	private Method method;
	private String taskId;
	private String taskIdForShow;
	private MethodInvoker methodInvoker;

	private ClusterScheduler clusterScheduler;
	private StringValueResolver stringValueResolver;
	private TaskDefinitionDao taskDefinitionDao;
	private boolean concurrent = false;


	public ScheduledMethodInvoker(Object target, Method method, ClusterScheduler clusterScheduler) {
		this(target, method, clusterScheduler, null);
	}

	public ScheduledMethodInvoker(Object target, Method method) {
		this(target, method, null, null);
	}

	public ScheduledMethodInvoker(Object target, Method method, ClusterScheduler clusterScheduler, StringValueResolver stringValueResolver) {
		this.clusterScheduler = clusterScheduler;
		this.stringValueResolver = stringValueResolver;
		this.target = target;
		this.method = AopUtils.selectInvocableMethod(method, target.getClass());
		ReflectionUtils.makeAccessible(method);
		init();
	}

	public ScheduledMethodInvoker(TaskDefinition taskDefinition, ClusterScheduler clusterScheduler, StringValueResolver stringValueResolver) {
		this.clusterScheduler = clusterScheduler;
		this.stringValueResolver = stringValueResolver;
		ApplicationContext context = ApplicationContextHolder.getApplicationContext();
		try {
			taskId = taskDefinition.getTaskId();
			final MethodInvoker methodInvoker = new MethodInvoker();
			methodInvoker.setTargetMethod(taskDefinition.getMethodName());
			if (context.containsBean(taskDefinition.getClassName())) {
				target = context.getBean(taskDefinition.getClassName());
			} else {
				Class<?> aClass = Class.forName(taskDefinition.getClassName());
				try {
					target = context.getBean(aClass);
				} catch (BeansException e) {
					target = context.getAutowireCapableBeanFactory().createBean(aClass);
				}
			}
			methodInvoker.setTargetObject(target);
			methodInvoker.prepare();
			this.method = methodInvoker.getPreparedMethod();
			this.methodInvoker = methodInvoker;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new IllegalArgumentException(e);
		}
		init();
	}

	public ScheduledMethodInvoker(TaskDefinition taskDefinition) {
		this(taskDefinition, (ClusterScheduler) null, (StringValueResolver) null);
	}

	public ScheduledMethodInvoker(TaskDefinition taskDefinition, ClusterScheduler clusterScheduler) {
		this(taskDefinition, clusterScheduler, (StringValueResolver) null);
	}

	private void init() {
		if (ApplicationContextHolder.hasApplicationContext()) {
			try {
				taskDefinitionDao = ApplicationContextHolder.getBean(TaskDefinitionDao.class);
			} catch (BeansException e) {
			}
		}
		if (clusterScheduler == null) {
			clusterScheduler = ClusterScheduler.getClusterScheduler();
		}
		ClusterScheduled classAnno = AnnotationUtils.findAnnotation(target.getClass(), ClusterScheduled.class);
		ClusterScheduled methodAnno = AnnotationUtils.findAnnotation(method, ClusterScheduled.class);

		if (classAnno != null) {
			concurrent = classAnno.concurrent();
		}
		if (methodAnno != null) {
			if (StringUtils.isBlank(taskId)) {
				taskId = methodAnno.id();
			}
			concurrent = methodAnno.concurrent();
		}
		if (StringUtils.isBlank(taskId)) {
			taskId = method.getDeclaringClass().getCanonicalName() + "." + method.getName();
			taskIdForShow = toShortClassname(method.getDeclaringClass().getCanonicalName()) + "." + method.getName();
		} else {
			taskIdForShow = taskId;
		}
	}


	public void invoke() throws Throwable {
		if (concurrent) {
			invokeMethod();
		} else {
			if (clusterScheduler.tryLock(taskId)) {
				try {
					invokeMethod();
				} finally {
					clusterScheduler.unlock(taskId);
				}
			}
		}

	}

	private void invokeMethod() throws Throwable {
		if (methodInvoker != null) {
			try {
				if (taskDefinitionDao != null) {
					try {
						taskDefinitionDao.updateRunningState(taskId, TaskRunningState.RUNNING, "开始运行");
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}
				methodInvoker.invoke();
				if (taskDefinitionDao != null) {
					try {
						taskDefinitionDao.updateRunningState(taskId, TaskRunningState.SUCCESS, "运行成功");
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}
			} catch (Throwable throwable) {
				/*StringWriter stringWriter = new StringWriter();
				PrintWriter printWriter = new PrintWriter(stringWriter);
				throwable.printStackTrace(printWriter);
				String trace = stringWriter.toString();*/
				if (taskDefinitionDao != null) {
					try {
						taskDefinitionDao.updateRunningState(taskId, TaskRunningState.ERROR, "运行失败", throwable);
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}
				throw throwable;
			}
		} else {
			try {
				log.info("任务开始运行: {}", taskIdForShow);
				method.invoke(target);
				log.info("任务运行成功: {}", taskIdForShow);
			} catch (Throwable throwable) {
				log.error("任务运行失败: " + taskIdForShow, throwable);
				throw throwable;
			}

		}
	}

	private String toShortClassname(String classname) {
		String[] arr = classname.split("\\.");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < arr.length - 1; i++) {
			if (arr[i].length() > 0) {
				sb.append(arr[i].charAt(0)).append(".");
			}
		}
		sb.append(arr[arr.length - 1]);
		return sb.toString();
	}

	public String getTaskId() {
		return taskId;
	}
}


