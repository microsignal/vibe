package io.microvibe.booster.system.enums;

import io.microvibe.booster.core.base.entity.EntryableAdaptor;
import lombok.Getter;

@Getter
public enum UserStatus implements EntryableAdaptor {

	uncheck("未审核"), normal("正常状态"), blocked("封禁状态"), recommend("推荐状态");

	private final String info;

	private UserStatus(String info) {
		this.info = info;
	}

}
