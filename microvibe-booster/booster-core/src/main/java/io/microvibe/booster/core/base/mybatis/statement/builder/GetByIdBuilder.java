package io.microvibe.booster.core.base.mybatis.statement.builder;

import io.microvibe.booster.commons.utils.StringUtils;
import io.microvibe.booster.core.base.mybatis.builder.BuilderAssistant;
import io.microvibe.booster.core.base.mybatis.builder.MapperBuilders;
import io.microvibe.booster.core.base.mybatis.statement.StatementBuilder;
import io.microvibe.booster.core.base.persistence.EntityMetaData;

import java.lang.reflect.Method;

public class GetByIdBuilder extends SelectByEntityBuilder implements StatementBuilder {

	@Override
	protected String buildSQL(EntityMetaData entityMetaData, Method method) {
		String tableAlias = StringUtils.trimToNull(entityMetaData.getTableAlias());
		if (joinMetaDataSet!=null && joinMetaDataSet.size() > 0) {
			BuilderAssistant assistant = BuilderAssistant.instance();
			return MapperBuilders.buildGetById(entityMetaData, tableAlias, false, true,
				joinMetaDataSet);
		} else {
			return MapperBuilders.buildGetById(entityMetaData,tableAlias);
		}
	}

}
