package io.microvibe.booster.core.base.shiro.session;

import java.io.Serializable;

public interface SessionKicker {
	/**
	 * 根据会话ID 踢出会话
	 *
	 * @param sessionId 会话ID
	 */
	void kickoutBySessionId(Serializable sessionId);

	/**
	 * 根据用户ID 踢出会话
	 *
	 * @param userId 用户ID
	 */
	void kickoutByUserId(Serializable userId);
}
