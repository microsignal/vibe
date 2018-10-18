/*
 * Copyright (c) 2016-9-9 alex
 */

package io.microvibe.booster.commons.validation.validator;

import io.microvibe.booster.commons.validation.DeleteOrModifyUserValidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 正则注解验证器
 *
 * @author huangyong
 * @since 1.0.0
 */
public class DeleteModifyUserValidator implements ConstraintValidator<DeleteOrModifyUserValidation, Object[]> {

	private String value;

	@Override
	public void initialize(DeleteOrModifyUserValidation constraintAnnotation) {
		value = constraintAnnotation.value();
	}

	@Override
	public boolean isValid(Object[] value, ConstraintValidatorContext context) {

		return false;
	}
}
