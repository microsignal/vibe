package io.microvibe.booster.core.env;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class TaskEnv {
	@Value("${task.executor.corePoolSize:16}")
	int corePoolSize;
	@Value("${task.executor.maxPoolSize:64}")
	int maxPoolSize;
	@Value("${task.executor.queueCapacity:128}")
	int queueCapacity;

	@Value("${task.scheduler.poolSize:16}")
	int schedulerPoolSize;

}
