/*
 * Copyright (c) 2016-8-31
 * created by alex
 */

package io.microvibe.booster.commons.validation.validator;

import io.microvibe.booster.commons.utils.StringUtils;
import io.microvibe.booster.commons.validation.IsNumeric;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 数字注解验证器
 *
 * @author huangyong
 * @since 1.0.0
 */
public class IsNumericValidator implements ConstraintValidator<IsNumeric, String> {

	@Override
	public void initialize(IsNumeric constraintAnnotation) {
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return StringUtils.isNumeric(value);
	}
}
