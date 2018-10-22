package io.microvibe.booster.system.mapper;

import io.microvibe.booster.core.base.mybatis.mapping.BaseMapper;
import io.microvibe.booster.core.base.mybatis.annotation.AfterEntityScanner;
import io.microvibe.booster.core.base.mybatis.annotation.AutoMapper;
import io.microvibe.booster.core.base.mybatis.annotation.AutoStatement;
import io.microvibe.booster.core.base.mybatis.annotation.JoinOn;
import io.microvibe.booster.core.base.mybatis.statement.BuilderType;
import io.microvibe.booster.system.entity.A;
import io.microvibe.booster.system.entity.B;
import io.microvibe.booster.system.entity.C;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

import java.util.List;

/**
 * @author Qt
 * @since Aug 25, 2018
 */
@Mapper
@AfterEntityScanner
@AutoMapper
public interface EntityATestMapper extends BaseMapper<A, Long> {

	@Override
	@AutoStatement(BuilderType.SELECT_BY_ENTITY)
	@JoinOn(table = C.class,columnPrefix = "auth",tableAlias = "auth",property = "authList",
		on = "auth.user_id = user_id")
	List<A> selectByEntity(A entity);


	@AutoMapper(B.class)
	@AutoStatement(BuilderType.INSERT)
//	@Options(useGeneratedKeys = true)
	long insertB(B b);
}
