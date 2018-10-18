package io.microvibe.booster.core.base.mybatis.mapping;

import io.microvibe.booster.core.base.mybatis.MybatisConstants;
import io.microvibe.booster.core.base.mybatis.annotation.AfterEntityScanner;
import io.microvibe.booster.core.base.mybatis.annotation.SuperMapper;
import io.microvibe.booster.core.base.mybatis.builder.MapperBuilder;
import org.apache.ibatis.annotations.*;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;

@AfterEntityScanner
@SuperMapper
public interface BaseProvidedMapper<T extends Persistable<ID>, ID extends Serializable> extends EntityMapper<T, ID> {

	@SelectProvider(type = MapperBuilder.class, method = "hasOne")
	public boolean hasOneById(@Param(MybatisConstants.PARAM_ID) ID id);

	@SelectProvider(type = MapperBuilder.class, method = "hasOne")
	public boolean hasOneByEntity(T entity);

	@SelectProvider(type = MapperBuilder.class, method = "findOne")
	public T findOneById(@Param(MybatisConstants.PARAM_ID) ID id);

	@DeleteProvider(type = MapperBuilder.class, method = "doRemove")
	public long doRemoveById(@Param(MybatisConstants.PARAM_ID) ID id);

	@InsertProvider(type = MapperBuilder.class, method = "doAdd")
	public long doAddByEntity(T entity);

	@UpdateProvider(type = MapperBuilder.class, method = "doModify")
	public long doModifyByEntity(T entity);

}
