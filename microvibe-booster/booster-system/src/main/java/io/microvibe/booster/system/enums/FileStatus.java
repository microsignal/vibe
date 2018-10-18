package io.microvibe.booster.system.enums;

import io.microvibe.booster.core.base.entity.EntryableAdaptor;
import lombok.Getter;

@Getter
public enum FileStatus implements EntryableAdaptor {

	temporary("临时"), normal("正常"), illegal("违规");

	private final String info;

	private FileStatus(String info) {
		this.info = info;
	}

}
