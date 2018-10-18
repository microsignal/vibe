package io.microvibe.booster.core.base.mybatis.mapping.auto;

import io.microvibe.booster.core.base.mybatis.annotation.AutoStatement;
import io.microvibe.booster.core.base.mybatis.annotation.SuperMapper;
import io.microvibe.booster.core.base.mybatis.mapping.AutoEntityMapper;
import io.microvibe.booster.core.base.mybatis.statement.BuilderType;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.util.Map;

@SuperMapper
public interface UpdateByIdMapper<T extends Persistable<ID>, ID extends Serializable>
	extends AutoEntityMapper<T, ID> {



	// region Update Method

	@AutoStatement(BuilderType.UPDATE_BY_ID)
	public long updateByEntity(T entity);

	@AutoStatement(BuilderType.UPDATE_BY_ID)
	public long updateByMap(Map<String, Object> entity);

	@AutoStatement(BuilderType.UPDATE_SELECTIVE_BY_ID)
	public long updateSelectiveByEntity(T entity);

	@AutoStatement(BuilderType.UPDATE_SELECTIVE_BY_ID)
	public long updateSelectiveByMap(Map<String, Object> entity);

	// endregion


}
