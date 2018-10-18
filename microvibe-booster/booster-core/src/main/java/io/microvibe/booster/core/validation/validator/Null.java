package io.microvibe.booster.core.validation.validator;

import com.baidu.unbiz.fluentvalidator.Validator;
import io.microvibe.booster.commons.err.ValidationException;
import org.springframework.stereotype.Component;

/**
 * @author Qt
 * @since Jul 02, 2018
 */
@Component(Null.BEAN_ID)
public class Null extends AbstractValidator<Object> implements Validator<Object> {

	public static final String BEAN_ID = "NullValidator";

	@Override
	public void doValidate(Object obj) {
		if (obj != null) {
			throw new ValidationException("validation.core.Null");
		}
	}
}
