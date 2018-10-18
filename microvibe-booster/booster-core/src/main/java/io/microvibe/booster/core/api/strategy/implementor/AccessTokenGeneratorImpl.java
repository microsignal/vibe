package io.microvibe.booster.core.api.strategy.implementor;

import com.alibaba.fastjson.JSONObject;
import io.microvibe.booster.commons.crypto.EncryptUtil;
import io.microvibe.booster.commons.crypto.MessageDigestUtil;
import io.microvibe.booster.commons.string.Hex;
import io.microvibe.booster.core.accessor.CacheAccessor;
import io.microvibe.booster.core.api.ApiConstants;
import io.microvibe.booster.core.api.ApiException;
import io.microvibe.booster.core.api.ReplyCode;
import io.microvibe.booster.core.api.strategy.AccessTokenGenerator;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Component
public class AccessTokenGeneratorImpl implements AccessTokenGenerator {

	/**
	 * 生成AccessToken值
	 *
	 * @param appId     第三方应用ID
	 * @param appSecret 第三方应用密钥
	 * @return
	 */
	public String generate(String appId, String appSecret) {
		appId = StringUtils.trimToNull(appId);
		appSecret = StringUtils.trimToNull(appSecret);

		// 验证有效性
		ReplyCode.RequestAppIdEmpty.assertNotNull("", appId);
		ReplyCode.RequestAppSecretEmpty.assertNotNull("", appSecret);

		try {
			String time = Base64.encodeBase64String(EncryptUtil.encryptAES(appSecret.getBytes(),
				Hex.toHex(System.currentTimeMillis()).getBytes()));
			String sign = Base64.encodeBase64String(EncryptUtil.encryptAES(appSecret.getBytes(),
				MessageDigestUtil.sha1(appId.getBytes())));
			String uuid = Base64.encodeBase64String(UUID.randomUUID().toString().getBytes());
			String accessToken = time + sign + uuid;

			JSONObject tokenBinding = new JSONObject(true);
			tokenBinding.put(ApiConstants.APP_ID, appId);
			tokenBinding.put(ApiConstants.APP_SECRET, appSecret);
			// bind shiro session
			Subject shiroSubject = SecurityUtils.getSubject();
			if (shiroSubject != null) {
				Serializable sessionId = shiroSubject.getSession().getId();
				tokenBinding.put(ApiConstants.SHIRO_SESSION_ID, sessionId);
			}

			CacheAccessor.getApiAccessTokenCache().put(accessToken, tokenBinding.toJSONString());

			ValueWrapper appInfo = CacheAccessor.getApiAppKeyCache().get(appId);
			JSONObject newAppInfoJson = new JSONObject();
			if (appInfo != null) {
				JSONObject appInfoJson = (JSONObject) appInfo.get();
				String cacheAccessToken = appInfoJson.getString(ApiConstants.ACCESS_TOKEN);
				// 移除旧token
				if (cacheAccessToken != null) {
					CacheAccessor.getApiAccessTokenCache().evict(cacheAccessToken);
					appInfoJson.remove(ApiConstants.ACCESS_TOKEN);
				}
				newAppInfoJson.putAll(appInfoJson);
			}
			newAppInfoJson.put(ApiConstants.ACCESS_TOKEN, accessToken);
			CacheAccessor.getApiAppKeyCache().put(appId, newAppInfoJson);
			return accessToken;
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
			| BadPaddingException e) {
			throw new ApiException(ReplyCode.RequestTokenGenError, e);
		}

	}
}
