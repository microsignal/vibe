package io.microvibe.booster.core.validation.validator;

import com.baidu.unbiz.fluentvalidator.Validator;
import io.microvibe.booster.commons.err.ValidationException;

/**
 * @author Qt
 * @since Jul 02, 2018
 */
public class MaxLength extends AbstractValidator<Object> implements Validator<Object> {

	private int length = 0;

	public MaxLength(int length) {
		this.length = length;
	}

	@Override
	public void doValidate(Object obj) {
		if (obj == null) return;
		if (obj instanceof CharSequence) {
			if (((CharSequence) obj).length() > length) {
				throw new ValidationException("validation.core.MaxLength", length);
			}
		}
	}

}
