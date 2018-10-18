package io.microvibe.booster.system.mapper;

import io.microvibe.booster.core.base.mybatis.mapping.BaseMapper;
import io.microvibe.booster.core.base.mybatis.annotation.AfterEntityScanner;
import io.microvibe.booster.system.entity.SysUserMenu;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统用户菜单映射信息
 * @since Jul 28, 2018
 * @version 1.0
 * @author wz
 */
@Mapper
@AfterEntityScanner
public interface SysUserMenuMapper extends BaseMapper<SysUserMenu, Long> {

}
