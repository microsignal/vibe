package io.microvibe.booster.core.app.service;

import io.microvibe.booster.core.app.entity.AppAuthz;
import io.microvibe.booster.core.app.repository.AppAuthzRepository;
import io.microvibe.booster.core.base.annotation.BaseComponent;
import io.microvibe.booster.core.base.service.JpaBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppAuthzServiceJpa extends JpaBaseService<AppAuthz, Long> {
	@Autowired(required = false)
	@BaseComponent
	AppAuthzRepository appAuthzRepository;

	public List<AppAuthz> findByAppInfoId(Long appInfoId) {
		return appAuthzRepository.findByAppInfoId(appInfoId);
	}

	public List<AppAuthz> findByTxnCode(String txnCode) {
		return appAuthzRepository.findByTxnCode(txnCode);
	}

	public List<AppAuthz> findByAppInfoIdAndTxnCode(Long appInfoId, String txnCode) {
		return appAuthzRepository.findByAppInfoIdAndTxnCode(appInfoId, txnCode);
	}
}
