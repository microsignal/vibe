package io.microvibe.booster.system.mapper;

import io.microvibe.booster.core.base.mybatis.mapping.BaseMapper;
import io.microvibe.booster.core.base.mybatis.annotation.AfterEntityScanner;
import io.microvibe.booster.core.base.mybatis.lang.VelocityLangDriver;
import io.microvibe.booster.system.entity.SysResource;
import org.apache.ibatis.annotations.Lang;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
@AfterEntityScanner
public interface SysResourceMapper extends BaseMapper<SysResource, Long> {
	/**
	 * 根据用户获取所有资源权限
	 *
	 * @param userId
	 * @return
	 */
	@Select({"{{",
		"select $sql.columnsAlias('SysResource', 'res') ",
		"from $sql.table('SysResource', 'res') ",
		", $sql.table('SysRoleResource', 'rr') ",
		", $sql.table('SysUserRole', 'ur') ",
		"where $sql.column('SysRoleResource', 'resourceId','rr') = $sql.column('SysResource','id','res') ",
		"and $sql.column('SysRoleResource', 'roleId','rr')  =  $sql.column('SysUserRole','roleId','ur') ",
		"and $sql.column('SysUserRole','userId','ur') = #{userId} ",
		"and $sql.column('SysResource', 'deleted','res') = 0 ",
//		"union ",
//		"select $sql.columnsAlias('SysResource', 'res') ",
//		"from $sql.table('SysResource', 'res') ",
//		", $sql.table('SysUserResource', 'ur') ",
//		"where ur.resource_id = res.id and ur.user_id = #{userId} ",
//		"and ur.deleted = 0 ",
//		"and res.deleted = 0 ",
		"}} "})
	@Lang(VelocityLangDriver.class)
	List<SysResource> getUserResources(@Param("userId") Long userId);

}
