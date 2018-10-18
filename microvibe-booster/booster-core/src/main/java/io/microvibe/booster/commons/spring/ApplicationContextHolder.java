package io.microvibe.booster.commons.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.StandardServletEnvironment;

import javax.servlet.ServletContext;
import java.util.Locale;

/**
 * 获取 {@linkplain ApplicationContext}的工具类
 *
 * @author Qt
 * @since Oct 09, 2017
 */
@Component(ApplicationContextHolder.BEAN_ID)
@Slf4j
public class ApplicationContextHolder implements ApplicationContextAware {
	public static final String BEAN_ID = "applicationContextHolder";
	public static boolean REQUIRED = true;

	public static ApplicationContext getApplicationContext() {
		if (Holder.appContext != null) {
			return Holder.appContext;
		}
		if (REQUIRED) {
			synchronized (ApplicationContextHolder.class) {
				if (Holder.appContext == null) {
					AnnotationConfigApplicationContext appContext = new AnnotationConfigApplicationContext();
					String[] arr = ApplicationContextHolder.class.getPackage().getName().split("\\.");
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < 3; i++) {
						sb.append(arr[i]).append(".");
					}
					sb.deleteCharAt(sb.length() - 1);
					String basePackage = sb.toString();
					appContext.scan(basePackage);
					appContext.setEnvironment(new StandardServletEnvironment());
					appContext.addBeanFactoryPostProcessor(beanFactory -> {
						PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
						configurer.postProcessBeanFactory(beanFactory);
					});
					try {
						appContext.refresh();
					} catch (Exception e) {
						log.debug(e.getMessage(), e);
					}
					Holder.appContext = appContext;
				}
			}
		}
		return Holder.appContext;
	}

	@Override
	public void setApplicationContext(ApplicationContext newAppContext) throws BeansException {
		Holder.appContext = newAppContext;
		Holder.hasApplicationContext = true;
	}

	public static boolean hasApplicationContext() {
		return Holder.hasApplicationContext;
	}

	public static boolean containsBean(String name) {
		return getApplicationContext().containsBean(name);
	}

	public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
		return getApplicationContext().isSingleton(name);
	}

	public static boolean isSingletonQuietly(String name) {
		try {
			return getApplicationContext().isSingleton(name);
		} catch (NoSuchBeanDefinitionException e) {
			return false;
		}
	}

	public static Class<? extends Object> getType(String name) throws NoSuchBeanDefinitionException {
		return getApplicationContext().getType(name);
	}

	public static Class<? extends Object> getTypeQuietly(String name) {
		try {
			return getApplicationContext().getType(name);
		} catch (NoSuchBeanDefinitionException e) {
			return null;
		}
	}

	public static String[] getAliases(String name) throws NoSuchBeanDefinitionException {
		return getApplicationContext().getAliases(name);
	}

	public static String[] getAliasesQuietly(String name) {
		try {
			return getApplicationContext().getAliases(name);
		} catch (NoSuchBeanDefinitionException e) {
			return new String[0];
		}
	}

	public static String getContextPath() {
		String contextPath = Holder.contextPath;
		if (contextPath != null) {
			return contextPath;
		}
		ServletContext servletContext = getServletContext();
		if (servletContext != null) {
			Holder.contextPath = contextPath = servletContext.getContextPath();
		}
		return contextPath;
	}

	public static void setContextPath(String contextPath) {
		Holder.contextPath = contextPath;
	}

	public static String getApplicationName() {
		return getApplicationContext().getApplicationName();
	}

	public static String getMessage(String code, Object params[], String defaultDesc, Locale local) {
		return getApplicationContext().getMessage(code, params, defaultDesc, local);
	}

	public static <T> T getBean(String beanId, Class<T> clazz) throws BeansException {
		return getApplicationContext().getBean(beanId, clazz);
	}

	public static <T> T getBeanQuietly(String beanId, Class<T> clazz) {
		try {
			return getApplicationContext().getBean(beanId, clazz);
		} catch (BeansException e) {
			return null;
		}
	}

	public static <T> T getBean(Class<T> clazz) throws BeansException {
		return getApplicationContext().getBean(clazz);
	}

	public static <T> T getBeanQuietly(Class<T> clazz) {
		try {
			return getApplicationContext().getBean(clazz);
		} catch (BeansException e) {
			return null;
		}
	}

	public static Object getBean(String beanId) throws BeansException {
		return getApplicationContext().getBean(beanId);
	}

	public static Object getBeanQuietly(String beanId) {
		try {
			return getApplicationContext().getBean(beanId);
		} catch (BeansException e) {
			return null;
		}
	}

	public static WebApplicationContext getWebApplicationContext() {
		ApplicationContext applicationContext = getApplicationContext();
		if (applicationContext instanceof WebApplicationContext) {
			return (WebApplicationContext) applicationContext;
		}
		return null;
	}

	public static ServletContext getServletContext() {
		ApplicationContext applicationContext = getApplicationContext();
		if (applicationContext instanceof WebApplicationContext) {
			return ((WebApplicationContext) applicationContext).getServletContext();
		}
		return null;
	}

	private static class Holder {
		private static ApplicationContext appContext = null;
		private static boolean hasApplicationContext;
		private static String contextPath = null;
	}

}
