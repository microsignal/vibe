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
@Component(NotNull.BEAN_ID)
public class NotNull extends AbstractValidator<Object> implements Validator<Object> {

	public static final String BEAN_ID = "NotNullValidator";

	@Override
	public void doValidate(Object obj) {
		if (obj == null) {
			throw new ValidationException("validation.core.NotNull");
		}
	}

}
