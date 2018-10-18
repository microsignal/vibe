package io.microvibe.booster.core.base.web.utils;

import io.microvibe.booster.core.base.entity.EntryableAdaptor;
import lombok.Getter;

public enum UserAgentType implements EntryableAdaptor {
	pc("PC端"), android("Android"), iphone("IPhone"), wechat("微信");

	@Getter
	private String info;

	private UserAgentType(String info) {
		this.info = info;
		register();
	}
}
