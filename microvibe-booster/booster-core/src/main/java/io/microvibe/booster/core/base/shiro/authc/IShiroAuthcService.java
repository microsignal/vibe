package io.microvibe.booster.core.base.shiro.authc;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;

import java.io.Serializable;

public interface IShiroAuthcService<K extends Serializable> {

	/**
	 * login
	 *
	 * @param token
	 * @return
	 */
	AuthcSessionPacket<K> doAuthentication(AuthenticationToken token);

	/**
	 * auth
	 *
	 * @param sessionKey
	 * @return
	 */
	AuthorizationInfo getAuthorizationInfo(AuthcSessionPacket<K> sessionKey);


}
