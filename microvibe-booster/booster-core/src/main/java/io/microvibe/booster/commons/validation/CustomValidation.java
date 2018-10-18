/*
 * Copyright (c) 2016-9-8 alex
 */

package io.microvibe.booster.commons.validation;

import io.microvibe.booster.commons.validation.custom.CustomValid;
import io.microvibe.booster.commons.validation.validator.CustomValidator;

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
@Target({ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CustomValidator.class)
public @interface CustomValidation {

	String message() default "custom";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	Class<? extends CustomValid> value();
}
