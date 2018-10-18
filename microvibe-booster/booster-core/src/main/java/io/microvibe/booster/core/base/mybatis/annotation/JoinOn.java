package io.microvibe.booster.core.base.mybatis.annotation;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 标识实体的表连接信息
 *
 * @author Qt
 * @since Aug 26, 2018
 */
@Documented
@Inherited
@Target({TYPE, FIELD, METHOD})
@Retention(RUNTIME)
@Repeatable(JoinOns.class)
public @interface JoinOn {

	/**
	 * 连接的主表实体属性名
	 *
	 * @return
	 */
	String property() default "";

	/**
	 * 表连接类型(left/right/inner/full)
	 *
	 * @return
	 */
	JoinType joinType() default JoinType.left;

	/**
	 * 连接的表实体
	 *
	 * @return
	 */
	Class<?> table();

	/**
	 * 表连接条件表达式, 需要注意字段条件的表别名需要与FROM子句中一致, 其中主表的别名可为空,生成最终SQL时会填充
	 *
	 * @return
	 */
	String on();

	/**
	 * 连接的表的别名,为空时默认取关联的主表实体的属性名
	 *
	 * @return
	 */
	String tableAlias() default "";

	/**
	 * 字段的前缀, 最终SQL的查询列别名会添加此前缀, 在ResultMap映射关系配置中会使用
	 *
	 * @return
	 */
	String columnPrefix() default "";


	/**
	 * 表连接类型
	 */
	enum JoinType {
		left, right, inner, full
	}

}
