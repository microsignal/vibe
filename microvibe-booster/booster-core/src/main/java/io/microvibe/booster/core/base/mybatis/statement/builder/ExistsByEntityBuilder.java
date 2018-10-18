package io.microvibe.booster.core.base.mybatis.statement.builder;

import io.microvibe.booster.commons.utils.StringUtils;
import io.microvibe.booster.core.base.mybatis.builder.BuilderAssistant;
import io.microvibe.booster.core.base.mybatis.builder.MapperBuilders;
import io.microvibe.booster.core.base.mybatis.statement.MybatisStatementAdapter;
import io.microvibe.booster.core.base.mybatis.statement.StatementBuilder;
import io.microvibe.booster.core.base.persistence.EntityMetaData;
import org.apache.ibatis.mapping.SqlCommandType;

import java.lang.reflect.Method;

public class ExistsByEntityBuilder extends AbstractSelectBuilder implements StatementBuilder {

	@Override
	protected void build(MybatisStatementAdapter adapter, EntityMetaData entityMetaData, Class<?> mapper, Method method) {
		adapter.setResultType(Boolean.class);
		// sqlScript
		adapter.setSqlScript(buildSQL(entityMetaData, method));
	}

	protected String buildSQL(EntityMetaData entityMetaData, Method method) {
		String tableAlias = StringUtils.trimToNull(entityMetaData.getTableAlias());
		if (joinMetaDataSet != null && joinMetaDataSet.size() > 0) {
			BuilderAssistant assistant = BuilderAssistant.instance();
			return MapperBuilders.buildCountBy(entityMetaData, tableAlias, true, joinMetaDataSet,
				() -> assistant.whereByEntity(entityMetaData, tableAlias)
					+ "\n" + assistant.whereByJoinEntity(entityMetaData)
					+ "\n" + assistant.whereByJoinEntity(joinMetaDataSet));
		} else {
			return MapperBuilders.buildCountByEntity(entityMetaData, tableAlias);
		}
	}

}
