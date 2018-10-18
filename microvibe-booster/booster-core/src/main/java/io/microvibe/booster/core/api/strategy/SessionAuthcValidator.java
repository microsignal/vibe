package io.microvibe.booster.core.api.strategy;

import org.springframework.stereotype.Component;

@Component
public interface SessionAuthcValidator {

	/**
	 * 校验当前WEB会话是否认证通过
	 *
	 * @return true: 校验通过; false: 校验失败
	 */
	public boolean validate();
}
