package io.microvibe.booster.core.app.repository;

import io.microvibe.booster.core.app.entity.AppInfo;
import io.microvibe.booster.core.base.repository.BaseRepository;

public interface AppInfoRepository extends BaseRepository<AppInfo, Long> {

	AppInfo findByAppId(String appId);

	AppInfo findByAppName(String appName);

}
