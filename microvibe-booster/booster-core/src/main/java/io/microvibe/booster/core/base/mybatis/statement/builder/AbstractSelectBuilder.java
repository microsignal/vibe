package io.microvibe.booster.core.base.mybatis.statement.builder;

import io.microvibe.booster.core.base.mybatis.annotation.JoinOn;
import io.microvibe.booster.core.base.mybatis.configuration.PersistentEnhancerBuilder;
import io.microvibe.booster.core.base.mybatis.statement.MybatisStatementAdapter;
import io.microvibe.booster.core.base.mybatis.statement.StatementBuilder;
import io.microvibe.booster.core.base.persistence.EntityMetaData;
import io.microvibe.booster.core.base.persistence.JoinMetaData;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class AbstractSelectBuilder extends AbstractStatementBuilder implements StatementBuilder {


	protected Set<JoinMetaData> joinMetaDataSet;

	protected Set<JoinMetaData> fetchJoinMetaData(EntityMetaData entityMetaData, Class<?> mapper, Method method) {
		Set<JoinOn> mapperJoinOns = AnnotationUtils.getRepeatableAnnotations(mapper, JoinOn.class);
		Set<JoinOn> methodJoinOns = AnnotationUtils.getRepeatableAnnotations(method, JoinOn.class);
		Set<JoinMetaData> joinMetaDataSet = new LinkedHashSet<>(mapperJoinOns.size() + methodJoinOns.size());
		for (JoinOn joinOn : mapperJoinOns) {
			JoinMetaData joinMetaData = new JoinMetaData(entityMetaData, joinOn);
			joinMetaDataSet.add(joinMetaData);
		}
		for (JoinOn joinOn : methodJoinOns) {
			JoinMetaData joinMetaData = new JoinMetaData(entityMetaData, joinOn);
			joinMetaDataSet.add(joinMetaData);
		}
		return joinMetaDataSet;
	}

	@Override
	protected void prebuild(MybatisStatementAdapter adapter, EntityMetaData entityMetaData, Class<?> mapper, Method method) {
		joinMetaDataSet = fetchJoinMetaData(entityMetaData, mapper, method);
		adapter.setSqlCommandType(SqlCommandType.SELECT);
		adapter.setDynamicSql(true);
	}

	protected void fillResultMap(MybatisStatementAdapter adapter, EntityMetaData entityMetaData, Class<?> mapper, Method method) {
		if (!method.isAnnotationPresent(ResultMap.class)) {
			if (joinMetaDataSet!=null && joinMetaDataSet.size() > 0) {
				// 需要构造新的ResultMap
				String resultMapId = mapper.getName() + "." + method.getName() + ".ResultMap";
				PersistentEnhancerBuilder.buildResultMap(adapter.getConfiguration(), resultMapId,
					entityMetaData.getEntityClass(), true, false, joinMetaDataSet);
				adapter.setResultMapId(resultMapId);
			} else {
				adapter.setResultMapId(entityMetaData.getEntityClass().getName());
			}
		}
	}

}
