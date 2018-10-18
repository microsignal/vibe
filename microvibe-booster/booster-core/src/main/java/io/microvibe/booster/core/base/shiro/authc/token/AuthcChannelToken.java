package io.microvibe.booster.core.base.shiro.authc.token;

import io.microvibe.booster.core.base.shiro.authc.AuthcChannel;
import org.apache.shiro.authc.UsernamePasswordToken;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class AuthcChannelToken extends UsernamePasswordToken {

	private static final long serialVersionUID = 1L;
	private List<AuthcChannel> authcChannels = Arrays.asList(
		AuthcChannel.getCurrentChannel().orElse(AuthcChannel.DEFAULT));

	public AuthcChannelToken() {
	}

	public AuthcChannelToken(String username, String password) {
		super(username, password);
	}

	public AuthcChannelToken(String username, String password, boolean rememberMe) {
		super(username, password, rememberMe);
	}


	public AuthcChannelToken(AuthcChannel authcChannel, String username, String password) {
		super(username, password);
		setAuthcChannel(authcChannel);
	}

	public AuthcChannelToken(AuthcChannel[] authcChannel, String username, String password) {
		super(username, password);
		setAuthcChannel(authcChannel);
	}

	public AuthcChannelToken(AuthcChannel authcChannel, String username, String password, boolean rememberMe) {
		super(username, password, rememberMe);
		setAuthcChannel(authcChannel);
	}

	public AuthcChannelToken(AuthcChannel[] authcChannel, String username, String password, boolean rememberMe) {
		super(username, password, rememberMe);
		setAuthcChannel(authcChannel);
	}


	public void setAuthcChannel(AuthcChannel authcChannel) {
		this.authcChannels = Arrays.asList(authcChannel);
	}

	public void addAuthcChannel(AuthcChannel... authcChannels) {
		for (AuthcChannel authcChannel : authcChannels)
			this.authcChannels.add(authcChannel);
	}

	public List<AuthcChannel> getAuthcChannels() {
		return authcChannels;
	}

	public AuthcChannel getAuthcChannel() {
		AuthcChannel authcChannel = null;
		if (authcChannels.size() > 0) {
			authcChannel = authcChannels.get(0);
		}
		return Optional.ofNullable(authcChannel).orElse(AuthcChannel.DEFAULT);
	}

	public void setAuthcChannel(AuthcChannel... authcChannel) {
		this.authcChannels = Arrays.asList(authcChannel);
	}

}
