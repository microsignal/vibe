package io.microvibe.booster.core.schedule.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Qt
 * @since Aug 06, 2018
 */

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ClusterScheduled {
	/**
	 * 任务id，同一时间只执行同一id的任务
	 * 默认为任务方法全名（包名+类名+方法名）
	 *
	 * @return
	 */
	String id() default "";


	boolean concurrent() default false;
}
