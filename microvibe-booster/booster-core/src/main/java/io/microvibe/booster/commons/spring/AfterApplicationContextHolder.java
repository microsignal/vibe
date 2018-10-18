package io.microvibe.booster.commons.spring;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.DependsOn;

import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
@DependsOn(ApplicationContextHolder.BEAN_ID)
@ConditionalOnBean(ApplicationContextHolder.class)
public @interface AfterApplicationContextHolder {
}
