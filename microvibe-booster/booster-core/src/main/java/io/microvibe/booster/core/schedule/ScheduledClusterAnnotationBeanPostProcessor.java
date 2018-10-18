package io.microvibe.booster.core.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

import java.lang.reflect.Method;

/**
 * @author Qt
 * @since Aug 06, 2018
 */
@Slf4j
@Component
public class ScheduledClusterAnnotationBeanPostProcessor extends ScheduledAnnotationBeanPostProcessor
	implements SchedulingConfigurer {


	@Value(value = "${spring.scheduling.cluster.heartTime:10}")
	private int heartTime = 10;
	private StringValueResolver embeddedValueResolver;

	@Autowired(required = false)
	private ClusterScheduler clusterScheduler;

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		if (clusterScheduler != null) {
			clusterScheduler.keepAlive();
			/*
			taskRegistrar.addFixedRateTask(new IntervalTask(new Runnable() {
				@Override
				public void run() {
					clusterScheduler.keepAlive();
				}
			}, heartTime * 1000, heartTime * 1000));
			*/
		}
	}

	@Override
	public void setEmbeddedValueResolver(StringValueResolver resolver) {
		super.setEmbeddedValueResolver(resolver);
		embeddedValueResolver = resolver;
	}

	@Override
	protected void processScheduled(Scheduled scheduled, Method method, Object bean) {
		try {
			bean = new ScheduledMethodInvoker(bean, method, clusterScheduler, embeddedValueResolver);
			method = ScheduledMethodInvoker.class.getMethod("invoke");
		} catch (NoSuchMethodException e) {
			log.error(e.getMessage(), e);
		}
		super.processScheduled(scheduled, method, bean);
	}
}
