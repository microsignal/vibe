package io.microvibe.booster.config.monitor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * ApplicationContext会自动检查是否在定义文件中有实现了BeanPostProcessor接口的类，
 * 如果有的话，Spring容器会在每个Bean(其他的Bean)被初始化之前和初始化之后，
 * 分别调用实现了BeanPostProcessor接口的类的postProcessAfterInitialization()方法
 * 和postProcessBeforeInitialization()方法
 */
@Configuration
@Slf4j
@ConditionalOnProperty("system.config.trace")
public class TraceConfig implements BeanPostProcessor {
	private String toString(Object bean) {
		return bean.getClass().getName() + "@" + Integer.toHexString(bean.hashCode());
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
		throws BeansException {
		log.info("对象初始化开始[ " + beanName + " -> " + toString(bean) + " ]");
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		log.info("对象初始化成功[ " + beanName + " -> " + toString(bean) + " ]");
		return bean;
	}
}
