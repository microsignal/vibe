package io.microvibe.booster.system.service;

import io.microvibe.booster.commons.spring.ApplicationContextHolder;
import io.microvibe.booster.core.base.annotation.BaseComponent;
import io.microvibe.booster.core.base.shiro.session.SessionKicker;
import io.microvibe.booster.system.entity.SysUserSession;
import io.microvibe.booster.system.mapper.SysUserSessionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 系统用户在线会话信息表
 *
 * @author Qt
 * @since Aug 01, 2018
 */
@Service
@Slf4j
public class SysUserSessionService extends SysBaseService<SysUserSession, String> {

	@Autowired
	@BaseComponent
	private SysUserSessionMapper sysUserSessionMapper;

	public void kickoutUser(String userId) {
		SessionKicker kicker = ApplicationContextHolder.getBean(SessionKicker.class);
		kicker.kickoutByUserId(userId);
	}

	public void kickoutSession(String sessionId) {
		SessionKicker kicker = ApplicationContextHolder.getBean(SessionKicker.class);
		kicker.kickoutBySessionId(sessionId);
	}

}
