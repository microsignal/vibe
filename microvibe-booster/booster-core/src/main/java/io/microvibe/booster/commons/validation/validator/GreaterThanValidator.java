/*
 * Copyright (c) 2016-8-31
 * created by alex
 */

package io.microvibe.booster.commons.validation.validator;

import io.microvibe.booster.commons.validation.GreaterThan;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 大于值注解验证器
 *
 * @author huangyong
 * @since 1.0.0
 */
public class GreaterThanValidator implements ConstraintValidator<GreaterThan, Integer> {

	private Integer value;

	@Override
	public void initialize(GreaterThan constraintAnnotation) {
		value = constraintAnnotation.value();
	}

	@Override
	public boolean isValid(Integer value, ConstraintValidatorContext context) {
		return value > this.value;
	}
}
