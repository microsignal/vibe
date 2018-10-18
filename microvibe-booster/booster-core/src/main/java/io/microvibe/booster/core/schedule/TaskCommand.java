package io.microvibe.booster.core.schedule;

import io.microvibe.booster.core.base.entity.EntryableAdaptor;
import lombok.Getter;

public enum TaskCommand implements EntryableAdaptor {

	ENABLE("启用"),
	DISABLE("禁用"),
	RESTART("重启"),
	START("启动"),
	STOP("停止"),
	PAUSE("暂停"),
	RESUME("恢复"),;

	@Getter
	private String info;

	private TaskCommand(String info) {
		this.info = info;
		register();
	}

}

