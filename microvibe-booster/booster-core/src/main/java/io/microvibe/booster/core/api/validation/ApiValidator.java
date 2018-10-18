package io.microvibe.booster.core.api.validation;

import java.util.regex.Pattern;

public interface ApiValidator {

	/**
	 * 添加验证规则
	 *
	 * @param feature      规则类型
	 * @param message      验证错误消息
	 * @param pathProperty 属性路径表达式
	 * @param args         规则参数
	 * @return
	 */
	ApiValidator require(RuleFeature feature, String message, String pathProperty, Object... args);

	ApiValidator require(RuleFeature feature, String message, String pathProperty);

	ApiValidator isPattern(String message, String pathProperty, Pattern pattern);

	ApiValidator isPattern(String message, String pathProperty, String pattern);

	ApiValidator isNull(String message, String pathProperty);

	ApiValidator isEmpty(String message, String pathProperty);

	ApiValidator isBlank(String message, String pathProperty);

	ApiValidator isTrue(String message, String pathProperty);

	ApiValidator notTrue(String message, String pathProperty);

	ApiValidator notNull(String message, String pathProperty);

	ApiValidator notEmpty(String message, String pathProperty);

	ApiValidator notBlank(String message, String pathProperty);

}
