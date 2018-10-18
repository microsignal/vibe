package io.microvibe.booster.core.schedule;

import java.util.List;

/**
 * @author Qt
 * @since Aug 06, 2018
 */
public interface TaskDefinitionDao<T extends TaskDefinition> {

	List<T> findAll();

	T getById(String taskId);

	void updateRunningState(String taskId, TaskRunningState state, String message);

	void updateRunningState(String taskId, TaskRunningState state, String message, Throwable e);

}
