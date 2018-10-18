package io.microvibe.booster.core.base.mybatis.mapping.auto;

import io.microvibe.booster.core.base.mybatis.annotation.AutoStatement;
import io.microvibe.booster.core.base.mybatis.annotation.SuperMapper;
import io.microvibe.booster.core.base.mybatis.mapping.AutoEntityMapper;
import io.microvibe.booster.core.base.mybatis.statement.BuilderType;
import org.apache.ibatis.session.RowBounds;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@SuperMapper
public interface SelectByMapMapper<T extends Persistable<ID>, ID extends Serializable>
	extends AutoEntityMapper<T, ID> {


	// region Select Method


	@AutoStatement(BuilderType.EXISTS_BY_MAP)
	public boolean existsByMap(Map<String, Object> entity);

	@AutoStatement(BuilderType.SELECT_BY_MAP)
	public T getByMap(Map<String, Object> entity);

	@AutoStatement(BuilderType.SELECT_BY_MAP)
	public List<T> selectByMap(Map<String, Object> entity);

	@AutoStatement(BuilderType.SELECT_BY_MAP)
	public List<T> selectByMap(Map<String, Object> entity, RowBounds rowBounds);

	@AutoStatement(BuilderType.COUNT_BY_MAP)
	public long countByMap(Map<String, Object> entity);

	// endregion


}
