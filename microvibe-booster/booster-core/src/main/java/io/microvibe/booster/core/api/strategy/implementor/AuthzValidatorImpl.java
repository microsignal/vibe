package io.microvibe.booster.core.api.strategy.implementor;

import com.alibaba.fastjson.JSONObject;
import io.microvibe.booster.core.accessor.CacheAccessor;
import io.microvibe.booster.core.api.ApiConstants;
import io.microvibe.booster.core.api.ApiException;
import io.microvibe.booster.core.api.ReplyCode;
import io.microvibe.booster.core.api.annotation.ApiAuthz;
import io.microvibe.booster.core.api.strategy.AuthzValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.stereotype.Component;

@Component
public class AuthzValidatorImpl implements AuthzValidator {

	@Override
	@SuppressWarnings("unused")
	public void validate(String accessToken, String authzPermission) {
		accessToken = StringUtils.trimToNull(accessToken);
		if (accessToken == null) {
			throw new ApiException(ReplyCode.RequestTokenEmpty);
		}

		ValueWrapper valueWrapper = CacheAccessor.getApiAccessTokenCache().get(accessToken);// tokenBinding
		if (valueWrapper == null) {
			// 已过期或不存在
			throw new ApiException(ReplyCode.RequestTokenError);
		}

		if (authzPermission != null && !authzPermission.equals(ApiAuthz.ANY)) {
			JSONObject tokenBinding = JSONObject.parseObject(valueWrapper.get().toString());
			String appId = tokenBinding.getString(ApiConstants.APP_ID);
			String appSecret = tokenBinding.getString(ApiConstants.APP_SECRET);

			//FXIME 待添加appid申请的权限验证规则

		}


	}
}
