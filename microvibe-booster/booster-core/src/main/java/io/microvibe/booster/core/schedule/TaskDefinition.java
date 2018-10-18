package io.microvibe.booster.core.schedule;

/**
 * @author Qt
 * @since Aug 06, 2018
 */
public interface TaskDefinition {

	String getTaskId();

	String getName();

	String getCron();

	TaskRunningState getStatus();

	String getClassName();

	String getMethodName();

	Boolean getEnabled();

}
