package io.microvibe.booster.core.base.controller.annotation;

import com.baidu.unbiz.fluentvalidator.Validator;
import io.microvibe.booster.core.validation.validator.Noop;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(DataValids.class)
public @interface DataValid {

	String key() default "";

	/**
	 * 验证器列表，接受{@link Validator}实现类的数组，除了级联外需要处理的额外验证
	 */
	Class<? extends Validator> value() default Noop.class;

	String[] args() default {};

	String message() default "";

	/**
	 * 作用于Spring AOP时候，用于标示该参数验证是否启用failfast失败策略
	 */
	boolean isFailFast() default true;
}
