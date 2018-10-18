package io.microvibe.booster.core.api.dbo.repository;

import io.microvibe.booster.core.api.dbo.entity.ApiInfo;
import io.microvibe.booster.core.base.repository.BaseRepository;

public interface ApiInfoRepository extends BaseRepository<ApiInfo, Long> {

	ApiInfo findByApiCode(String apiCode);

}
