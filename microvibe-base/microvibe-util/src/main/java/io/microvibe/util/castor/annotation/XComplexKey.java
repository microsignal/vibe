package io.microvibe.util.castor.annotation;

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
public @interface XComplexKey {

	/**
	 * @return 复合类型序列化时的映射类的键字段名(注意不是此字段的序列化名称)
	 */
	String value();

}
