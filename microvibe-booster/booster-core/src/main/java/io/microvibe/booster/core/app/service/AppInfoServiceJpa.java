package io.microvibe.booster.core.app.service;

import io.microvibe.booster.core.api.ApiException;
import io.microvibe.booster.core.api.tools.AppKeyToolkit;
import io.microvibe.booster.core.app.entity.AppInfo;
import io.microvibe.booster.core.app.repository.AppInfoRepository;
import io.microvibe.booster.core.base.annotation.BaseComponent;
import io.microvibe.booster.core.base.service.JpaBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppInfoServiceJpa extends JpaBaseService<AppInfo, Long> {

	@Autowired(required = false)
	@BaseComponent
	private AppInfoRepository appInfoRepository;

	public AppInfo findByAppId(String appId) {
		return appInfoRepository.findByAppId(appId);
	}

	public AppInfo createAppId(String appName) {
		AppInfo appInfo = appInfoRepository.findByAppName(appName);
		if (appInfo != null) {
			throw new ApiException("应用名已存在!");
		}

		String appId = AppKeyToolkit.genAppId();
		String appSecret = AppKeyToolkit.genAppSecret(appId);

		appInfo = new AppInfo();
		appInfo.setAppId(appId);
		appInfo.setAppSecret(appSecret);

		appInfo = save(appInfo);

		return appInfo;
	}

}
