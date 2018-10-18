package io.microvibe.booster.core.accessor;

import io.microvibe.booster.commons.spring.ApplicationContextHolder;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Map;

public class ValidatorAccessor {

	public static Validator getValidator() {
		Validator validator = null;
		try {
			validator = ApplicationContextHolder.getBean("validator", Validator.class);
		} catch (RuntimeException e) {
		}
		if (validator == null) {
			try {
				Map<String, Validator> beans = ApplicationContextHolder.getApplicationContext()
					.getBeansOfType(Validator.class);
				validator = beans.values().iterator().next();
			} catch (RuntimeException e) {
			}
		}
		if (validator == null) {
			ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
			validator = factory.getValidator();
		}
		return validator;
	}
}
