package io.microvibe.booster.core.base.controller;

import io.microvibe.booster.core.base.entity.EntryableAdaptor;
import lombok.Getter;

public enum OpType implements EntryableAdaptor {

	CREATE("新增"), UPDATE("修改"), VIEW("查看"), DELETE("删除");

	public static final String KEY = "op";
	@Getter
	private final String info;

	{
		register();
	}

	OpType(String info) {
		this.info = info;
	}


}
