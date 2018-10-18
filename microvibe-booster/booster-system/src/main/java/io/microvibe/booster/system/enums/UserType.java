package io.microvibe.booster.system.enums;

import io.microvibe.booster.core.base.entity.EntryableAdaptor;
import lombok.Getter;

@Getter
public enum UserType implements EntryableAdaptor {

	admin("超级管理员"), manager("管理员"), user("一般用户"), place("场所"), dealer("经销商");

	private final String info;

	private UserType(String info) {
		this.info = info;
	}

}
