package io.microvibe.booster.core.base.mybatis.mapping.auto;

import io.microvibe.booster.core.base.mybatis.annotation.SuperMapper;
import io.microvibe.booster.core.base.mybatis.mapping.AutoEntityMapper;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;

@SuperMapper
public interface AutoStatementMapper<T extends Persistable<ID>, ID extends Serializable>
	extends AutoEntityMapper<T, ID>,
	SelectByExampleMapper<T, ID>,
	SelectByEntityMapper<T, ID>,
	SelectByMapMapper<T, ID>,
	SelectByIdMapper<T, ID>,
	DeleteByIdMapper<T, ID>,
	InsertMapper<T, ID>,
	UpdateByIdMapper<T, ID> {

}
