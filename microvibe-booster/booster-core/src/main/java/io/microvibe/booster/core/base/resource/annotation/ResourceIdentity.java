package io.microvibe.booster.core.base.resource.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(ResourceIdentities.class)
public @interface ResourceIdentity {

	String SEPARATOR = ":";

	/**
	 * 是否需要继承父级资源
	 *
	 * @return
	 */
	boolean inherited() default true;

	/**
	 * 资源或操作的权限名称
	 */
	String value();

	/**
	 * 父级资源名称, 如不配置默认以分隔符从资源名截取
	 *
	 * @return
	 */
	String parent() default "";

	/**
	 * 描述信息
	 */
	String description() default "";

}
