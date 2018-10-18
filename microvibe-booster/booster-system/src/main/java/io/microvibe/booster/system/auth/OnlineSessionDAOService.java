package io.microvibe.booster.system.auth;

import io.microvibe.booster.core.base.mybatis.example.Example;
import io.microvibe.booster.core.base.shiro.session.IOnlineSessionDAOService;
import io.microvibe.booster.core.base.shiro.session.OnlineSession;
import io.microvibe.booster.core.base.shiro.session.OnlineStatus;
import io.microvibe.booster.system.entity.SysUserLastSession;
import io.microvibe.booster.system.entity.SysUserSession;
import io.microvibe.booster.system.service.SysUserLastSessionService;
import io.microvibe.booster.system.service.SysUserSessionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.InvalidSessionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Qt
 * @since Aug 01, 2018
 */
@Slf4j
@Component
public class OnlineSessionDAOService implements IOnlineSessionDAOService {
	@Autowired
	private SysUserSessionService userSessionService;
	@Autowired
	private SysUserLastSessionService userLastSessionService;

	@Override
	public OnlineSession doReadSession(String sessionId) {
		SysUserSession userOnline = null;
		try {
			userOnline = userSessionService.getById(sessionId);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		if (userOnline == null || userOnline.deleted() || userOnline.getStatus() == OnlineStatus.OFFLINE) {
			// 无此会话，或处于离线
			return null;
		}
		OnlineSession session = userOnline.getSession();
		try {
			session.validate();
		} catch (InvalidSessionException e) {
			this.doDelete(session);
		}
		return session;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void doUpdate(OnlineSession session) {
		if (session.getStatus() == null) {
			// change to online
			session.setStatus(OnlineStatus.ONLINE);
		}
		SysUserSession userSession = SysUserSession.fromOnlineSession(session);
		boolean saved = false;
		if (userSessionService.existsById(userSession)) {
			if (userSession.getSessionPersistTime() > session.getLastPersistTime()) {
				// 以数据库中会话为最新会话状态
				session.setStatus(userSession.getStatus());// 踢出会话需要此实现
				return;
			} else {
				session.setLastPersistTime(System.currentTimeMillis());
				userSession.setSessionPersistTime(session.getLastPersistTime());
				userSessionService.updateSelective(userSession);
				saved = true;
			}
		} else {
			session.setLastPersistTime(System.currentTimeMillis());
			userSession.setSessionPersistTime(session.getLastPersistTime());
			userSessionService.insertSelective(userSession);
			saved = true;
		}

		if (saved && session.getUserId() != null) {
			SysUserLastSession lastSession = userLastSessionService.getById((Long) session.getUserId());
			if (lastSession != null) {
				if (lastSession.getLastSessionId() != null
					&& lastSession.getLastSessionId().equalsIgnoreCase(userSession.getId())) {
					SysUserLastSession.merge(SysUserLastSession.fromUserOnline(userSession), lastSession);
				} else {
					SysUserLastSession.merge(SysUserLastSession.fromUserOnline(userSession), lastSession);
					// 不同会话时,记录登录次数
					lastSession.incLoginCount();
				}
				lastSession.incTotalOnlineTime();
				userLastSessionService.updateSelective(lastSession);
			} else {
				lastSession = SysUserLastSession.fromUserOnline(userSession);
				lastSession.incLoginCount();
				lastSession.incTotalOnlineTime();
				userLastSessionService.insertSelective(lastSession);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void doDelete(OnlineSession session) {
		// deleted & offline
		session.setStatus(OnlineStatus.OFFLINE);
		session.setLastPersistTime(System.currentTimeMillis());
		SysUserSession userSession = SysUserSession.fromOnlineSession(session);
		userSession.setDeleted(true);
		userSessionService.updateSelective(userSession);

		Serializable userId = session.getUserId();
		if (userId != null) {
			SysUserLastSession lastSession = userLastSessionService.getById((Long) userId);
			if (lastSession != null) {
				SysUserLastSession.merge(SysUserLastSession.fromUserOnline(userSession), lastSession);
				lastSession.incTotalOnlineTime();
				userLastSessionService.updateSelective(lastSession);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void doKickout(OnlineSession session) {
		session.setStatus(OnlineStatus.FORCE_LOGOUT);
		session.setLastPersistTime(System.currentTimeMillis());

		SysUserSession userSession = new SysUserSession();
		userSession.setId(session.getId().toString());
		userSession.setStatus(session.getStatus());
		userSession.setSessionPersistTime(session.getLastPersistTime());
		userSessionService.updateSelective(userSession);

		Serializable userId = session.getUserId();
		if (userId != null) {
			SysUserLastSession lastSession = userLastSessionService.getById((Long) session.getUserId());
			if (lastSession != null) {
				SysUserLastSession.merge(SysUserLastSession.fromUserOnline(userSession), lastSession);
				lastSession.incTotalOnlineTime();
				userLastSessionService.updateSelective(lastSession);
			}
		}
	}

	@Override
	public Collection<OnlineSession> getSessionsByUserId(Serializable userId) {
		Example<SysUserSession> example = Example.of(SysUserSession.class)
			.equalTo("userId", userId.toString())
			.or()
			.equalTo("status", OnlineStatus.ONLINE)
			.equalTo("status", OnlineStatus.HIDDEN)
			.end()
			.build();
		List<SysUserSession> sessions = userSessionService.findAll(example);
		return sessions.stream().map(userSession -> userSession.getSession()).collect(Collectors.toList());
	}

}
