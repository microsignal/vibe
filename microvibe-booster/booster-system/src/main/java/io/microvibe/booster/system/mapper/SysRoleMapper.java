package io.microvibe.booster.system.mapper;

import io.microvibe.booster.core.base.mybatis.mapping.BaseMapper;
import io.microvibe.booster.core.base.mybatis.annotation.AfterEntityScanner;
import io.microvibe.booster.core.base.mybatis.lang.VelocityLangDriver;
import io.microvibe.booster.system.entity.SysRole;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
@AfterEntityScanner
public interface SysRoleMapper extends BaseMapper<SysRole, Long> {

	@Lang(VelocityLangDriver.class)
	@Select({"{{",
		"select $sql.columnsAlias('SysRole', 'r') ",
		"from $sql.table('SysRole', 'r') ",
		", $sql.table('SysUserRole', 'ur') ",
		"where $sql.column('SysRole','id','r') = $sql.column('SysUserRole','roleId','ur') ",
		"and $sql.column('SysUserRole','userId','ur') = #{userId}",
		"}} "})
	@ResultType(SysRole.class)
	List<SysRole> getRolesByUserId(@Param("userId") Long userId);

}
