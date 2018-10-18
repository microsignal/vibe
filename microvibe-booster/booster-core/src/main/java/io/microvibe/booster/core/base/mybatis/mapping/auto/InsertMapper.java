package io.microvibe.booster.core.base.mybatis.mapping.auto;

import io.microvibe.booster.core.base.mybatis.annotation.AutoStatement;
import io.microvibe.booster.core.base.mybatis.annotation.SuperMapper;
import io.microvibe.booster.core.base.mybatis.mapping.AutoEntityMapper;
import io.microvibe.booster.core.base.mybatis.statement.BuilderType;
import org.apache.ibatis.annotations.Options;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@SuperMapper
public interface InsertMapper<T extends Persistable<ID>, ID extends Serializable>
	extends AutoEntityMapper<T, ID> {


	// region Insert Method

	@AutoStatement(BuilderType.INSERT)
	public long insertByEntity(T entity);

	@AutoStatement(BuilderType.INSERT)
	public long insertByMap(Map<String, Object> entity);

	@AutoStatement(BuilderType.INSERT_SELECTIVE)
	public long insertSelectiveByEntity(T entity);

	@AutoStatement(BuilderType.INSERT_SELECTIVE)
	public long insertSelectiveByMap(Map<String, Object> entity);

	@AutoStatement(BuilderType.INSERT_BATCH)
	public long insertBatchByEntity(List<T> entities);

	@AutoStatement(BuilderType.INSERT_BATCH)
	public long insertBatchByMap(List<Map<String, Object>> entities);

	// endregion


	// region Insert-AutoIncrementKey Method

	@AutoStatement(BuilderType.INSERT)
	@Options(useGeneratedKeys = true)
	public long insertByEntityOfAutoKey(T entity);

	@AutoStatement(BuilderType.INSERT)
	@Options(useGeneratedKeys = true)
	public long insertByMapOfAutoKey(Map<String, Object> entity);

	@AutoStatement(BuilderType.INSERT_SELECTIVE)
	@Options(useGeneratedKeys = true)
	public long insertSelectiveByEntityOfAutoKey(T entity);

	@AutoStatement(BuilderType.INSERT_SELECTIVE)
	@Options(useGeneratedKeys = true)
	public long insertSelectiveByMapOfAutoKey(Map<String, Object> entity);

	@AutoStatement(BuilderType.INSERT_BATCH)
	@Options(useGeneratedKeys = true)
	public long insertBatchByEntityOfAutoKey(List<T> entities);

	@AutoStatement(BuilderType.INSERT_BATCH)
	@Options(useGeneratedKeys = true)
	public long insertBatchByMapOfAutoKey(List<Map<String, Object>> entities);

	// endregion

}
