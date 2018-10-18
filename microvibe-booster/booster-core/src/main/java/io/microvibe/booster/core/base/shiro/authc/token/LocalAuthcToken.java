package io.microvibe.booster.core.base.shiro.authc.token;

import io.microvibe.booster.core.base.shiro.authc.AuthcChannel;

public class LocalAuthcToken extends AuthcChannelToken {
	private static final long serialVersionUID = 1L;

	public LocalAuthcToken(String username, String password) {
		super(AuthcChannel.local, username, password);
	}

	public LocalAuthcToken(String username, String password, boolean rememberMe) {
		super(AuthcChannel.local, username, password, rememberMe);
	}

}
