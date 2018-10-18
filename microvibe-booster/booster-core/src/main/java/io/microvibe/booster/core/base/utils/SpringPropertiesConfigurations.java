package io.microvibe.booster.core.base.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.bind.PropertiesConfigurationFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.*;
import org.springframework.validation.BindException;

import java.util.Iterator;

/**
 * @author Qt
 * @since Aug 14, 2018
 */
@Slf4j
public class SpringPropertiesConfigurations {

	public static <T> T createBean(ApplicationContext context, Class<T> clazz) {
		AutowireCapableBeanFactory autowireCapableBeanFactory = context.getAutowireCapableBeanFactory();
		T bean = autowireCapableBeanFactory.createBean(clazz);
		return bean;
	}

	public static <T> T createBean(ApplicationContext context, String prefix, Class<T> clazz) {
		AutowireCapableBeanFactory autowireCapableBeanFactory = context.getAutowireCapableBeanFactory();
		T bean = autowireCapableBeanFactory.createBean(clazz);
		bindProperties(context, prefix, bean);
		return bean;
	}

	public static <T> void bindProperties(ApplicationContext context, String prefix, T bean) {
		PropertiesConfigurationFactory<T> factory = new PropertiesConfigurationFactory<>(bean);
		factory.setApplicationContext(context);
		factory.setConversionService(getConversionService(context));
		factory.setPropertySources(propertySources(context.getEnvironment()));

		factory.setTargetName(prefix);
		try {
			factory.bindPropertiesToTarget();
		} catch (BindException e) {
			log.error(e.getMessage(), e);
		}
	}

	public static ConversionService getConversionService(ApplicationContext context) {
		ConversionService conversionService;
		try {
			conversionService = context.getBean(ConversionService.class);
		} catch (BeansException e) {
			conversionService = new DefaultConversionService();
		}
		return conversionService;
	}

	public static PropertySources propertySources(Environment environment) {
		if (environment instanceof ConfigurableEnvironment) {
			MutablePropertySources propertySources = ((ConfigurableEnvironment) environment).getPropertySources();
			return new FlatPropertySources(propertySources);
		}
		// empty, so not very useful, but fulfils the contract
		log.warn("Unable to obtain PropertySources from "
			+ "PropertySourcesPlaceholderConfigurer or Environment");
		return new MutablePropertySources();
	}

	private static class FlatPropertySources implements PropertySources {

		private PropertySources propertySources;

		FlatPropertySources(PropertySources propertySources) {
			this.propertySources = propertySources;
		}

		@Override
		public Iterator<PropertySource<?>> iterator() {
			MutablePropertySources result = getFlattened();
			return result.iterator();
		}

		@Override
		public boolean contains(String name) {
			return get(name) != null;
		}

		@Override
		public PropertySource<?> get(String name) {
			return getFlattened().get(name);
		}

		private MutablePropertySources getFlattened() {
			MutablePropertySources result = new MutablePropertySources();
			for (PropertySource<?> propertySource : this.propertySources) {
				flattenPropertySources(propertySource, result);
			}
			return result;
		}

		private void flattenPropertySources(PropertySource<?> propertySource,
			MutablePropertySources result) {
			Object source = propertySource.getSource();
			if (source instanceof ConfigurableEnvironment) {
				ConfigurableEnvironment environment = (ConfigurableEnvironment) source;
				for (PropertySource<?> childSource : environment.getPropertySources()) {
					flattenPropertySources(childSource, result);
				}
			} else {
				result.addLast(propertySource);
			}
		}

	}
}
