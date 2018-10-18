package io.microvibe.booster.core.api.dbo.service;

import io.microvibe.booster.core.api.dbo.entity.ApiInfo;
import io.microvibe.booster.core.api.dbo.repository.ApiInfoRepository;
import io.microvibe.booster.core.base.annotation.BaseComponent;
import io.microvibe.booster.core.base.service.JpaBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApiInfoServiceJpa extends JpaBaseService<ApiInfo, Long> {
	@Autowired
	@BaseComponent
	ApiInfoRepository apiInfoRepository;

	public ApiInfo findByApiCode(String apiCode) {
		return apiInfoRepository.findByApiCode(apiCode);
	}

}
