package io.microvibe.booster.core.app.repository;

import io.microvibe.booster.core.app.entity.AppAuthz;
import io.microvibe.booster.core.base.repository.BaseRepository;

import java.util.List;

public interface AppAuthzRepository extends BaseRepository<AppAuthz, Long> {

	List<AppAuthz> findByAppInfoId(Long appInfoId);

	List<AppAuthz> findByTxnCode(String txnCode);

	List<AppAuthz> findByAppInfoIdAndTxnCode(Long appInfoId, String txnCode);
}
