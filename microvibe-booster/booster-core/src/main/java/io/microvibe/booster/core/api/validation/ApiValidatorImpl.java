package io.microvibe.booster.core.api.validation;

import io.microvibe.booster.core.api.tools.Assertion;

import java.util.regex.Pattern;

public class ApiValidatorImpl implements ApiValidator {

	private Object bean;

	public ApiValidatorImpl(Object bean) {
		this.bean = bean;
	}

	@Override
	public ApiValidator require(RuleFeature feature, String message, String pathProperty, Object... args) {
		switch (feature) {
			case notBlank:
				return notBlank(message, pathProperty);
			case notEmpty:
				return notEmpty(message, pathProperty);
			case notNull:
				return notNull(message, pathProperty);
			case notTrue:
				return notTrue(message, pathProperty);
			case isBlank:
				return isBlank(message, pathProperty);
			case isEmpty:
				return isEmpty(message, pathProperty);
			case isNull:
				return isNull(message, pathProperty);
			case isTrue:
				return isTrue(message, pathProperty);
			case isPattern:
				if (args[0] instanceof Pattern) {
					isPattern(message, pathProperty, (Pattern) args[0]);
				} else if (args[0] instanceof String) {
					isPattern(message, pathProperty, (String) args[0]);
				}
			default:
				break;
		}
		return this;
	}

	@Override
	public ApiValidator require(RuleFeature feature, String message, String pathProperty) {
		return require(feature, message, pathProperty, new Object[]{});
	}

	@Override
	public ApiValidator notBlank(String message, String pathProperty) {
		Assertion.isNotBlank(message, this.bean, pathProperty);
		return this;
	}

	@Override
	public ApiValidator notEmpty(String message, String pathProperty) {
		Assertion.isNotEmpty(message, this.bean, pathProperty);
		return this;
	}

	@Override
	public ApiValidator notNull(String message, String pathProperty) {
		Assertion.isNotNull(message, this.bean, pathProperty);
		return this;
	}

	@Override
	public ApiValidator notTrue(String message, String pathProperty) {
		Assertion.isNotTrue(message, this.bean, pathProperty);
		return this;
	}

	@Override
	public ApiValidator isTrue(String message, String pathProperty) {
		Assertion.isTrue(message, this.bean, pathProperty);
		return this;
	}

	@Override
	public ApiValidator isBlank(String message, String pathProperty) {
		Assertion.isBlank(message, this.bean, pathProperty);
		return this;
	}

	@Override
	public ApiValidator isEmpty(String message, String pathProperty) {
		Assertion.isEmpty(message, this.bean, pathProperty);
		return this;
	}

	@Override
	public ApiValidator isNull(String message, String pathProperty) {
		Assertion.isNull(message, this.bean, pathProperty);
		return this;
	}

	@Override
	public ApiValidator isPattern(String message, String pathProperty, String pattern) {
		Assertion.isPattern(message, pattern, this.bean, pathProperty);
		return this;
	}

	@Override
	public ApiValidator isPattern(String message, String pathProperty, Pattern pattern) {
		Assertion.isPattern(message, pattern, this.bean, pathProperty);
		return this;
	}

}
