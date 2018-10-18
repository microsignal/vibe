package io.microvibe.util.spring;

import java.util.Locale;

import javax.servlet.ServletContext;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.WebApplicationContext;

/**
 * 获取 {@linkplain ApplicationContext}的工具类
 *
 * @author Tranquil
 */
public class ApplicationContextHolder implements ApplicationContextAware {

	static String contextPath = null;
	private static ApplicationContext appContext = null;

	public static boolean containsBean(String name) {
		return appContext.containsBean(name);
	}

	public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
		return appContext.isSingleton(name);
	}

	public static Class<? extends Object> getType(String name) throws NoSuchBeanDefinitionException {
		return appContext.getType(name);
	}

	public static String[] getAliases(String name) throws NoSuchBeanDefinitionException {
		return appContext.getAliases(name);
	}

	public static String getContextPath() {
		if (contextPath != null) {
			return contextPath;
		}
		ServletContext servletContext = getServletContext();
		if (servletContext != null) {
			servletContext.getContextPath();
		}
		return null;
	}

	public static ApplicationContext getApplicationContext() {
		return appContext;
	}

	public static String getApplicationName() {
		return appContext.getApplicationName();
	}

	public static String getMessage(String code, Object params[], String defaultDesc, Locale local) {
		return appContext.getMessage(code, params, defaultDesc, local);
	}

	public static <T> T getBean(String beanId, Class<T> clazz) throws BeansException {
		return appContext.getBean(beanId, clazz);
	}

	public static <T> T getBean(Class<T> clazz) throws BeansException {
		return appContext.getBean(clazz);
	}

	public static Object getBean(String beanId) throws BeansException {
		return appContext.getBean(beanId);
	}

	public static WebApplicationContext getWebApplicationContext() {
		if (appContext instanceof WebApplicationContext) {
			return (WebApplicationContext) appContext;
		}
		return null;
	}

	public static ServletContext getServletContext() {
		if (appContext instanceof WebApplicationContext) {
			return ((WebApplicationContext) appContext).getServletContext();
		}
		return null;
	}

	public void setApplicationContext(ApplicationContext newAppContext) throws BeansException {
		appContext = newAppContext;
	}
}
