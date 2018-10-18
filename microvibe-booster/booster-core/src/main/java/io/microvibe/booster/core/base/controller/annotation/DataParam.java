package io.microvibe.booster.core.base.controller.annotation;

import io.microvibe.booster.core.api.ApiConstants;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataParam {
	public static final int DEFAULT_PAGE_NUMBER = 1;
	public static final int DEFAULT_PAGE_SIZE = 10;
	public static final String DEFAULT_NAME = ApiConstants.HTTP_PARAM_NAME;
	public static final String DEFAULT_VALUE = "{}";

	DataKey[] searchKeys() default {};

	@AliasFor("name")
	String value() default DEFAULT_NAME;

	@AliasFor("value")
	String name() default DEFAULT_NAME;

	boolean required() default true;

	String defaultValue() default DEFAULT_VALUE;

	/**
	 * 是否移除所有空白值的元素
	 * @return
	 */
	boolean cleanEmptyValues() default false;

	boolean pageRequired() default false;

	int defaultPageSize() default DEFAULT_PAGE_SIZE;

	int defaultPageNumber() default DEFAULT_PAGE_NUMBER;

}
