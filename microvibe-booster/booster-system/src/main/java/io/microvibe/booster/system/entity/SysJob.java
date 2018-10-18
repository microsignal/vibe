package io.microvibe.booster.system.entity;

import io.microvibe.booster.core.base.entity.CreateDateRecordable;
import io.microvibe.booster.core.base.entity.DeletedRecordable;
import io.microvibe.booster.core.base.entity.EnabledRecordable;
import io.microvibe.booster.core.base.entity.UpdateDateRecordable;
import io.microvibe.booster.core.schedule.TaskDefinition;
import io.microvibe.booster.core.schedule.TaskRunningState;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.*;


/**
 * 系统任务信息
 *
 * @author Q
 * @version 1.0
 * @since Jun 22, 2018
 */
@Entity
@Table(name = "sys_job")
@Getter
@Setter
@EqualsAndHashCode
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@AttributeOverrides(
	@AttributeOverride(name = "id", column = @Column(name = "id"))
)
public class SysJob extends BaseSysUuidEntity
	implements EnabledRecordable, DeletedRecordable, CreateDateRecordable, UpdateDateRecordable,
	TaskDefinition {

	private static final long serialVersionUID = 1L;

	// region columns

	@Column(name = "name")
	private String name;//任务

	@Column(name = "cron")
	private String cron;//任务计划

	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private TaskRunningState status;//任务状态

	@Column(name = "class_name")
	private String className;//执行类名

	@Column(name = "method_name")
	private String methodName;//执行方法名

	@Column(name = "message")
	private String message;//任务状态描述

	@Column(name = "stacktrace")
	private String stacktrace;//错误堆栈

	@Column(name = "intro")
	private String intro;//描述介绍

	@Column(name = "enabled")
	private Boolean enabled;//是否启用

	// endregion columns

	// region constructors

	public SysJob() {
	}

	// endregion constructors


	@Override
	public String getTaskId() {
		return getId();
	}

}

