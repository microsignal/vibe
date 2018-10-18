package io.microvibe.booster.core.base.mybatis.statement.builder;

import io.microvibe.booster.core.base.mybatis.statement.StatementBuilder;
import io.microvibe.booster.core.base.persistence.EntityMetaData;
import org.apache.ibatis.builder.MapperBuilderAssistant;

import java.lang.reflect.Method;

public class NoopStatementBuilder implements StatementBuilder {

	@Override
	public void parseStatement(MapperBuilderAssistant assistant, EntityMetaData entityMetaData, Class<?> mapper, Method method) {
		//noop
	}

}
