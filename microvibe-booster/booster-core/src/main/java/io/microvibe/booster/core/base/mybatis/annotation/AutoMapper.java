package io.microvibe.booster.core.base.mybatis.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface AutoMapper {

	/**
	 * 持久化Entity类型
	 */
	@AliasFor("value")
	Class<?> entityClass() default void.class;

	@AliasFor("entityClass")
	Class<?> value() default void.class;
}
