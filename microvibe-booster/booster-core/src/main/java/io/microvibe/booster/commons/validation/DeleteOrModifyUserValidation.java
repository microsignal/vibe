/*
 * Copyright (c) 2016-9-9 alex
 */

package io.microvibe.booster.commons.validation;

import io.microvibe.booster.commons.validation.validator.DeleteModifyUserValidator;

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
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DeleteModifyUserValidator.class)
public @interface DeleteOrModifyUserValidation {

	String message() default "";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	String value();
}
