package io.microvibe.booster.core.base.mybatis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 为了避免与jpa的id定义冲突,引入此注解, 配合 Transient 注解使用避免 jpa 解析
 */
@Documented
@Inherited
@Target({FIELD})
@Retention(RUNTIME)
public @interface PrimaryKey {

}
