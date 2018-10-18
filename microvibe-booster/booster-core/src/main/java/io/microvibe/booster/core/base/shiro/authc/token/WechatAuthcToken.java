package io.microvibe.booster.core.base.shiro.authc.token;

import io.microvibe.booster.core.base.shiro.authc.AuthcChannel;

public class WechatAuthcToken extends AuthcChannelToken {
	private static final long serialVersionUID = 1L;

	public WechatAuthcToken(String username, String password) {
		super(AuthcChannel.wechat, username, password);
	}


	public WechatAuthcToken(String username, String password, boolean rememberMe) {
		super(AuthcChannel.wechat, username, password, rememberMe);
	}
}
