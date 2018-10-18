package io.microvibe.booster.system.mapper;

import io.microvibe.booster.core.base.mybatis.mapping.BaseMapper;
import io.microvibe.booster.core.base.mybatis.lang.VelocityLangDriver;
import io.microvibe.booster.system.entity.SysMenu;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu, Long> {

	/**
	 * 用户权限下所有菜单
	 */
	@Select({"{{",
		"select $sql.columnsAlias('SysMenu','m') ",//,$sql.columnsAlias('SysRole','r','r_')
		"from $sql.table('SysMenu','m'), ",
		"$sql.table('SysRoleMenu','rm'), ",
		"$sql.table('SysRole','r'), ",
		"$sql.table('SysUserRole','ur') ",
		"where $sql.column('SysMenu','id','m') = $sql.column('SysRoleMenu','menuId','rm') ",
		"and $sql.column('SysRoleMenu','roleId','rm') = $sql.column('SysRole','id','r') ",
		"and $sql.column('SysUserRole','roleId','ur') = $sql.column('SysRole','id','r') ",
		"and $sql.column('SysMenu','deleted','m') = 0 ",
		"and $sql.column('SysUserRole','userId','ur') = #{userId,jdbcType=VARCHAR} ",
//		"union ",
//		"select $sql.columnsAlias('SysMenu','m') ",
//		"from $sql.table('SysMenu','m'), ",
//		"$sql.table('SysUserMenu','um') ",
//		"where m.id = um.menu_id ",
//		"and m.deleted = 0 ",
//		"and um.user_id = #{userId,jdbcType=VARCHAR} ",
		"order by $sql.column('SysMenu','orderNo','m') asc ",
		"}}",
	})
	@Lang(VelocityLangDriver.class)
	@ResultMap("SysMenuResultMap")
	List<SysMenu> getUserAllMenusForAuth(@Param("userId") Long userId);

	/**
	 * 查询所有菜单栏
	 */
	@Select({"{{",
		"select $sql.columnsAlias('SysMenu','m') ",//,$sql.columnsAlias('SysRole','r','r_')
		"from $sql.table('SysMenu','m'), ",
		"$sql.table('SysRoleMenu','rm'), ",
		"$sql.table('SysRole','r'), ",
		"$sql.table('SysUserRole','ur') ",
		"where $sql.column('SysMenu','id','m') = $sql.column('SysRoleMenu','menuId','rm') ",
		"and $sql.column('SysRoleMenu','roleId','rm') = $sql.column('SysRole','id','r') ",
		"and $sql.column('SysUserRole','roleId','ur') = $sql.column('SysRole','id','r') ",
		"and $sql.column('SysMenu','deleted','m') = 0 ",
		"and $sql.column('SysMenu','type','m') = 0 ",
		"and $sql.column('SysUserRole','userId','ur') = #{userId,jdbcType=VARCHAR} ",
//		"union ",
//		"select $sql.columnsAlias('SysMenu','m') ",
//		"from $sql.table('SysMenu','m'), ",
//		"$sql.table('SysUserMenu','um') ",
//		"where m.id = um.menu_id ",
//		"and um.user_id = #{userId,jdbcType=VARCHAR} ",
//		"and m.deleted = 0 ",
//		"and m.type = 0 ",
		"order by $sql.column('SysMenu','orderNo','m') asc ",
		"}}",
	})
	@Lang(VelocityLangDriver.class)
	@ResultMap("SysMenuResultMap")
	List<SysMenu> getUserAllCategories(@Param("userId") Long userId);

	/**
	 * 查询所有下级菜单
	 */
	@Select({"{{",
		"select $sql.columnsAlias('SysMenu','m') ",//,$sql.columnsAlias('SysRole','r','r_')
		"from $sql.table('SysMenu','m'), ",
		"$sql.table('SysRoleMenu','rm'), ",
		"$sql.table('SysRole','r'), ",
		"$sql.table('SysUserRole','ur') ",
		"where $sql.column('SysMenu','id','m') = $sql.column('SysRoleMenu','menuId','rm') ",
		"and $sql.column('SysRoleMenu','roleId','rm') = $sql.column('SysRole','id','r') ",
		"and $sql.column('SysUserRole','roleId','ur') = $sql.column('SysRole','id','r') ",
		"and $sql.column('SysMenu','deleted','m') = 0 ",
		"and $sql.column('SysMenu','type','m') = 1 ",
		"and $sql.column('SysMenu','parentPath','m') like concat(concat('%',#{parentId,jdbcType=VARCHAR}),'%')  ",
		"and $sql.column('SysUserRole','userId','ur') = #{userId,jdbcType=VARCHAR} ",

//		"union ",
//		"{{",
//		"select $sql.columnsAlias('SysMenu','m') ",
//		"from $sql.table('SysMenu','m'), ",
//		"$sql.table('SysUserMenu','um') ",
//		"}}",
//		"where m.id = um.menu_id ",
//		"and um.user_id = #{userId,jdbcType=VARCHAR} ",
//		"and m.parent_path like concat(concat('%',#{parentId,jdbcType=VARCHAR}),'%') ",
//		"and m.type = 1 ",
//		"and m.deleted = 0 ",
		"order by $sql.column('SysMenu','orderNo','m') asc ",
		"}}",
	})
	@Lang(VelocityLangDriver.class)
	@ResultMap("SysMenuResultMap")
	List<SysMenu> getUserAllMenusByParent(@Param("userId") Long userId, @Param("parentId") Long parentId);

	@Select({"{{",
		"select $sql.columnsAlias('SysMenu','m') ",//,$sql.columnsAlias('SysRole','r','r_')
		"from $sql.table('SysMenu','m'), ",
		"$sql.table('SysRoleMenu','rm'), ",
		"$sql.table('SysRole','r'), ",
		"$sql.table('SysUserRole','ur') ",
		"where $sql.column('SysMenu','id','m') = $sql.column('SysRoleMenu','menuId','rm') ",
		"and $sql.column('SysRoleMenu','roleId','rm') = $sql.column('SysRole','id','r') ",
		"and $sql.column('SysUserRole','roleId','ur') = $sql.column('SysRole','id','r') ",
		"and $sql.column('SysMenu','deleted','m') = 0 ",
		"and $sql.column('SysMenu','type','m') = 2 ",
		"and $sql.column('SysMenu','visible','m') = 1 ",
		"and $sql.column('SysMenu','parentId','m') = #{parentId,jdbcType=VARCHAR} ",
		"and $sql.column('SysUserRole','userId','ur') = #{userId,jdbcType=VARCHAR} ",
//		"union ",
//		"{{",
//		"select $sql.columnsAlias('SysMenu','m') ",
//		"from $sql.table('SysMenu','m'), ",
//		"$sql.table('SysUserMenu','um') ",
//		"}}",
//		"where m.id = um.menu_id ",
//		"and um.user_id = #{userId,jdbcType=VARCHAR} ",
//		"and m.parent_id = #{parentId,jdbcType=VARCHAR}) ",
//		"and m.type = 2 ",
//		"and m.deleted = 0 ",
//		"and m.visible = 1 ",
		"order by $sql.column('SysMenu','orderNo','m') asc ",
		"}}",
	})
	@Lang(VelocityLangDriver.class)
	@ResultMap("SysMenuResultMap")
	List<SysMenu> getUserButtons(@Param("userId") Long userId, @Param("parentId") Long parentId);


	@Select({"{{",
		"select $sql.columnsAlias('SysMenu','m') ",
		"from $sql.table('SysMenu','m') ",
		"where $sql.column('SysMenu','type','m') = 0 ",
		"and $sql.column('SysMenu','deleted','m') = 0 ",
		"order by $sql.column('SysMenu','orderNo','m') asc ",
		"}}",
	})
	@Lang(VelocityLangDriver.class)
	@ResultMap("SysMenuResultMap")
	List<SysMenu> getAllCategories();

	@Select({"{{",
		"select $sql.columnsAlias('SysMenu','m') ",
		"from $sql.table('SysMenu','m') ",
		"where $sql.column('SysMenu','deleted','m') = 0 ",
		"and $sql.column('SysMenu','type','m') = 1 ",
		"and $sql.column('SysMenu','parentPath','m') like concat(concat('%',#{parentId,jdbcType=VARCHAR}),'%')  ",
		"order by $sql.column('SysMenu','orderNo','m') asc ",
		"}}",
	})
	@Lang(VelocityLangDriver.class)
	@ResultMap("SysMenuResultMap")
	List<SysMenu> getAllMenus(@Param("parentId") Long parentId);

	@Select({"{{",
		"select $sql.columnsAlias('SysMenu','m') ",
		"from $sql.table('SysMenu','m') ",
		"where $sql.column('SysMenu','deleted','m') = 0 ",
		"and $sql.column('SysMenu','type','m') = 0 ",
		"and $sql.column('SysMenu','parentId','m') is null ",
		"order by $sql.column('SysMenu','orderNo','m') asc ",
		"}}",
	})
	@Lang(VelocityLangDriver.class)
	@ResultMap("SysMenuResultMap")
	List<SysMenu> getRootCategories();

	@Select({"{{",
		"select $sql.columnsAlias('SysMenu','m') ",
		"from $sql.table('SysMenu','m') ",
		"where $sql.column('SysMenu','deleted','m') = 0 ",
		"and $sql.column('SysMenu','type','m') = 0 ",
		"and $sql.column('SysMenu','parentId','m') = #{parentId,jdbcType=VARCHAR}) ",
		"order by $sql.column('SysMenu','orderNo','m') asc ",
		"}}",
	})
	@Lang(VelocityLangDriver.class)
	@ResultMap("SysMenuResultMap")
	List<SysMenu> getSubCategories(@Param("parentId") Long parentId);

	@Select({"{{",
		"select $sql.columnsAlias('SysMenu','m') ",
		"from $sql.table('SysMenu','m') ",
		"where $sql.column('SysMenu','deleted','m') = 0 ",
		"and $sql.column('SysMenu','type','m') = 1 ",
		"and $sql.column('SysMenu','parentId','m') = #{parentId,jdbcType=VARCHAR}) ",
		"order by $sql.column('SysMenu','orderNo','m') asc ",
		"}}",
	})
	@Lang(VelocityLangDriver.class)
	@ResultMap("SysMenuResultMap")
	List<SysMenu> getSubMenus(@Param("parentId") Long parentId);

	@Select({"{{",
		"select $sql.columnsAlias('SysMenu','m') ",
		"from $sql.table('SysMenu','m') ",
		"where $sql.column('SysMenu','deleted','m') = 0 ",
		"and $sql.column('SysMenu','type','m') = 2 ",
		"and $sql.column('SysMenu','parentId','m') = #{parentId,jdbcType=VARCHAR}) ",
		"order by $sql.column('SysMenu','orderNo','m') asc ",
		"}}",
	})
	@Lang(VelocityLangDriver.class)
	@ResultMap("SysMenuResultMap")
	List<SysMenu> getSubButtons(@Param("parentId") Long parentId);

}
