package io.microvibe.booster.core.schedule;

import io.microvibe.booster.core.base.entity.EntryableAdaptor;
import lombok.Getter;

public enum TaskRunningState implements EntryableAdaptor {

//	DISABLED, ENABLED, RUNNING, TERMINATED;
	WAITING("等待"),
	RUNNING("运行"),
	ERROR("失败"),
	SUCCESS("成功"),
	TERMINATED("终止"),
	;

	/*WAITING("等待"),
	PAUSED("暂停"),
	ACQUIRED("运行"),
	BLOCKED("阻塞"),
	ERROR("错误"),
	NORUN("停止");*/

	@Getter
	private String info;

	private TaskRunningState(String info) {
		this.info = info;
		register();
	}

}

