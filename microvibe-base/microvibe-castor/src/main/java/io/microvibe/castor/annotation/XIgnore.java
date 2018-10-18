package io.microvibe.castor.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 映射类的字段的序列化配置的描述注解
 *
 * @since 1.0 , Java 1.8 , Sep 19, 2016
 * @version 1.0
 * @author Qt
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({
		ElementType.FIELD
})
public @interface XIgnore {
}
