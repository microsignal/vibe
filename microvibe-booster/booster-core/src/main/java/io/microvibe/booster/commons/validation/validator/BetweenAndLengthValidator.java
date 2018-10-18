/*
 * Copyright (c) 2016-9-8 alex
 */

package io.microvibe.booster.commons.validation.validator;

import io.microvibe.booster.commons.validation.BetweenAndLength;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 最大长度注解验证器
 *
 * @author huangyong
 * @since 1.0.0
 */
public class BetweenAndLengthValidator implements ConstraintValidator<BetweenAndLength, String> {

	private int min;

	private int max;

	@Override
	public void initialize(BetweenAndLength constraintAnnotation) {
		min = constraintAnnotation.min();
		max = constraintAnnotation.max();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {


		return value.length() <= max && value.length() >= min;
	}
}
