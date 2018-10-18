package io.microvibe.booster.core.base.shiro.session;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Qt
 * @since Jul 31, 2018
 */
public class NoopOnlineSessionDAOService implements IOnlineSessionDAOService {
	@Override
	public OnlineSession doReadSession(String sessionId) {
		return null;
	}

	@Override
	public void doUpdate(OnlineSession session) {
	}

	@Override
	public void doDelete(OnlineSession session) {
	}

	@Override
	public void doKickout(OnlineSession session) {
	}

	@Override
	public Collection<OnlineSession> getSessionsByUserId(Serializable userId) {
		return Collections.emptyList();
	}
}
