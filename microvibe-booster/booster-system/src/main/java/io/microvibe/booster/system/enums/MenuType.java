package io.microvibe.booster.system.enums;

import io.microvibe.booster.core.base.entity.EntryableAdaptor;
import lombok.Getter;

@Getter
public enum MenuType implements EntryableAdaptor {


	CATEGORY("栏目"), MENU("菜单"), BUTTON("按钮");

	private final String info;

	private MenuType(String info) {
		this.info = info;
	}

}
