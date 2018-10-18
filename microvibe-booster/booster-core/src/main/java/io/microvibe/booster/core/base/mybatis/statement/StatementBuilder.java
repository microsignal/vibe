package io.microvibe.booster.core.base.mybatis.statement;

import io.microvibe.booster.core.base.persistence.EntityMetaData;
import org.apache.ibatis.builder.MapperBuilderAssistant;

import java.lang.reflect.Method;

/**
 * mybatis statement buider 接口
 *
 * @author svili
 */
public interface StatementBuilder {


	/**
	 * 创建并注册Mybatis Statement
	 *
	 * @param assistant      {@see MapperBuilderAssistant}
	 * @param entityMetaData Entity持久化元数据
	 * @param mapper
	 * @param method         mapper.method
	 */
	void parseStatement(MapperBuilderAssistant assistant, EntityMetaData entityMetaData, Class<?> mapper, Method method);

}
