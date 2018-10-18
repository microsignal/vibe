package io.microvibe.booster.core.base.mybatis.mapping.auto;

import io.microvibe.booster.core.base.mybatis.annotation.AutoStatement;
import io.microvibe.booster.core.base.mybatis.annotation.SuperMapper;
import io.microvibe.booster.core.base.mybatis.mapping.AutoEntityMapper;
import io.microvibe.booster.core.base.mybatis.statement.BuilderType;
import io.microvibe.booster.core.base.mybatis.example.Example;
import org.apache.ibatis.session.RowBounds;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.util.List;

@SuperMapper
public interface SelectByExampleMapper<T extends Persistable<ID>, ID extends Serializable>
	extends AutoEntityMapper<T, ID> {
	/*
	@Mapping(searchKey="xxx",column="t.xxx")
	@Alias

	 */

	// region Select Method

	@AutoStatement(BuilderType.EXISTS_BY_EXAMPLE)
	public boolean existsByExample(Example<T> example);

	@AutoStatement(BuilderType.SELECT_BY_EXAMPLE)
	public T getByExample(Example<T> example);

	@AutoStatement(BuilderType.SELECT_BY_EXAMPLE)
	public List<T> selectByExample(Example<T> example);

	@AutoStatement(BuilderType.SELECT_BY_EXAMPLE)
	public List<T> selectByExample(Example<T> example, RowBounds rowBounds);

	@AutoStatement(BuilderType.COUNT_BY_EXAMPLE)
	public long countByExample(Example<T> example);

	// endregion


}
