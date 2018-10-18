package io.microvibe.booster.core.validation.validator;

import com.baidu.unbiz.fluentvalidator.Validator;
import io.microvibe.booster.commons.err.ValidationException;
import org.springframework.stereotype.Component;

/**
 * @author Qt
 * @since Jul 02, 2018
 */
@Component(Empty.BEAN_ID)
public class Empty extends AbstractValidator<Object> implements Validator<Object> {

	public static final String BEAN_ID = "EmptyValidator";

	@Override
	public void doValidate(Object obj) {
		if (obj instanceof CharSequence) {
			if (obj == null || obj.toString().length() > 0) {
				throw new ValidationException("validation.core.Empty");
			}
		} else {
			if (obj != null) {
				throw new ValidationException("validation.core.Empty");
			}
		}
	}
}
