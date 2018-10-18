package io.microvibe.booster.core.base.mybatis.statement.builder;

import io.microvibe.booster.core.base.mybatis.builder.MapperBuilders;
import io.microvibe.booster.core.base.mybatis.statement.MybatisStatementAdapter;
import io.microvibe.booster.core.base.mybatis.statement.StatementBuilder;
import io.microvibe.booster.core.base.persistence.EntityMetaData;
import io.microvibe.booster.core.base.persistence.FieldMetaData;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.mapping.SqlCommandType;
import org.springframework.core.annotation.AnnotationUtils;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.lang.reflect.Method;
import java.util.List;

public class InsertBuilder extends AbstractStatementBuilder implements StatementBuilder {

	@Override
	protected void build(MybatisStatementAdapter adapter, EntityMetaData entityMetaData, Class<?> mapper, Method method) {
		adapter.setSqlCommandType(SqlCommandType.INSERT);

		Options methodOptions = method.getAnnotation(Options.class);
		boolean generated = false;
		if (methodOptions != null) {
			generated = methodOptions.useGeneratedKeys();
			if (generated) {
				adapter.setKeyGenerator(Jdbc3KeyGenerator.INSTANCE);
				adapter.setKeyProperty(methodOptions.keyProperty());
				adapter.setKeyColumn(methodOptions.keyColumn());
			}
		}
		if (!generated) {
			List<FieldMetaData> pkColumnFields = entityMetaData.getPkColumnFields();
			if (pkColumnFields.size() == 1) {
				FieldMetaData pk = pkColumnFields.get(0);
				GeneratedValue anno = AnnotationUtils.findAnnotation(pk.getField(), GeneratedValue.class);
				if (anno != null && (anno.strategy() == GenerationType.IDENTITY
					|| anno.strategy() == GenerationType.AUTO)) {
					adapter.setKeyGenerator(Jdbc3KeyGenerator.INSTANCE);
					adapter.setKeyProperty(pk.getJavaProperty());
					adapter.setKeyColumn(pk.getColumnName());
				}
			}
		}
		adapter.setGenerated(true);

		// sqlScript
		adapter.setSqlScript(buildSQL(entityMetaData, method, generated));
		adapter.setDynamicSql(true);
	}

	protected String buildSQL(EntityMetaData entityMetaData, Method method, boolean generated) {
		return MapperBuilders.buildInsert(entityMetaData, generated);
	}
}
