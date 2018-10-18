package io.microvibe.booster.core.base.shiro.session;

import java.io.Serializable;
import java.util.Collection;

public interface IOnlineSessionDAOService {

	OnlineSession doReadSession(String sessionId);

	void doUpdate(OnlineSession session);

	void doDelete(OnlineSession session);

	void doKickout(OnlineSession session);

	Collection<OnlineSession> getSessionsByUserId(Serializable userId);

}
