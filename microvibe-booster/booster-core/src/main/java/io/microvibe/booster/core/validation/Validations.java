package io.microvibe.booster.core.validation;

import com.baidu.unbiz.fluentvalidator.*;
import com.baidu.unbiz.fluentvalidator.jsr303.HibernateSupportedValidator;
import com.baidu.unbiz.fluentvalidator.registry.Registry;
import com.baidu.unbiz.fluentvalidator.registry.impl.SimpleRegistry;
import com.baidu.unbiz.fluentvalidator.registry.impl.SpringApplicationContextRegistry;
import io.microvibe.booster.commons.spring.ApplicationContextHolder;
import io.microvibe.booster.commons.utils.property.PropertyUtil;
import io.microvibe.booster.core.accessor.ValidatorAccessor;
import io.microvibe.booster.core.api.ApiException;
import io.microvibe.booster.core.api.ReplyCode;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;

import java.util.Arrays;
import java.util.List;

public class Validations {
	public static <T> void validate(T o, Validator... validator) {
		validate(o, null, null, validator);
	}

	public static <T> void validate(T o, Class[] groups, Validator... validator) {
		validate(o, groups, null, validator);
	}

	public static <T> void validate(T o, Class[] groups, Class[] excludeGroups, Validator... validator) {
		FluentValidator fluentValidator = FluentValidator.checkAll()
			.configure(getRegistry());
		if (validator != null && validator.length > 0) {
			ValidatorChain chain = new ValidatorChain();
			chain.setValidators(Arrays.asList(validator));
			fluentValidator.on(o, chain);
		}
		if (groups != null) {
			fluentValidator.setGroups(groups);
		}
		if (excludeGroups != null) {
			fluentValidator.setExcludeGroups(excludeGroups);
		}
		ComplexResult result = fluentValidator
			.on(o, new HibernateSupportedValidator<T>().setHiberanteValidator(ValidatorAccessor.getValidator()))
			.on(o)
			.doValidate()
			.result(ResultCollectors.toComplex());
		raiseWhenError(result);
	}

	public static  void raiseWhenError(ComplexResult result, String message) {
		if (!result.isSuccess() && result.getErrorNumber() > 0) {
			List<ValidationError> errors = result.getErrors();
			if(StringUtils.isBlank(message)) {
				StringBuilder sb = new StringBuilder();
				for (ValidationError validationError : errors) {
					String field = validationError.getField();
					String errorMsg = validationError.getErrorMsg();
					if (field != null) {
						sb.append("字段 [ ").append(field).append(" ] 有误.");
					}
					sb.append(errorMsg).append(";\n");
				}
				message = sb.substring(0, sb.length() - 2);
			}
			throw new ApiException(ReplyCode.RequestParamError, message);
		}
	}

	public static void raiseWhenError(ComplexResult result) {
		raiseWhenError(result, null);
	}

	public static Registry getRegistry() {
		Registry registry;
		if (ApplicationContextHolder.hasApplicationContext()) {
			try {
				registry = ApplicationContextHolder.getBean(Registry.class);
			} catch (BeansException e) {
				registry = new SpringApplicationContextRegistry();
				((SpringApplicationContextRegistry) registry).setApplicationContext(ApplicationContextHolder.getApplicationContext());
			}
		} else {
			registry = new SimpleRegistry();
		}
		return registry;
	}
}

