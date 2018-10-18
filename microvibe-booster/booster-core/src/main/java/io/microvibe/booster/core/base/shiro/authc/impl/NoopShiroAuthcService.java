package io.microvibe.booster.core.base.shiro.authc.impl;

import io.microvibe.booster.core.base.shiro.authc.AuthcSessionPacket;
import io.microvibe.booster.core.base.shiro.authc.IShiroAuthcService;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;

public class NoopShiroAuthcService implements IShiroAuthcService {

	@Override
	public AuthcSessionPacket doAuthentication(AuthenticationToken token) {
		return null;
	}

	@Override
	public AuthorizationInfo getAuthorizationInfo(AuthcSessionPacket sessionKey) {
		return null;
	}

}
