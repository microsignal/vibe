package io.microvibe.booster.core.validation.validator;

import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;
import io.microvibe.booster.commons.err.MessageException;
import io.microvibe.booster.commons.err.ValidationException;
import io.microvibe.booster.commons.spring.MessageResources;
import io.microvibe.booster.core.validation.Validations;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * @author Qt
 * @since Jul 02, 2018
 */
public abstract class AbstractValidator<T> extends ValidatorHandler<T> implements Validator<T> {

	@Override
	public final boolean validate(ValidatorContext context, T obj) {
		boolean rs = false;
		try {
			doValidate(obj);
			rs = true;
		} catch (ValidationException e) {
			context.addErrorMsg(e.getMessage());
		} catch (MessageException e) {
			context.addErrorMsg(e.getMessage());
		} catch (Exception e) {
			context.addErrorMsg(MessageResources.getMessage(e.getMessage()));
		}
		return rs;
	}

	protected abstract void doValidate(T obj) throws ValidationException;


}
