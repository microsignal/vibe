package io.microvibe.booster.core.base.mybatis.mapping.auto;

import io.microvibe.booster.core.base.mybatis.annotation.AutoStatement;
import io.microvibe.booster.core.base.mybatis.annotation.SuperMapper;
import io.microvibe.booster.core.base.mybatis.mapping.AutoEntityMapper;
import io.microvibe.booster.core.base.mybatis.statement.BuilderType;
import org.apache.ibatis.session.RowBounds;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.util.List;

@SuperMapper
public interface SelectByEntityMapper<T extends Persistable<ID>, ID extends Serializable>
	extends AutoEntityMapper<T, ID> {


	// region Select Method


	@AutoStatement(BuilderType.EXISTS_BY_ENTITY)
	public boolean existsByEntity(T entity);

	@AutoStatement(BuilderType.SELECT_BY_ENTITY)
	public T getByEntity(T entity);

	@AutoStatement(BuilderType.SELECT_BY_ENTITY)
	public List<T> selectByEntity(T entity);

	@AutoStatement(BuilderType.SELECT_BY_ENTITY)
	public List<T> selectByEntity(T entity, RowBounds rowBounds);

	@AutoStatement(BuilderType.COUNT_BY_ENTITY)
	public long countByEntity(T entity);

	// endregion


}
