package io.microvibe.booster.system.mapper;

import io.microvibe.booster.core.base.mybatis.mapping.BaseMapper;
import io.microvibe.booster.core.base.mybatis.annotation.AutoStatement;
import io.microvibe.booster.core.base.mybatis.lang.VelocityLangDriver;
import io.microvibe.booster.core.base.mybatis.statement.BuilderType;
import io.microvibe.booster.system.entity.SysUser;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser, Long> {

	@Lang(VelocityLangDriver.class)
	@Select({"{{",
		"select $sql.columnsAlias('SysUser','t') ",
		"from $sql.table('SysUser','t') ",
		"where $sql.column('SysUser','username','t') = #{username,jdbcType=VARCHAR}",
		"}}"})
	@ResultType(SysUser.class)
	SysUser getByUserName(@Param("username") String userName);

	@Lang(VelocityLangDriver.class)
	@Select({"{{",
		"select count(1) ",
		"from $sql.table('SysUser','t') ",
		"where $sql.column('SysUser','username','t') = #{username,jdbcType=VARCHAR}",
		"}}"})
	long countByUserName(@Param("username") String userName);

	/*

	@Lang(VelocityLangDriver.class)
	@Select({"{{",
		"select $sql.columnsAlias('SysUser','t') ",
		"from $sql.table('SysUser','t') ",
		"where $sql.column('SysUser','mobilePhone','t') = #{mobilePhone,jdbcType=VARCHAR}",
		"}}"})
	@ResultType(SysUser.class)
	SysUser getByMobilePhone(@Param("mobilePhone") String mobilePhone);

	@Lang(VelocityLangDriver.class)
	@Select({"{{",
		"select $sql.columnsAlias('SysUser','t') ",
		"from $sql.table('SysUser','t') ",
		"where $sql.column('SysUser','email','t') = #{email,jdbcType=VARCHAR}",
		"}}"})
	@ResultType(SysUser.class)
	SysUser getByEmail(@Param("email") String email);

	@Lang(VelocityLangDriver.class)
	@Select({"{{",
		"select count(1) ",
		"from $sql.table('SysUser','t') ",
		"where $sql.column('SysUser','mobilePhone','t') = #{mobilePhone,jdbcType=VARCHAR}",
		"}}"})
	long countByMobilePhone(@Param("mobilePhone") String mobilePhone);

	@Lang(VelocityLangDriver.class)
	@Select({"{{",
		"select count(1) ",
		"from $sql.table('SysUser','t') ",
		"where $sql.column('SysUser','email','t') = #{email,jdbcType=VARCHAR}",
		"}}"})
	long countByEmail(@Param("email") String email);
	*/


	@Lang(VelocityLangDriver.class)
	@Select({"select {{$sql.columns('SysUser','t')}} ",
		"from {{ $sql.table('SysUser','t')}} "})
	@ResultType(SysUser.class)
	List<SysUser> getAll();


	@AutoStatement(BuilderType.SELECT_BY_ENTITY)
	@ResultType(SysUser.class)
	List<SysUser> selectAll();

}
