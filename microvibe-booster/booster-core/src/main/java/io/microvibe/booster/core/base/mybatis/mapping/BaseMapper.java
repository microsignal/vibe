package io.microvibe.booster.core.base.mybatis.mapping;

import io.microvibe.booster.core.base.mybatis.annotation.AfterEntityScanner;
import io.microvibe.booster.core.base.mybatis.annotation.SuperMapper;
import io.microvibe.booster.core.base.mybatis.mapping.auto.AutoStatementMapper;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;

@SuperMapper
@AfterEntityScanner
public interface BaseMapper<T extends Persistable<ID>, ID extends Serializable>
	extends BaseProvidedMapper<T, ID>, AutoStatementMapper<T, ID>, EntityMapper<T, ID> {

}
