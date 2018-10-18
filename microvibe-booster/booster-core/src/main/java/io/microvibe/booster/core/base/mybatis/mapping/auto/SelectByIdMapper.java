package io.microvibe.booster.core.base.mybatis.mapping.auto;

import io.microvibe.booster.core.base.mybatis.MybatisConstants;
import io.microvibe.booster.core.base.mybatis.annotation.AutoStatement;
import io.microvibe.booster.core.base.mybatis.annotation.SuperMapper;
import io.microvibe.booster.core.base.mybatis.mapping.AutoEntityMapper;
import io.microvibe.booster.core.base.mybatis.statement.BuilderType;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.util.Map;

@SuperMapper
public interface SelectByIdMapper<T extends Persistable<ID>, ID extends Serializable>
	extends AutoEntityMapper<T, ID> {

	// region Select Method


	@AutoStatement(BuilderType.EXISTS_BY_ID)
	public boolean existsById(@Param(MybatisConstants.PARAM_ID) ID id);

	@AutoStatement(BuilderType.EXISTS_BY_ID)
	public boolean existsByIdOfEntity(T entity);

	@AutoStatement(BuilderType.EXISTS_BY_ID)
	public boolean existsByIdOfMap(Map<String, Object> entity);

	@AutoStatement(BuilderType.GET_BY_ID)
	public T getById(@Param(MybatisConstants.PARAM_ID) ID id);

	@AutoStatement(BuilderType.GET_BY_ID)
	public T getByIdOfEntity(T entity);

	@AutoStatement(BuilderType.GET_BY_ID)
	public T getByIdOfMap(Map<String, Object> entity);

	// endregion


}
