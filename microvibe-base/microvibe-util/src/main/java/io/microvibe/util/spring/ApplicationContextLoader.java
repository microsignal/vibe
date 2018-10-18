package io.microvibe.util.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import io.microvibe.util.err.ContextLoadException;

public class ApplicationContextLoader {

	static final Logger logger = LoggerFactory.getLogger(ApplicationContextLoader.class);
	private ApplicationContext appContext = null;
	private String[] springCfgFiles = {
		System.getProperty("springCfgPath", "classpath*:config/spring/**/*.xml")
	};

	public static ApplicationContextLoader newInstance() {
		return new ApplicationContextLoader();
	}

	public static ApplicationContextLoader newInstance(String[] springCfgFiles) {
		return new ApplicationContextLoader(springCfgFiles);
	}

	private ApplicationContextLoader() {
		super();
		init();
	}

	private ApplicationContextLoader(String[] springCfgFiles) {
		super();
		this.springCfgFiles = springCfgFiles;
		init();
	}

	private void init() {
		logger.info("初始化Spring配置...");
		try {
			appContext = new ClassPathXmlApplicationContext(springCfgFiles);
		} catch (BeansException e) {
			throw new ContextLoadException(e);
		}
	}

	public ApplicationContext getContext() {
		return appContext;
	}

	public <T> T getBean(String beanId, Class<T> clazz) throws BeansException {
		return appContext.getBean(beanId, clazz);
	}

	public <T> T getBean(Class<T> clazz) throws BeansException {
		return appContext.getBean(clazz);
	}

	public Object getBean(String beanId) throws BeansException {
		return appContext.getBean(beanId);
	}
}
