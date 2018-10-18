/*
 * Copyright (c) 2016-8-31
 * created by alex
 */

package io.microvibe.booster.commons.validation;

import io.microvibe.booster.commons.validation.validator.EqualLengthValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 等于长度注解
 *
 * @author huangyong
 * @since 1.0.0
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EqualLengthValidator.class)
public @interface EqualLength {

	String message() default "equal_length";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	int value();
}
