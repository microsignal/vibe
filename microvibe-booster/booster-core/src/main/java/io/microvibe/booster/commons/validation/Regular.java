/*
 * Copyright (c) 2016-9-7 alex
 */

package io.microvibe.booster.commons.validation;

import io.microvibe.booster.commons.validation.validator.RegularValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 正则验证
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RegularValidator.class)
public @interface Regular {

	String message() default "regular not pass";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	String value();
}
