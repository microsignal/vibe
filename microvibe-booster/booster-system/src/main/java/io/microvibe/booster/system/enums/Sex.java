package io.microvibe.booster.system.enums;

import io.microvibe.booster.core.base.entity.EntryableAdaptor;
import lombok.Getter;

@Getter
public enum Sex implements EntryableAdaptor {

	male("男"), female("女"), other("其他");

	private final String info;


	private Sex(String info) {
		this.info = info;
	}

}
