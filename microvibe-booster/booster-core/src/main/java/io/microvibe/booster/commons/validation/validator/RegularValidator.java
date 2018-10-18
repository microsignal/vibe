/*
 * Copyright (c) 2016-9-7 alex
 */

package io.microvibe.booster.commons.validation.validator;

import io.microvibe.booster.commons.validation.Regular;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * 正则注解验证器
 *
 * @author huangyong
 * @since 1.0.0
 */
public class RegularValidator implements ConstraintValidator<Regular, String> {

	private String value;

	@Override
	public void initialize(Regular constraintAnnotation) {
		value = constraintAnnotation.value();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {

		Pattern pattern = Pattern.compile(this.value);
		return pattern.matcher(value).find();
	}

}
