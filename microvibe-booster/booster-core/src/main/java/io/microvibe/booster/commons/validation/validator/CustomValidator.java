/*
 * Copyright (c) 2016-9-8 alex
 */

package io.microvibe.booster.commons.validation.validator;

import io.microvibe.booster.commons.spring.ApplicationContextHolder;
import io.microvibe.booster.commons.validation.CustomValidation;
import io.microvibe.booster.commons.validation.custom.CustomValid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 正则注解验证器
 *
 * @author huangyong
 * @since 1.0.0
 */
public class CustomValidator implements ConstraintValidator<CustomValidation, Object> {

	private Class clazz;

	@Override
	public void initialize(CustomValidation constraintAnnotation) {
		clazz = constraintAnnotation.value();
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {

		CustomValid customValid = (CustomValid) ApplicationContextHolder.getBean(clazz);
		Object result = customValid.isValid(value);


		if (result == null) {
			return false;
		}
		return (boolean) result;
	}
}
