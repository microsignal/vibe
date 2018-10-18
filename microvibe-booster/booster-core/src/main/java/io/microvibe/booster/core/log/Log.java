package io.microvibe.booster.core.log;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 记录日志
 *
 * @author Qt
 * @since May 15, 2018
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
@Inherited
public @interface Log {

	/**
	 * 日志级别
	 *
	 * @return
	 */
	Level level() default Level.INFO;
	/**
	 * 日志级别
	 *
	 * @return
	 */
	Level errorLevel() default Level.ERROR;

	/**
	 * 日志切点
	 *
	 * @return
	 */
	Pointcut[] pointcut() default {Pointcut.Before, Pointcut.After};

	/**
	 * 功能模块名
	 *
	 * @return
	 */
	String module() default "";

	/**
	 * 日志内容
	 *
	 * @return
	 */
	@AliasFor("value")
	String content() default "";

	/**
	 * 日志内容
	 *
	 * @return
	 */
	@AliasFor("content")
	String value() default "";

	enum Pointcut {
		Before(1), AfterReturning(2), AfterThrowing(4), After(6);

		private final int mask;

		private Pointcut(int mask) {
			this.mask = mask;
		}

		public int mask() {
			return mask;
		}
	}

	enum Level {
		DEBUG, INFO, WARN, ERROR;
	}

}
