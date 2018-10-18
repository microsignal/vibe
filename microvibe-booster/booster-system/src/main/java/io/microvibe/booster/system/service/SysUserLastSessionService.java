package io.microvibe.booster.system.service;

import io.microvibe.booster.core.base.annotation.BaseComponent;
import io.microvibe.booster.system.entity.SysUserLastSession;
import io.microvibe.booster.system.mapper.SysUserLastSessionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 系统用户最近在线信息表
 *
 * @author Qt
 * @since Aug 01, 2018
 */
@Service
@Slf4j
public class SysUserLastSessionService extends SysBaseService<SysUserLastSession, Long> {

	@Autowired
	@BaseComponent
	private SysUserLastSessionMapper sysUserLastSessionMapper;

}
