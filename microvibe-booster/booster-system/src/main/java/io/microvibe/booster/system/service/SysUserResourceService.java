package io.microvibe.booster.system.service;

import io.microvibe.booster.core.base.annotation.BaseComponent;
import io.microvibe.booster.system.entity.SysUserResource;
import io.microvibe.booster.system.mapper.SysUserResourceMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


/**
 * 系统用户权限配置表
 *
 * @author wz
 * @version 1.0
 * @since Jul 28, 2018
 */
@Service
@Slf4j
public class SysUserResourceService extends SysBaseService<SysUserResource, Long> {

	@Autowired
	@BaseComponent
	private SysUserResourceMapper sysUserResourceMapper;

	public void saveUserResources(Long userId, Long[] added, Long[] deleted) {
		for (Long id : deleted) {
			Map<String, Object> param = new HashMap<>();
			param.put("userId", userId);
			param.put("resourceId", id);
			SysUserResource rel = get(param);
			if (rel != null) {
				delete(rel);
			}
		}
		for (Long id : added) {
			Map<String, Object> param = new HashMap<>();
			param.put("userId", userId);
			param.put("resourceId", id);
			SysUserResource rel = get(param);
			if (rel != null) {
				rel.setDeleted(false);
				updateSelective(rel);
			} else {
				rel = new SysUserResource();
				rel.setUserId(userId);
				rel.setResourceId(id);
				rel.setDeleted(false);
				insertSelective(rel);
			}
		}
	}
}
