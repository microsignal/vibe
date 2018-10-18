package io.microvibe.booster.system.mapper;

import io.microvibe.booster.core.base.mybatis.mapping.BaseMapper;
import io.microvibe.booster.core.base.mybatis.annotation.AfterEntityScanner;
import io.microvibe.booster.core.base.mybatis.lang.VelocityLangDriver;
import io.microvibe.booster.system.entity.SysRoleResource;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
@AfterEntityScanner
public interface SysRoleResourceMapper extends BaseMapper<SysRoleResource, Long> {
	/**
	 * 根据用户获取所有资源权限
	 *
	 * @param userId
	 * @return
	 */
	@Select({"{{",
		"select $sql.columnsAlias('SysRoleResource', 'rr')",
		", $sql.columnsAlias('SysResource', 'res', 'res') ",
		"from $sql.table('SysRoleResource', 'rr') ",
		", $sql.table('SysResource', 'res') ",
		", $sql.table('SysUserRole', 'ur') ",
		"where $sql.column('SysRoleResource','resourceId','rr') = $sql.column('SysResource','id','res') ",
		"and $sql.column('SysRoleResource','roleId','rr') = $sql.column('SysUserRole','roleId','ur') ",
		"and $sql.column('SysUserRole','userId','ur') = #{userId}",
		"}} "})
	@Lang(VelocityLangDriver.class)
	@ResultMap("SysRoleResourceResultMap")
	List<SysRoleResource> getUserRoleResources(@Param("userId") String userId);

}
