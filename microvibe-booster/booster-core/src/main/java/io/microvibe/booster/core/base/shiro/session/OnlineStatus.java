package io.microvibe.booster.core.base.shiro.session;

import io.microvibe.booster.core.base.entity.EntryableAdaptor;
import lombok.Getter;

public enum OnlineStatus implements EntryableAdaptor {

	OFFLINE("离线"), ONLINE("在线"), HIDDEN("隐身"), FORCE_LOGOUT("强制退出");

	@Getter
	private final String info;

	private OnlineStatus(String info) {
		this.info = info;
		register();
	}


}
