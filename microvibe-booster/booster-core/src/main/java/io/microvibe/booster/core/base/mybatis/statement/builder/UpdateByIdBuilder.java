package io.microvibe.booster.core.base.mybatis.statement.builder;

import io.microvibe.booster.core.base.mybatis.builder.MapperBuilders;
import io.microvibe.booster.core.base.mybatis.statement.MybatisStatementAdapter;
import io.microvibe.booster.core.base.mybatis.statement.StatementBuilder;
import io.microvibe.booster.core.base.persistence.EntityMetaData;
import org.apache.ibatis.mapping.SqlCommandType;

import java.lang.reflect.Method;

public class UpdateByIdBuilder extends AbstractStatementBuilder implements StatementBuilder {

	@Override
	protected void build(MybatisStatementAdapter adapter, EntityMetaData entityMetaData, Class<?> mapper, Method method) {
		adapter.setSqlCommandType(SqlCommandType.UPDATE);
		// sqlScript
		adapter.setSqlScript(buildSQL(entityMetaData, method));
		adapter.setDynamicSql(true);
	}

	protected String buildSQL(EntityMetaData entityMetaData, Method method) {
		return MapperBuilders.buildUpdateById(entityMetaData);
	}

}
