package io.microvibe.booster.core.schedule;

public interface ScheduleContext {

	void start(String taskId);

	void stop(String taskId);

	void execute(String taskId, TaskCommand command);
}
