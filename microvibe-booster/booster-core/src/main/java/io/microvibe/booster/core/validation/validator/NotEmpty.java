package io.microvibe.booster.core.validation.validator;

import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;
import io.microvibe.booster.commons.err.ValidationException;
import org.springframework.stereotype.Component;

/**
 * @author Qt
 * @since Jul 02, 2018
 */
@Component(NotEmpty.BEAN_ID)
public class NotEmpty extends AbstractValidator<Object> implements Validator<Object> {

	public static final String BEAN_ID = "NotEmptyValidator";

	@Override
	public void doValidate(Object obj) {
		if (obj == null) {
			throw new ValidationException("validation.core.NotEmpty");
		}
		if (obj instanceof CharSequence) {
			if (obj.toString().trim().length() == 0) {
				throw new ValidationException("validation.core.NotEmpty");
			}
		}
	}
}
