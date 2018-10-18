package io.microvibe.booster.core.base.mybatis.statement.builder;

import io.microvibe.booster.core.base.mybatis.statement.MybatisStatementAdapter;
import io.microvibe.booster.core.base.mybatis.statement.StatementBuilder;
import io.microvibe.booster.core.base.persistence.EntityMetaData;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.MapperBuilderAssistant;

import java.lang.reflect.Method;
import java.util.Map;

@Slf4j
public abstract class AbstractStatementBuilder implements StatementBuilder {

	@Override
	public void parseStatement(MapperBuilderAssistant assistant, EntityMetaData entityMetaData,
		Class<?> mapper, Method method) {
		MybatisStatementAdapter adapter = new MybatisStatementAdapter(assistant, mapper);
		adapter.buildMethod(method);
		if (adapter.isDuplicated()) {
			return;
		}


		/*boolean useEntityClass = false;
		Class<?>[] parameterTypes = method.getParameterTypes();
		for (Class<?> parameterType : parameterTypes) {
			if (entityMetaData.getEntityClass().isAssignableFrom(parameterType)) {
				useEntityClass = true;
			}
		}
		if (useEntityClass) {
			adapter.setParameterTypeClass(entityMetaData.getEntityClass());
		} else {
			adapter.setParameterTypeClass(Map.class);
		}*/

		adapter.setParameterTypeClass(Map.class);

		prebuild(adapter, entityMetaData, mapper, method);
		// sqlScript, key
		build(adapter, entityMetaData, mapper, method);

		adapter.parseStatement();
	}

	protected void prebuild(MybatisStatementAdapter adapter, EntityMetaData entityMetaData,
		Class<?> mapper, Method method) {
	}

	protected abstract void build(MybatisStatementAdapter adapter, EntityMetaData entityMetaData, Class<?> mapper, Method method);


}
