package io.microvibe.booster.core.base.shiro.authc;

import io.microvibe.booster.core.base.entity.EntryableAdaptor;
import io.microvibe.booster.core.base.shiro.authc.token.Authcness;
import lombok.Getter;

import java.util.Optional;

/**
 * 认证渠道/方式
 *
 * @author Qt
 * @since Nov 02, 2017
 */
public enum AuthcChannel implements Authcness, EntryableAdaptor {

	/**
	 * 本地认证,通过用户名/手机/邮箱等唯一身份标识与登录密码实现认证
	 */
	local("本地"),
	/**
	 * 邮箱认证, 通过邮箱一次性动态口令认证
	 */
	email("邮箱"),
	/**
	 * 手机, 通过手机信息一次性动态口令认证
	 */
	mobile("手机"),

	/**
	 * 微信, 通过微信开放认证接口登录
	 */
	wechat("微信", false),

	//
	;
	public static final AuthcChannel DEFAULT = AuthcChannel.local;
	private static ThreadLocal<AuthcChannel> currentChannel = new ThreadLocal<>();
	private boolean authcness = true;
	@Getter
	private String info;

	private AuthcChannel(String info) {
		this.info = info;
		register();
	}

	private AuthcChannel(String info, boolean authcness) {
		this.info = info;
		this.authcness = authcness;
		register();
	}

	public static Optional<AuthcChannel> getCurrentChannel() {
		return Optional.ofNullable(currentChannel.get());
	}

	public static void setCurrentChannel(AuthcChannel authcChannel) {
		currentChannel.set(authcChannel);
	}

	public static AuthcChannel getCurrentOrDefault(AuthcChannel authcChannel) {
		return Optional.ofNullable(currentChannel.get()).orElse(authcChannel);
	}

	public static AuthcChannel getCurrentOrDefault() {
		return Optional.ofNullable(currentChannel.get()).orElse(DEFAULT);
	}

	@Override
	public boolean authcness() {
		return authcness;
	}

}
