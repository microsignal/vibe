package io.microvibe.booster.core.api.strategy;

import io.microvibe.booster.core.api.ApiException;
import org.springframework.stereotype.Component;

@Component
public interface AuthzValidator {

	/**
	 * 校验AccessToken值及权限
	 *
	 * @param accessToken
	 * @param authzPermission
	 * @throws ApiException
	 */
	public void validate(String accessToken, String authzPermission) throws ApiException;
}
