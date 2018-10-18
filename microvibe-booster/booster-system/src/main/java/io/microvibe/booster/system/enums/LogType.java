package io.microvibe.booster.system.enums;

import io.microvibe.booster.core.base.entity.EntryableAdaptor;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum LogType implements EntryableAdaptor {

	AUDIT("审计日志"),
	BATCH("批处理日志"),
	CRON("定时任务日志"),
	SYSTEM("系统操作日志"),
	//
	;

	public static final LogType DEFAULT = AUDIT;//默认审计日志
	@Getter
	private final String info;
	@Getter
	private final Logger logger;

	private LogType(String info) {
		this.info = info;
		Logger logger = LoggerFactory.getLogger(name());
		this.logger = logger;
		register();
	}
}
