package io.microvibe.booster.system.service;

import io.microvibe.booster.core.base.annotation.BaseComponent;
import io.microvibe.booster.core.base.mybatis.example.Example;
import io.microvibe.booster.core.base.utils.EntityKit;
import io.microvibe.booster.system.entity.SysUserRole;
import io.microvibe.booster.system.mapper.SysUserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class SysUserRoleService extends SysBaseService<SysUserRole, Long> {

	@BaseComponent
	@Autowired
	SysUserRoleMapper sysUserRoleMapper;


	public void addUserRole(Long userId, Long[] roleIds) {
		Set<Long> set = new LinkedHashSet<>();
		for (Long id : roleIds) {
			set.add(id);
		}
		Example<SysUserRole> example = Example.of(SysUserRole.class)
			.equalTo("userId", userId)
			.in("roleId", (Set) set)
			.build();
		List<SysUserRole> list = sysUserRoleMapper.selectByExample(example);
		for (SysUserRole o : list) {
			if (o.deleted()) {
				o.setDeleted(false);
				sysUserRoleMapper.updateSelectiveByEntity(o);
			}
			set.remove(o.getRoleId());
		}
		for (Long id : set) {
			SysUserRole o = new SysUserRole();
			EntityKit.fillKeyFields(o);
			o.setDeleted(false);
			o.setRoleId(id);
			o.setUserId(userId);
			sysUserRoleMapper.insertSelectiveByEntity(o);
		}
	}

	public void delUserRole(String userId, String[] roleIds) {
		Set<String> set = new LinkedHashSet<>();
		for (String id : roleIds) {
			set.add(id);
		}
		Example<SysUserRole> example = Example.of(SysUserRole.class)
			.equalTo("userId", userId)
			.equalTo("deleted", false)
			.in("roleId", (Set) set)
			.build();
		List<SysUserRole> list = sysUserRoleMapper.selectByExample(example);
		for (SysUserRole o : list) {
			sysUserRoleMapper.deleteByEntity(o);
			set.remove(o.getRoleId());
		}
	}
}
