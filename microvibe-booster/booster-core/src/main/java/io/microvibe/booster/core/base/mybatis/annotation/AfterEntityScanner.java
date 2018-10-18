package io.microvibe.booster.core.base.mybatis.annotation;

import io.microvibe.booster.core.base.mybatis.configuration.EntityPersistentRecognizerScanner;
import io.microvibe.booster.core.base.mybatis.configuration.PersistentEnhancerScanner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.DependsOn;

import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@DependsOn(EntityPersistentRecognizerScanner.BEAN_ID)
public @interface AfterEntityScanner {
}
