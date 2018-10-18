package io.microvibe.booster.config.task;

import org.springframework.context.annotation.DependsOn;

import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@DependsOn({TaskConfig.TASK_EXECUTOR_BEAN_ID, TaskConfig.TASK_SCHEDULER_BEAN_ID})
public @interface AfterTaskConfig {
}
