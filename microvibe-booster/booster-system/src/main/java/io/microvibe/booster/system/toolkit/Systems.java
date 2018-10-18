package io.microvibe.booster.system.toolkit;

import io.microvibe.booster.commons.spring.ApplicationContextHolder;
import io.microvibe.booster.commons.utils.HttpWebUtils;
import io.microvibe.booster.core.base.utils.RequestContextUtils;
import io.microvibe.booster.core.log.Log;
import io.microvibe.booster.core.log.Logging;
import io.microvibe.booster.system.entity.SysLog;
import io.microvibe.booster.system.enums.LogType;
import io.microvibe.booster.system.service.SysLogService;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @author Qt
 * @since Jul 19, 2018
 */
public class Systems {

	public static void debug(String logContent) {
		log(Log.Level.DEBUG, LogType.SYSTEM, logContent);
	}

	public static void info(String logContent) {
		log(Log.Level.INFO, LogType.SYSTEM, logContent);
	}

	public static void warn(String logContent) {
		log(Log.Level.WARN, LogType.SYSTEM, logContent);
	}

	public static void error(String logContent) {
		log(Log.Level.ERROR, LogType.SYSTEM, logContent);
	}

	public static void debug(String logContent, Throwable throwable) {
		log(Log.Level.DEBUG, LogType.SYSTEM, logContent, throwable);
	}

	public static void info(String logContent, Throwable throwable) {
		log(Log.Level.INFO, LogType.SYSTEM, logContent, throwable);
	}

	public static void warn(String logContent, Throwable throwable) {
		log(Log.Level.WARN, LogType.SYSTEM, logContent, throwable);
	}

	public static void error(String logContent, Throwable throwable) {
		log(Log.Level.ERROR, LogType.SYSTEM, logContent, throwable);
	}

	public static void debug(LogType logType, String logContent) {
		log(Log.Level.DEBUG, logType, logContent);
	}

	public static void info(LogType logType, String logContent) {
		log(Log.Level.INFO, logType, logContent);
	}

	public static void warn(LogType logType, String logContent) {
		log(Log.Level.WARN, logType, logContent);
	}

	public static void error(LogType logType, String logContent) {
		log(Log.Level.ERROR, logType, logContent);
	}


	public static void debug(LogType logType, String logContent, Throwable throwable) {
		log(Log.Level.DEBUG, logType, logContent, throwable);
	}

	public static void info(LogType logType, String logContent, Throwable throwable) {
		log(Log.Level.INFO, logType, logContent, throwable);
	}

	public static void warn(LogType logType, String logContent, Throwable throwable) {
		log(Log.Level.WARN, logType, logContent, throwable);
	}

	public static void error(LogType logType, String logContent, Throwable throwable) {
		log(Log.Level.ERROR, logType, logContent, throwable);
	}


	public static void log(Log.Level level, LogType logType, String logContent) {
		log(level, LogType.SYSTEM, logContent, null);
	}

	public static boolean isLogEnabled(Log.Level level, LogType logType) {
		Logger log = logType.getLogger();
		if (level == Log.Level.DEBUG) {
			return log.isDebugEnabled();
		} else if (level == Log.Level.INFO) {
			return log.isInfoEnabled();
		} else if (level == Log.Level.WARN) {
			return log.isWarnEnabled();
		} else if (level == Log.Level.ERROR) {
			return log.isErrorEnabled();
		} else {
			return false;
		}
	}

	public static void log(Log.Level level, LogType logType, String logContent, Throwable throwable) {
		if (!isLogEnabled(level, logType)) {
			return;
		}

		ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
		if (applicationContext != null) {
			try {
				SysLogService sysLogService = applicationContext.getBean(SysLogService.class);
				SysLog sysLog = new SysLog();
				sysLog.setLogLevel(level);
				sysLog.setLogType(logType);
				sysLog.setLogTime(new Date());
				sysLog.setLogContent(logContent);

				HttpServletRequest request = RequestContextUtils.currentHttpRequest();
				sysLog.setRequestIp(HttpWebUtils.getIpAddr(request));
				sysLog.setRequestUri(HttpWebUtils.getRequestURI(request));
				sysLog.setRequestMethod(HttpWebUtils.getRequestMethod(request));
				if (Users.isOnline()) {
					sysLog.setUserId(Users.getCurrentUserId());
					sysLog.setUserName(Users.getCurrentUser().getUsername());
				}
				StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
				for (int i = 0; i < stacks.length; i++) {
					if (stacks[i].getClassName().startsWith(Systems.class.getName())) {
						continue;
					}
					sysLog.setClassName(stacks[i].getClassName());
					sysLog.setMethodName(stacks[i].getMethodName());
					break;
				}
				sysLog.setLogStacktrace(Logging.toStackTrace(throwable));
				sysLogService.doLogAsync(sysLog);
			} catch (BeansException e) {
				slf4j(level, logType, logContent, throwable);
			}
		} else {
			slf4j(level, logType, logContent, throwable);
		}

	}

	private static void slf4j(Log.Level level, LogType logType, String logContent, Throwable throwable) {
		switch (level) {
			case DEBUG:
				logType.getLogger().debug(logContent, throwable);
				break;
			case INFO:
				logType.getLogger().info(logContent, throwable);
				break;
			case WARN:
				logType.getLogger().warn(logContent, throwable);
				break;
			case ERROR:
				logType.getLogger().error(logContent, throwable);
				break;
		}
	}

}
