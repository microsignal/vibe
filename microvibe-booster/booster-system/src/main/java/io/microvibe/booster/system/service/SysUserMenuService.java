package io.microvibe.booster.system.service;

import io.microvibe.booster.core.base.annotation.BaseComponent;
import io.microvibe.booster.system.entity.SysUserMenu;
import io.microvibe.booster.system.mapper.SysUserMenuMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


/**
 * 系统用户菜单映射信息
 *
 * @author wz
 * @version 1.0
 * @since Jul 28, 2018
 */
@Service
@Slf4j
public class SysUserMenuService extends SysBaseService<SysUserMenu, Long> {

	@Autowired
	@BaseComponent
	private SysUserMenuMapper sysUserMenuMapper;


	public void saveUserMenus(Long userId, Long[] added, Long[] deleted) {
		for (Long id : deleted) {
			Map<String, Object> param = new HashMap<>();
			param.put("userId", userId);
			param.put("menuId", id);
			SysUserMenu rel = get(param);
			if (rel != null) {
				delete(rel);
			}
		}
		for (Long id : added) {
			Map<String, Object> param = new HashMap<>();
			param.put("userId", userId);
			param.put("menuId", id);
			SysUserMenu rel = get(param);
			if (rel != null) {
				rel.setDeleted(false);
				updateSelective(rel);
			} else {
				rel = new SysUserMenu();
				rel.setUserId(userId);
				rel.setMenuId(id);
				rel.setDeleted(false);
				insertSelective(rel);
			}
		}
	}
}
