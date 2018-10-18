package io.microvibe.booster.core.base.shiro.authc.token;

import io.microvibe.booster.core.base.shiro.authc.AuthcChannel;

public class MobileAuthcToken extends AuthcChannelToken {
	private static final long serialVersionUID = 1L;

	public MobileAuthcToken(String username, String password) {
		super(AuthcChannel.mobile, username, password);
	}


	public MobileAuthcToken(String username, String password, boolean rememberMe) {
		super(AuthcChannel.mobile, username, password, rememberMe);
	}

}
