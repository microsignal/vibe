package io.microvibe.booster.core.base.mybatis.annotation;

import io.microvibe.booster.core.base.mybatis.statement.BuilderType;

import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface AutoStatement {

	BuilderType value() default BuilderType.NOOP;
}
