package io.microvibe.booster.system.service;

import io.microvibe.booster.core.base.annotation.BaseComponent;
import io.microvibe.booster.core.base.service.MybatisBaseService;
import io.microvibe.booster.system.entity.SysRoleResource;
import io.microvibe.booster.system.mapper.SysRoleResourceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SysRoleResourceService extends MybatisBaseService<SysRoleResource, Long> {

	@BaseComponent
	@Autowired
	SysRoleResourceMapper sysRoleResourceMapper;

	public void saveRoleResources(Long roleId, Long[] added, Long[] deleted) {
		for (Long id : deleted) {
			Map<String, Object> param = new HashMap<>();
			param.put("roleId", roleId);
			param.put("resourceId", id);
			SysRoleResource rel = get(param);
			if (rel != null) {
				delete(rel);
			}
		}
		for (Long id : added) {
			Map<String, Object> param = new HashMap<>();
			param.put("roleId", roleId);
			param.put("resourceId", id);
			SysRoleResource rel = get(param);
			if (rel != null) {
				rel.setDeleted(false);
				updateSelective(rel);
			} else {
				rel = new SysRoleResource();
				rel.setRoleId(roleId);
				rel.setResourceId(id);
				rel.setDeleted(false);
				insertSelective(rel);
			}
		}
	}

}
