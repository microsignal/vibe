package io.microvibe.booster.core.base.shiro.session;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class OnlineSessionDAO extends EnterpriseCacheSessionDAO implements SessionKicker {

	public static final String ATTR_KEY_EXPECTED_TIMEOUT = "expectedTimeout";
	private int memoryCacheSize = 1024;//本地缓冲池大小,超限后会移除旧会话(同步redis/db)
	private Map<Serializable, Session> sessions; //本地缓冲池
	@Getter
	@Setter
	@Autowired
	private IOnlineSessionDAOService onlineSessionDAOService;
	@Getter
	@Setter
	private long sessionPersistenceInterval = 1000 * 60 * 5;
	@Getter
	@Setter
	private long temporarySessionTimeout = 1000 * 60 * 5;

	public OnlineSessionDAO() {
		init();
	}

	public OnlineSessionDAO(int memoryCacheSize) {
		this.memoryCacheSize = memoryCacheSize;
		init();
	}

	/**
	 * 初始化本地会话缓存
	 */
	private void init() {
		sessions = Collections.synchronizedMap(new LinkedHashMap<Serializable, Session>() {
			@Override
			protected boolean removeEldestEntry(Map.Entry<Serializable, Session> eldest) {
				return this.size() > memoryCacheSize;
			}
		});
	}


	// region 本地会话缓存实现

	private Session getLocal(Serializable sessionId) {
		if (sessionId != null) {
			Session cached = sessions.get(sessionId);
			if (cached != null) {
				if (cached instanceof OnlineSession) {
					OnlineSession onlineSession = (OnlineSession) cached;
					long currentTimeMillis = System.currentTimeMillis();
					// 本地存在缓存对象,且在有效期内(最近持久化间隔期间)
					if (onlineSession.getLastPersistTime() + sessionPersistenceInterval > currentTimeMillis) {
						return cached;
					}
				} else {
					return cached;
				}
			}
		}
		return null;
	}

	/**
	 * 优先使用本地内存
	 */
	@Override
	protected Session getCachedSession(Serializable sessionId) {
		Session cached = getLocal(sessionId);
		if (cached == null) {
			cached = super.getCachedSession(sessionId);
			if (cached != null) {
				sessions.put(sessionId, cached);
			}
		}
		return cached;
	}

	/**
	 * 优先使用本地内存
	 */
	@Override
	protected void cache(Session session, Serializable sessionId) {
		if (session == null || sessionId == null) {
			return;
		}
		sessions.put(sessionId, session);// 直接放入本地缓冲池
		if (session instanceof OnlineSession) {
			OnlineSession onlineSession = (OnlineSession) session;
			if (onlineSession.getUserId() == null
				&& !(onlineSessionDAOService instanceof NoopOnlineSessionDAOService)) {
				// 未经用户认证的会话,禁用Redis等缓存,仅使用数据库,以缓解会话数暴增的压力
				return;
			}
		}
		super.cache(session, sessionId);
	}

	/**
	 * 优先使用本地内存
	 */
	@Override
	protected void uncache(Session session) {
		if (session == null) {
			return;
		}
		Serializable id = session.getId();
		if (id == null) {
			return;
		}
		sessions.remove(id);
		super.uncache(session);
	}

	// endregion

	// region 数据库会话实现

	@Override
	protected Session doReadSession(Serializable sessionId) {
		if (onlineSessionDAOService != null && sessionId instanceof String) {
			return onlineSessionDAOService.doReadSession((String) sessionId);
		}
		return super.doReadSession(sessionId);
	}

	@Override
	protected void doUpdate(Session session) {
		if (session instanceof OnlineSession) {
			OnlineSession onlineSession = (OnlineSession) session;
			if (onlineSession.getUserId() == null) {
				// 未经用户认证的会话
				if (onlineSession.getAttribute(ATTR_KEY_EXPECTED_TIMEOUT) == null) {
					onlineSession.setAttribute(ATTR_KEY_EXPECTED_TIMEOUT, onlineSession.getTimeout());
					onlineSession.setTimeout(temporarySessionTimeout);
				}
			} else {
				Object expectedTimeout = onlineSession.getAttribute(ATTR_KEY_EXPECTED_TIMEOUT);
				if (expectedTimeout != null && expectedTimeout instanceof Long) {
					onlineSession.setTimeout((Long) expectedTimeout);
					onlineSession.removeAttribute(ATTR_KEY_EXPECTED_TIMEOUT);
				}
			}
			if (onlineSessionDAOService != null) {
				if (onlineSession.isChanged() ||
					onlineSession.getLastPersistTime() + sessionPersistenceInterval < System.currentTimeMillis()) {
					try {
						onlineSessionDAOService.doUpdate(onlineSession);
						onlineSession.persist();//changed to persisted
					} catch (Exception e) {
						log.warn(e.getMessage(), e);
					}
				}
			}

		}
	}

	@Override
	protected void doDelete(Session session) {
		if (session instanceof OnlineSession) {
			if (onlineSessionDAOService != null) {
				OnlineSession onlineSession = (OnlineSession) session;
				if (onlineSession.isChanged() ||
					onlineSession.getLastPersistTime() + sessionPersistenceInterval < System.currentTimeMillis()) {
					try {
						onlineSessionDAOService.doDelete((OnlineSession) session);
						onlineSession.persist();//changed to persisted
					} catch (Exception e) {
						log.warn(e.getMessage(), e);
					}
				}
			}
		}
	}

	// endregion


	// region 踢出会话

	@Override
	public void kickoutBySessionId(Serializable sessionId) {
		Session session = readSession(sessionId);
		if (onlineSessionDAOService != null && session instanceof OnlineSession) {
			OnlineSession onlineSession = (OnlineSession) session;
			onlineSession.setStatus(OnlineStatus.FORCE_LOGOUT);
			onlineSessionDAOService.doKickout(onlineSession);
			onlineSession.persist();//changed to persisted
			// override cache
			cache(session, sessionId);
		}
	}

	@Override
	public void kickoutByUserId(Serializable userId) {
		if (onlineSessionDAOService != null) {
			Collection<OnlineSession> sessions = onlineSessionDAOService.getSessionsByUserId(userId);
			for (OnlineSession onlineSession : sessions) {
				onlineSession.setStatus(OnlineStatus.FORCE_LOGOUT);
				onlineSessionDAOService.doKickout(onlineSession);
				onlineSession.persist();//changed to persisted
				cache(onlineSession, onlineSession.getId());// override cache
			}
		}
	}
	// endregion

}
