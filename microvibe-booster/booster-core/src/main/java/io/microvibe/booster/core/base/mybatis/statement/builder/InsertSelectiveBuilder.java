package io.microvibe.booster.core.base.mybatis.statement.builder;

import io.microvibe.booster.core.base.mybatis.builder.MapperBuilders;
import io.microvibe.booster.core.base.mybatis.statement.StatementBuilder;
import io.microvibe.booster.core.base.persistence.EntityMetaData;

import java.lang.reflect.Method;

public class InsertSelectiveBuilder extends InsertBuilder implements StatementBuilder {

	@Override
	protected String buildSQL(EntityMetaData entityMetaData, Method method, boolean generated) {
		return MapperBuilders.buildInsertSelective(entityMetaData, generated);
	}

}
