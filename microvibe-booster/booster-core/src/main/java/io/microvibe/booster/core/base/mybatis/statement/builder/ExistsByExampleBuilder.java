package io.microvibe.booster.core.base.mybatis.statement.builder;

import io.microvibe.booster.commons.utils.StringUtils;
import io.microvibe.booster.core.base.mybatis.builder.BuilderAssistant;
import io.microvibe.booster.core.base.mybatis.builder.MapperBuilders;
import io.microvibe.booster.core.base.mybatis.statement.StatementBuilder;
import io.microvibe.booster.core.base.persistence.EntityMetaData;

import java.lang.reflect.Method;

public class ExistsByExampleBuilder extends ExistsByEntityBuilder implements StatementBuilder {

	@Override
	protected String buildSQL(EntityMetaData entityMetaData, Method method) {
		if (joinMetaDataSet != null && joinMetaDataSet.size() > 0) {
			BuilderAssistant assistant = BuilderAssistant.instance();
			String tableAlias = StringUtils.trimToNull(entityMetaData.getTableAlias());
			return MapperBuilders.buildCountBy(entityMetaData, tableAlias, true,joinMetaDataSet,
				() -> assistant.whereByExample());
		} else {
			return MapperBuilders.buildCountByExample(entityMetaData);
		}
	}

}
