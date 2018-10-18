/*
 * Copyright (c) 2016-9-8 alex
 */

package io.microvibe.booster.commons.validation;

import io.microvibe.booster.commons.validation.validator.BetweenAndLengthValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 最大长度注解
 *
 * @author huangyong
 * @since 1.0.0
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BetweenAndLengthValidator.class)
public @interface BetweenAndLength {

	String message() default "max_length";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	int min();

	int max();
}
