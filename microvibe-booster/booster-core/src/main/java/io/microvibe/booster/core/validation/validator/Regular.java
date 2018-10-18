package io.microvibe.booster.core.validation.validator;

import com.baidu.unbiz.fluentvalidator.Validator;
import io.microvibe.booster.commons.err.ValidationException;

import java.util.regex.Pattern;

/**
 * @author Qt
 * @since Jul 02, 2018
 */
public class Regular extends AbstractValidator<Object> implements Validator<Object> {

	private String regex;

	public Regular(String regex) {
		this.regex = regex;
	}

	@Override
	public void doValidate(Object obj) {
		if (obj == null) {
			return;
		}
		Pattern pattern = Pattern.compile(this.regex);
		boolean valid = pattern.matcher(obj.toString()).find();
		if (!valid) {
			throw new ValidationException("validation.core.Regular", regex);
		}
	}

}
