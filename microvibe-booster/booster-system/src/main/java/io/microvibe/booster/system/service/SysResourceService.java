package io.microvibe.booster.system.service;

import io.microvibe.booster.core.base.annotation.BaseComponent;
import io.microvibe.booster.system.entity.SysResource;
import io.microvibe.booster.system.mapper.SysResourceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysResourceService extends SysBaseService<SysResource, Long> {

	@BaseComponent
	@Autowired
	SysResourceMapper sysResourceMapper;


}
