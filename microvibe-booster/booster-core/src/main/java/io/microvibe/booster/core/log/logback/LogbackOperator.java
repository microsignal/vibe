package io.microvibe.booster.core.log.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import io.microvibe.booster.commons.spring.ApplicationContextHolder;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.util.*;

public class LogbackOperator implements InitializingBean {

	private volatile static LogbackOperator instance;
	//	private List<Logger> loggers = Collections.synchronizedList(new ArrayList<>());
	private Map<String, Logger> loggersCache = Collections.synchronizedMap(new LinkedHashMap<>());
	private LoggerContext context;

	public static LogbackOperator instance() {
		if (instance == null) {
			synchronized (LogbackOperator.class) {
				if (instance == null) {
					try {
						instance = ApplicationContextHolder.getBean(LogbackOperator.class);
					} catch (Exception e) {
						instance = new LogbackOperator();
						instance.afterPropertiesSet();
					}
				}
			}
		}
		return instance;
	}

	@Override
	public void afterPropertiesSet() {
		ILoggerFactory iLoggerFactory = LoggerFactory.getILoggerFactory();
		if (iLoggerFactory instanceof LoggerContext) {
			context = (LoggerContext) iLoggerFactory;
			List<Logger> loggerList = context.getLoggerList();
			for (Logger logger : loggerList) {
				if (logger.getLevel() != null) {
					loggersCache.put(logger.getName(), logger);
				}
			}
		}
	}

	public Collection<Logger> getLoggers() {
		return loggersCache.values();
	}

	public final Logger getLogger(final Class<?> clazz) {
		return getLogger(clazz.getName());
	}

	public final Logger getLogger(final String logName) {
		if (loggersCache.containsKey(logName)) {
			return loggersCache.get(logName);
		}
		synchronized (LogbackOperator.class) {
			if (loggersCache.containsKey(logName)) {
				return loggersCache.get(logName);
			}
			Logger logger = context.getLogger(logName);
			if (logger.getLevel() != null) {
				loggersCache.put(logName, logger);
			}
			return logger;
		}
	}

	public Level getLevel(Class<?> clazz) {
		return getLogger(clazz).getLevel();
	}

	public Level getEffectiveLevel(Class<?> clazz) {
		return getLogger(clazz).getEffectiveLevel();
	}

	public Level getLevel(String logName) {
		return getLogger(logName).getLevel();
	}

	public Level getEffectiveLevel(String logName) {
		return getLogger(logName).getEffectiveLevel();
	}

	public void setLevel(Class<?> clazz, Level level) {
		setLevel(clazz.getName(), level);
	}

	public void setLevel(String logName, Level level) {
		Logger logger = getLogger(logName);
		logger.setLevel(level);
		if (!loggersCache.containsKey(logName)) {
			loggersCache.put(logName, logger);
		}
	}


}
