package io.microvibe.booster.system.service;

import io.microvibe.booster.core.base.annotation.BaseComponent;
import io.microvibe.booster.system.entity.SysDict;
import io.microvibe.booster.system.mapper.SysDictMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 系统字典表
 *
 * @author Q
 * @version 1.0
 * @since Jun 19, 2018
 */
@SuppressWarnings("ALL")
@Service
@Slf4j
public class SysDictService extends SysBaseService<SysDict, Long> {

	@Autowired
	@BaseComponent
	private SysDictMapper sysDictMapper;

	public SysDict getDictByCode(String typeCode, String dictCode) {
		return sysDictMapper.getDictByCode(typeCode, dictCode);
	}

	public String getDictValueByCode(String typeCode, String dictCode) {
		return sysDictMapper.getDictValueByCode(typeCode, dictCode);
	}
}
