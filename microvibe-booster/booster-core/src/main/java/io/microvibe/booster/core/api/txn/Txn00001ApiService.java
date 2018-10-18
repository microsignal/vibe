package io.microvibe.booster.core.api.txn;

import io.microvibe.booster.core.api.ApiConstants;
import io.microvibe.booster.core.api.ApiException;
import io.microvibe.booster.core.api.ApiService;
import io.microvibe.booster.core.api.annotation.ApiName;
import io.microvibe.booster.core.api.model.RequestData;
import io.microvibe.booster.core.api.model.ResponseData;
import io.microvibe.booster.core.app.entity.AppInfo;
import io.microvibe.booster.core.app.service.AppInfoServiceJpa;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 生成第三方应用的appId与appSecret
 *
 * @author Qt
 * @since Oct 11, 2017
 */
@Component
@ApiName({"getAppKey"})
public class Txn00001ApiService extends BaseApiService implements ApiService {

	@Autowired
	AppInfoServiceJpa appInfoService;

	public Txn00001ApiService() {
	}

	@Override
	public ResponseData execute(RequestData request) throws ApiException {

		String appName = StringUtils.trimToNull(request.getBodyAsString(ApiConstants.APP_ID));
		String appId = StringUtils.trimToNull(request.getBodyAsString(ApiConstants.APP_NAME));

		AppInfo appInfo;
		if (appId != null) {// 查询
			appInfo = appInfoService.findByAppId(appId);
		} else if (appName != null) {
			appInfo = appInfoService.createAppId(appName);
		} else {
			throw new ApiException("请提供应用名称或ID!");
		}

		ResponseData response = request.buildResponse();
		response.setBody(ApiConstants.APP_ID, appInfo.getAppId());
		response.setBody(ApiConstants.APP_SECRET, appInfo.getAppSecret());
		return response;
	}

}
