package io.microvibe.booster.core.validation.validator;

import com.baidu.unbiz.fluentvalidator.Validator;
import io.microvibe.booster.commons.err.ValidationException;

import java.util.regex.Pattern;

/**
 * @author Qt
 * @since Jul 02, 2018
 */
public class Length extends AbstractValidator<Object> implements Validator<Object> {

	private int length = 0;

	public Length(int length) {
		this.length = length;
	}

	@Override
	public void doValidate(Object obj) {
		if (obj == null) {
			return;
		}
		boolean valid = true;
		if (obj instanceof CharSequence) {
			if (((CharSequence) obj).length() != length) {
				valid = false;
			}
		}
		if (!valid) {
			throw new ValidationException("validation.core.Length", length);
		}
	}

}
