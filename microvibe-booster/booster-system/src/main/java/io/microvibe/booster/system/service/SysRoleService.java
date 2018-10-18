package io.microvibe.booster.system.service;

import io.microvibe.booster.core.base.annotation.BaseComponent;
import io.microvibe.booster.system.entity.SysRole;
import io.microvibe.booster.system.mapper.SysRoleMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysRoleService extends SysBaseService<SysRole, Long> {

	@Autowired
	@BaseComponent
	private SysRoleMapper roleMapper;

	/**
	 * 用户所有角色
	 *
	 * @param userId
	 * @return
	 */
	public List<SysRole> getRolesByUserId(@Param("userId") Long userId) {
		List<SysRole> roles = roleMapper.getRolesByUserId(userId);
		return roles;
	}

}
