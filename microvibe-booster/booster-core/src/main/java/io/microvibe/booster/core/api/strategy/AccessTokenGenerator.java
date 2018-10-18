package io.microvibe.booster.core.api.strategy;

public interface AccessTokenGenerator {

	/**
	 * 生成AccessToken值
	 *
	 * @param appId     第三方应用ID
	 * @param appSecret 第三方应用密钥
	 * @return
	 */
	public String generate(String appId, String appSecret);
}
