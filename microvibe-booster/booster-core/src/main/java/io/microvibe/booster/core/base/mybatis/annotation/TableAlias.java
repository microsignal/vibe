package io.microvibe.booster.core.base.mybatis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Inherited
@Target({TYPE})
@Retention(RUNTIME)
public @interface TableAlias {
	String DEFAULT = "t";

	String value() default DEFAULT;
}
