package io.microvibe.booster.core.base.mybatis.configuration;

import io.microvibe.booster.core.base.mybatis.MybatisConstants;
import io.microvibe.booster.core.base.persistence.EntityMetaData;
import io.microvibe.booster.core.base.persistence.FieldMetaData;
import io.microvibe.booster.core.base.persistence.PersistentRecognizer;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.BaseBuilder;
import org.apache.ibatis.builder.IncompleteElementException;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.builder.ResultMapResolver;
import org.apache.ibatis.mapping.Discriminator;
import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

import javax.persistence.Entity;
import java.util.*;

@Slf4j
public class PersistentResultMapEnhancer extends BaseBuilder {

	/**
	 * mybatis
	 */
	protected MapperBuilderAssistant assistant;

	/**
	 * 命名空间
	 */
	protected String namespace = MybatisConstants.AUTO_MAPPER_NAMESPACE;

	/**
	 * 持久化Entity类型
	 */
	protected Class<?> type;

	/**
	 * 持久化Entity元数据
	 */
	protected EntityMetaData entityMetaData;
	private String resource;

	public PersistentResultMapEnhancer(Configuration configuration, Class<?> type) {
		super(configuration);

		this.resource = "auto-entity:" + type.getName();
		this.assistant = new MapperBuilderAssistant(configuration, resource);

		this.type = type;
		this.entityMetaData = PersistentRecognizer.entityMetaData(type);
	}

	public void enhance() {
//		String resource = "interface " + namespace;
		if (!configuration.isResourceLoaded(resource)) {
			configuration.addLoadedResource(resource);
		}
		assistant.setCurrentNamespace(namespace);

		if (!type.isAnnotationPresent(Entity.class)) {
			return;
		}

		// build and register ResultMap;
		try {
			ResultMapAdapter.parseResultMap(assistant, entityMetaData);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * resultMap adapter
	 */
	private static class ResultMapAdapter {

		static void parseResultMap(MapperBuilderAssistant assistant, EntityMetaData entityMetaData) {
			// auto-mapper + entity-name
			parseResultMap(entityMetaData.getEntityName(), assistant, entityMetaData);
		}

		static void parseResultMap(String id, MapperBuilderAssistant assistant, EntityMetaData entityMetaData) {
			Configuration configuration = assistant.getConfiguration();
			if (configuration.getResultMapNames().contains(id)) {
				return;
			}

			Class<?> resultType = entityMetaData.getEntityClass();

			String extend = null;
			// 是否自动映射
			Boolean autoMapping = false;
			Discriminator discriminator = null;

			List<FieldMetaData> fields = new ArrayList<>();
			fields.addAll(entityMetaData.getAllColumnFields());
			fields.addAll(entityMetaData.getFormulaFields());

			List<ResultMapping> resultMappings = new ArrayList<>();

			// 持久化字段
			for (FieldMetaData columnMeta : fields) {
				// java 字段名
				String property = columnMeta.getJavaProperty();
				// sql 列名
				String column = columnMeta.getColumnName();
				Class<?> javaType = columnMeta.getJavaType();
				JdbcType jdbcType = columnMeta.getJdbcType();

				String nestedSelect = null;
				String nestedResultMap = null;
				String notNullColumn = null;
				String columnPrefix = null;
				String resultSet = null;
				String foreignColumn = null;
				// if primaryKey flags.add(ResultFlag.ID);
				List<ResultFlag> flags = new ArrayList<>();
				// lazy or eager
				boolean lazy = false;
				// enum
				Class<? extends TypeHandler<?>> typeHandlerClass = columnMeta.getTypeHandlerClass();

				/*
				ResultMapping resultMapping = assistant.buildResultMapping(resultType, property, column, javaType,
					jdbcType, nestedSelect, nestedResultMap, notNullColumn, columnPrefix, typeHandlerClass, flags,
					resultSet, foreignColumn, lazy);
				*/
				// 解决[assistant.buildResultMapping]方法中枚举类型的 TypeHandler 混用问题
				TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
				TypeHandler<?> typeHandlerInstance = null;
				if (typeHandlerClass != null) {
					if (!javaType.isEnum()) {
						typeHandlerInstance = typeHandlerRegistry.getMappingTypeHandler(typeHandlerClass);
					}
					if (typeHandlerInstance == null) { // not in registry, create a new one
						typeHandlerInstance = typeHandlerRegistry.getInstance(javaType, typeHandlerClass);
					}
				}

				List<ResultMapping> composites = parseCompositeColumnName(configuration, column);
				ResultMapping resultMapping = new ResultMapping.Builder(configuration, property, column, javaType)
					.jdbcType(jdbcType)
					.nestedQueryId(assistant.applyCurrentNamespace(nestedSelect, true))
					.nestedResultMapId(assistant.applyCurrentNamespace(nestedResultMap, true))
					.resultSet(resultSet)
					.typeHandler(typeHandlerInstance)
					.flags(flags == null ? new ArrayList<ResultFlag>() : flags)
					.composites(composites)
					.notNullColumns(parseMultipleColumnNames(notNullColumn))
					.columnPrefix(columnPrefix)
					.foreignColumn(foreignColumn)
					.lazy(lazy)
					.build();

				resultMappings.add(resultMapping);
			}

			ResultMapResolver resultMapResolver = new ResultMapResolver(assistant, id, resultType, extend,
				discriminator, resultMappings, autoMapping);
			try {
				// 生成ResultMap并加入到Configuration中
				resultMapResolver.resolve();
			} catch (IncompleteElementException e) {
				configuration.addIncompleteResultMap(resultMapResolver);
				throw e;
			}
		}

		private static Set<String> parseMultipleColumnNames(String columnName) {
			Set<String> columns = new HashSet<String>();
			if (columnName != null) {
				if (columnName.indexOf(',') > -1) {
					StringTokenizer parser = new StringTokenizer(columnName, "{}, ", false);
					while (parser.hasMoreTokens()) {
						String column = parser.nextToken();
						columns.add(column);
					}
				} else {
					columns.add(columnName);
				}
			}
			return columns;
		}

		private static List<ResultMapping> parseCompositeColumnName(Configuration configuration, String columnName) {
			List<ResultMapping> composites = new ArrayList<ResultMapping>();
			if (columnName != null && (columnName.indexOf('=') > -1 || columnName.indexOf(',') > -1)) {
				StringTokenizer parser = new StringTokenizer(columnName, "{}=, ", false);
				while (parser.hasMoreTokens()) {
					String property = parser.nextToken();
					String column = parser.nextToken();
					ResultMapping complexResultMapping = new ResultMapping.Builder(
						configuration, property, column, configuration.getTypeHandlerRegistry().getUnknownTypeHandler()).build();
					composites.add(complexResultMapping);
				}
			}
			return composites;
		}
	}

}
