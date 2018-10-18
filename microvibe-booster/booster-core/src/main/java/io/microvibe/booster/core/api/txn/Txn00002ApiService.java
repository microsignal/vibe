package io.microvibe.booster.core.api.txn;

import io.microvibe.booster.core.api.ApiConstants;
import io.microvibe.booster.core.api.ApiException;
import io.microvibe.booster.core.api.ApiService;
import io.microvibe.booster.core.api.annotation.ApiName;
import io.microvibe.booster.core.api.model.RequestData;
import io.microvibe.booster.core.api.model.ResponseData;
import io.microvibe.booster.core.api.strategy.AccessTokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 通过第三方应用的appId与appSecret, 生成有时效性的accessToken
 *
 * @author Qt
 * @since Oct 11, 2017
 */
@Component
@ApiName({"getAccessToken"})
public class Txn00002ApiService extends BaseApiService implements ApiService {

	@Autowired
	AccessTokenGenerator accessTokenGenerator;

	public Txn00002ApiService() {
	}

	@Override
	public ResponseData execute(RequestData request) throws ApiException {
		String appId = request.getBody(ApiConstants.APP_ID, String.class);
		String appSecret = request.getBody(ApiConstants.APP_SECRET, String.class);

		String accessToken = accessTokenGenerator.generate(appId, appSecret);

		ResponseData response = request.buildResponse();
		response.setBody(ApiConstants.ACCESS_TOKEN, accessToken);
		return response;
	}

}
