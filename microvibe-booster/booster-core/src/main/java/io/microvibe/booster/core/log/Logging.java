package io.microvibe.booster.core.log;

import io.microvibe.booster.commons.utils.HttpWebUtils;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Logging {
	private static final Pattern ARGS_BIND_PATTERN = Pattern.compile("\\{\\}");

	public static org.slf4j.Logger getLogger() {
		return getDefaultLogger();// 默认堆栈待优化
	}

	public static org.slf4j.Logger getLogger(Class<?> clazz) {
		return LoggerFactory.getLogger(clazz);
	}

	public static org.slf4j.Logger getLogger(String className) {
		return LoggerFactory.getLogger(className);
	}

	private static org.slf4j.Logger getDefaultLogger() {
		return getDefaultLogger(4);// 默认堆栈待优化
	}

	private static org.slf4j.Logger getDefaultLogger(int stackIndex) {
		StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
		if (stacks == null || stacks.length == 0) {
			return LoggerFactory.getLogger(Thread.currentThread().getClass());
		}
		if (stacks.length < stackIndex) {
			stackIndex = stacks.length - 1;
		}
		return LoggerFactory.getLogger(stacks[stackIndex].getClassName());
	}

	public static String toStackTrace(Throwable throwable) {
		if (throwable == null) return null;
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		throwable.printStackTrace(printWriter);
		return stringWriter.toString();
	}

	public static String getStackTrace() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
		boolean skippedSelf = false;
		for (int i = 0; i < stacks.length; i++) {
			if (stacks[i].getClassName().startsWith(Logging.class.getName())) {
				skippedSelf = true;
				continue;
			}
			if (!skippedSelf) {
				continue;
			}
			pw.println(stacks[i].toString());
		}
		return sw.toString();
	}

	public static String getStackTrace(Class<?>... ingnoredClasses) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
		boolean skippedSelf = false;
		loop:
		for (int i = 0; i < stacks.length; i++) {
			StackTraceElement stack = stacks[i];
			if (stack.getClassName().startsWith(Logging.class.getName())) {
				skippedSelf = true;
				continue;
			}
			if (!skippedSelf) {
				continue;
			}
			for (Class<?> ingnoredClass : ingnoredClasses) {
				if (stack.getClassName().startsWith(ingnoredClass.getName())) {
					continue loop;
				}
			}
			pw.println(stack.toString());
		}
		return sw.toString();
	}

	public static String getIp() {
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		if (requestAttributes != null && requestAttributes instanceof ServletRequestAttributes) {
			return HttpWebUtils.getIpAddr(((ServletRequestAttributes) requestAttributes).getRequest());
		}
		return "unknown";
	}

	private static String format(String message, Object... args) {
		if (args.length > 0) {
			Matcher m = ARGS_BIND_PATTERN.matcher(message);
			int count = 0;
			int start = 0;
			StringBuilder sb = new StringBuilder();
			while (count < args.length) {
				if (!m.find(start)) {
					break;
				}
				sb.append(message.substring(start, m.start()));
				sb.append(args[count]);
				count++;
				start = m.end();
			}
			sb.append(message.substring(start, message.length()));
			return sb.toString();
		}
		return message;
	}

	/**
	 * 记录格式 [ip][用户名][操作][错误消息]
	 * <p/>
	 * 注意操作如下：
	 * loginError 登录失败
	 * loginSuccess 登录成功
	 * passwordError 密码错误
	 * changePassword 修改密码
	 * changeStatus 修改状态
	 *
	 * @param username
	 * @param op
	 * @param msg
	 * @param args
	 */
	public static void logUserInfo(String username, String op, String msg, Object... args) {
		StringBuilder s = new StringBuilder();
		s.append("[").append(getIp()).append("]");
		s.append("[").append(username).append("]");
		s.append("[").append(op).append("]");
		s.append("[").append(msg).append("]");
		getDefaultLogger().info(s.toString(), args);
	}

	public static void debug(Class<?> loggerClass, String message, Object... args) {
		getLogger(loggerClass).debug(message, message, args);
	}

	public static void debug(Class<?> loggerClass, Throwable cause, String message, Object... args) {
		getLogger(loggerClass).debug(format(message, args), cause);
	}

	public static void debug(String message, Object... args) {
		getDefaultLogger().debug(message, args);
	}

	public static void debug(String loggerName, String message, Object... args) {
		getLogger(loggerName).debug(message, message, args);
	}

	public static void debug(String loggerName, Throwable cause, String message, Object... args) {
		getLogger(loggerName).debug(format(message, args), cause);
	}

	public static void debug(Throwable cause, String message, Object... args) {
		getDefaultLogger().debug(format(message, args), cause);
	}

	public static void info(Class<?> loggerClass, String message, Object... args) {
		getLogger(loggerClass).info(message, message, args);
	}

	public static void info(Class<?> loggerClass, Throwable cause, String message, Object... args) {
		getLogger(loggerClass).info(format(message, args), cause);
	}

	public static void info(String message, Object... args) {
		getDefaultLogger().info(message, args);
	}

	public static void info(String loggerName, String message, Object... args) {
		getLogger(loggerName).info(message, message, args);
	}

	public static void info(String loggerName, Throwable cause, String message, Object... args) {
		getLogger(loggerName).info(format(message, args), cause);
	}

	public static void info(Throwable cause, String message, Object... args) {
		getDefaultLogger().info(format(message, args), cause);
	}

	public static void warn(Class<?> loggerClass, String message, Object... args) {
		getLogger(loggerClass).warn(message, message, args);
	}

	public static void warn(Class<?> loggerClass, Throwable cause, String message, Object... args) {
		getLogger(loggerClass).warn(format(message, args), cause);
	}

	public static void warn(String message, Object... args) {
		getDefaultLogger().warn(message, args);
	}

	public static void warn(String loggerName, String message, Object... args) {
		getLogger(loggerName).warn(message, message, args);
	}

	public static void warn(String loggerName, Throwable cause, String message, Object... args) {
		getLogger(loggerName).warn(format(message, args), cause);
	}

	public static void warn(Throwable cause, String message, Object... args) {
		getDefaultLogger().warn(format(message, args), cause);
	}

	public static void error(Class<?> loggerClass, String message, Object... args) {
		getLogger(loggerClass).error(message, message, args);
	}

	public static void error(Class<?> loggerClass, Throwable cause, String message, Object... args) {
		getLogger(loggerClass).error(format(message, args), cause);
	}

	public static void error(String message, Object... args) {
		getDefaultLogger().error(message, args);
	}

	public static void error(String loggerName, String message, Object... args) {
		getLogger(loggerName).error(message, message, args);
	}

	public static void error(String loggerName, Throwable cause, String message, Object... args) {
		getLogger(loggerName).error(format(message, args), cause);
	}

	public static void error(Throwable cause, String message, Object... args) {
		getDefaultLogger().error(format(message, args), cause);
	}
}
