package io.microvibe.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LogUtil 日志工具类<br>
 *
 * 统一使用此工具类进行记录日志, 便于对日志格式的统一控制
 *
 * @author 小痴
 * @date 2017年9月4日
 */
public class LogUtil {

    // 消息与数据之间的分隔符
    private static final String SPLIT_OBJS = " objs:";

    /**
     * 获取默认Logger
     */
    public static Logger getDefaultLogger() {
        int defaultStackIndex = 3; // 默认堆栈待优化
        StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
        if (stacks == null || stacks.length == 0) {
            return LoggerFactory.getLogger(Thread.currentThread().getClass());
        }
        if (stacks.length < defaultStackIndex) {
            defaultStackIndex = stacks.length - 1;
        }
        return LoggerFactory.getLogger(stacks[defaultStackIndex].getClassName());
    }

    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    public static Logger getLogger(String className) {
        return LoggerFactory.getLogger(className);
    }

    /**
     * 错误级别日志<br>
     *
     * 适合于catch异常时记日志，考虑加入发邮件功能
     *
     * @param cause 异常实例
     * @param objs 可变参数，业务对象数组，会以Json的格式写入日志；
     */
    public static void error(Throwable cause, String message) {
        Logger logger = getDefaultLogger();
        error(logger, cause, message);
    }

    public static void error(Throwable cause, String message, Object... objs) {
        Logger logger = getDefaultLogger();
        error(logger, cause, message, objs);
    }

    public static void error(Class<?> loggerClass, Throwable cause, String message) {
        Logger logger = getLogger(loggerClass);
        error(logger, cause, message);
    }

    public static void error(Class<?> loggerClass, Throwable cause, String message, Object... objs) {
        Logger logger = getLogger(loggerClass);
        error(logger, cause, message, objs);
    }

    public static void error(String loggerName, Throwable cause, String message) {
        Logger logger = getLogger(loggerName);
        error(logger, cause, message);
    }

    public static void error(String loggerName, Throwable cause, String message, Object... objs) {
        Logger logger = getLogger(loggerName);
        error(logger, cause, message, objs);
    }

    // 统一日志格式控制
    private static void error(Logger logger, Throwable cause, String message, Object... objs) {
        if (objs != null && objs.length > 0) {
            logger.error(message + SPLIT_OBJS + JsonUtil.object2Json(objs), cause);
        } else {
            logger.error(message, cause);
        }
    }

    /**
     * 警告级别日志<br>
     *
     * 当数据或字段存在异常时，可以记录警告日志
     *
     * @param message
     */
    public static void warn(String message) {
        Logger logger = getDefaultLogger();
        warn(logger, message);
    }

    public static void warn(String message, Object... objs) {
        Logger logger = getDefaultLogger();
        warn(logger, message, objs);
    }

    public static void warn(Class<?> loggerClass, String message) {
        Logger logger = getLogger(loggerClass);
        warn(logger, message);
    }

    public static void warn(Class<?> loggerClass, String message, Object... objs) {
        Logger logger = getLogger(loggerClass);
        warn(logger, message, objs);
    }

    public static void warn(String loggerName, String message) {
        Logger logger = getLogger(loggerName);
        warn(logger, message);
    }

    public static void warn(String loggerName, String message, Object... objs) {
        Logger logger = getLogger(loggerName);
        warn(logger, message, objs);
    }

    // 统一日志格式控制
    private static void warn(Logger logger, String message, Object... objs) {
        if (objs != null && objs.length > 0) {
            logger.warn(message + SPLIT_OBJS + JsonUtil.object2Json(objs));
        } else {
            logger.warn(message);
        }
    }

    /**
     * 信息日志
     *
     * 重要数据发生更新时，记录日志便于排查问题
     *
     * @param message
     */
    public static void info(String message) {
        Logger logger = getDefaultLogger();
        info(logger, message);
    }

    public static void info(String message, Object... objs) {
        Logger logger = getDefaultLogger();
        info(logger, message, objs);
    }

    public static void info(Class<?> loggerClass, String message) {
        Logger logger = getLogger(loggerClass);
        info(logger, message);
    }

    public static void info(Class<?> loggerClass, String message, Object... objs) {
        Logger logger = getLogger(loggerClass);
        info(logger, message, objs);
    }

    public static void info(String loggerName, String message) {
        Logger logger = getLogger(loggerName);
        info(logger, message);
    }

    public static void info(String loggerName, String message, Object... objs) {
        Logger logger = getLogger(loggerName);
        info(logger, message, objs);
    }

    // 统一日志格式控制
    private static void info(Logger logger, String message, Object... objs) {
        if (objs != null && objs.length > 0) {
            logger.info(message + SPLIT_OBJS + JsonUtil.object2Json(objs));
        } else {
            logger.info(message);
        }
    }

    /**
     * 调试日志
     *
     * 重要数据发生更新时，记录日志便于排查问题
     *
     * @param message
     */
    public static void debug(String message) {
        Logger logger = getDefaultLogger();
        debug(logger, message);
    }

    public static void debug(String message, Object... objs) {
        Logger logger = getDefaultLogger();
        debug(logger, message, objs);
    }

    public static void debug(Class<?> loggerClass, String message) {
        Logger logger = getLogger(loggerClass);
        debug(logger, message);
    }

    public static void debug(Class<?> loggerClass, String message, Object... objs) {
        Logger logger = getLogger(loggerClass);
        debug(logger, message, objs);
    }

    public static void debug(String loggerName, String message) {
        Logger logger = getLogger(loggerName);
        debug(logger, message);
    }

    public static void debug(String loggerName, String message, Object... objs) {
        Logger logger = getLogger(loggerName);
        debug(logger, message, objs);
    }

    // 统一日志格式控制
    private static void debug(Logger logger, String message, Object... objs) {
        if (objs != null && objs.length > 0) {
            logger.debug(message + SPLIT_OBJS + JsonUtil.object2Json(objs));
        } else {
            logger.debug(message);
        }
    }

}
