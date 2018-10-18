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
public interface DeleteByIdMapper<T extends Persistable<ID>, ID extends Serializable>
	extends AutoEntityMapper<T, ID> {


	// region Delete Method

	@AutoStatement(BuilderType.DELETE_BY_ID)
	public long deleteById(@Param(MybatisConstants.PARAM_ID) ID id);

	@AutoStatement(BuilderType.DELETE_BY_ID)
	public long deleteById(@Param(MybatisConstants.PARAM_ID) ID id, @Param(MybatisConstants.PARAM_VERSION) long version);

	@AutoStatement(BuilderType.DELETE_BY_ID)
	public long deleteByEntity(T entity);

	@AutoStatement(BuilderType.DELETE_BY_ID)
	public long deleteByMap(Map<String, Object> entity);

	// endregion

}
