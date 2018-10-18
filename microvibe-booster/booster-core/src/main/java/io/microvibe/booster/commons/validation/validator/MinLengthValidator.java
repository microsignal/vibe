/*
 * Copyright (c) 2016-8-31
 * created by alex
 */

package io.microvibe.booster.commons.validation.validator;

import io.microvibe.booster.commons.validation.MinLength;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 最小长度注解验证器
 *
 * @author huangyong
 * @since 1.0.0
 */
public class MinLengthValidator implements ConstraintValidator<MinLength, String> {

	private int length;

	@Override
	public void initialize(MinLength constraintAnnotation) {
		length = constraintAnnotation.value();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return value.length() >= length;
	}
}
