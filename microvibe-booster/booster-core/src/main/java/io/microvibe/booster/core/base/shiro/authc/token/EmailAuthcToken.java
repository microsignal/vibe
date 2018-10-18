package io.microvibe.booster.core.base.shiro.authc.token;

import io.microvibe.booster.core.base.shiro.authc.AuthcChannel;

public class EmailAuthcToken extends AuthcChannelToken {
	private static final long serialVersionUID = 1L;

	public EmailAuthcToken(String username, String password) {
		super(AuthcChannel.email, username, password);
	}


	public EmailAuthcToken(String username, String password, boolean rememberMe) {
		super(AuthcChannel.email, username, password, rememberMe);
	}

}
