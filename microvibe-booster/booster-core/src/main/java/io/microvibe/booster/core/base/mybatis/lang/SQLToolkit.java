package io.microvibe.booster.core.base.mybatis.lang;

import io.microvibe.booster.core.base.mybatis.builder.BuilderAssistant;
import io.microvibe.booster.core.base.persistence.EntityMetaData;
import io.microvibe.booster.core.base.persistence.FieldMetaData;
import io.microvibe.booster.core.base.persistence.PersistentRecognizer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * <ul>
 * <li></li>
 * </ul>
 * @author Qt
 * @since Jul 24, 2018
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SQLToolkit {

	private static SQLToolkit instance = new SQLToolkit();

	public static SQLToolkit instance() {
		return instance;
	}


	public Class<?> toClass(String entityClassName) {
		try {
			return Class.forName(entityClassName);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public EntityMetaData meta(String entityName) {
		try {
			Class<?> entityClass = toClass(entityName);
			EntityMetaData entityMetaData = PersistentRecognizer.entityMetaData(entityClass);
			return entityMetaData;
		} catch (Exception e) {
			return PersistentRecognizer.entityMetaData(entityName);
		}
	}


	public BuilderAssistant assistant() {
		return BuilderAssistant.instance();
	}

	public String columnsAlias(String entityClassName) {
		EntityMetaData entityMetaData = meta(entityClassName);
		String tableAlias = entityMetaData.getTableAlias();
		return BuilderAssistant.instance().columns(entityMetaData, tableAlias, true);
	}

	public String columnsAlias(String entityClassName, String tableAlias) {
		EntityMetaData entityMetaData = meta(entityClassName);
		return BuilderAssistant.instance().columns(entityMetaData, tableAlias, true);
	}

	public String columnsAlias(String entityClassName, String tableAlias, String columnPrefix) {
		EntityMetaData entityMetaData = meta(entityClassName);
		return BuilderAssistant.instance().columns(entityMetaData, tableAlias, columnPrefix, true);
	}

	public String columns(String entityClassName) {
		EntityMetaData entityMetaData = meta(entityClassName);
		String tableAlias = entityMetaData.getTableAlias();
		return BuilderAssistant.instance().columns(entityMetaData, tableAlias, false);
	}

	public String columns(String entityClassName, String tableAlias) {
		return columns(entityClassName, tableAlias, "", false);
	}

	public String columns(String entityClassName, String tableAlias, boolean propertyAliasable) {
		return columns(entityClassName, tableAlias, "", propertyAliasable);
	}

	public String columns(String entityClassName, String tableAlias, String columnPrefix, boolean propertyAliasable) {
		EntityMetaData entityMetaData = meta(entityClassName);
		return BuilderAssistant.instance().columns(entityMetaData, tableAlias, columnPrefix, propertyAliasable);
	}

	public String columns(String entityClassName, String tableAlias, String columnPrefix) {
		EntityMetaData entityMetaData = meta(entityClassName);
		return BuilderAssistant.instance().columns(entityMetaData, tableAlias, columnPrefix, false);
	}

	public String column(String entityClassName, String fieldName) {
		return column(entityClassName, fieldName, null);
	}

	public String column(String entityClassName, String fieldName, String tableAlias) {
		EntityMetaData entityMetaData = meta(entityClassName);
		FieldMetaData fieldMetaData = entityMetaData.getFieldMetaData(fieldName);
		tableAlias = StringUtils.trimToNull(tableAlias);
		StringBuilder sb = new StringBuilder();
		if (tableAlias != null) {
			sb.append(tableAlias).append(".");
		}
		sb.append(fieldMetaData.getColumnName());
		return sb.toString();
	}

	public String table(String entityClassName) {
		EntityMetaData entityMetaData = meta(entityClassName);
		return BuilderAssistant.instance().table(entityMetaData, entityMetaData.getTableAlias());
	}

	public String table(String entityClassName, String tableAlias) {
		EntityMetaData entityMetaData = meta(entityClassName);
		return BuilderAssistant.instance().table(entityMetaData, tableAlias);
	}

	public String idCondition(String entityClassName) {
		EntityMetaData entityMetaData = meta(entityClassName);
		return BuilderAssistant.instance().whereById(entityMetaData, entityMetaData.getTableAlias());
	}

	public String idCondition(String entityClassName, String tableAlias) {
		EntityMetaData entityMetaData = meta(entityClassName);
		return BuilderAssistant.instance().whereById(entityMetaData, tableAlias);
	}

	public String whereByMap(String entityClassName) {
		EntityMetaData entityMetaData = meta(entityClassName);
		return BuilderAssistant.instance().whereByMap(entityMetaData, entityMetaData.getTableAlias());
	}

	public String whereByMap(String entityClassName, String tableAlias) {
		EntityMetaData entityMetaData = meta(entityClassName);
		return BuilderAssistant.instance().whereByMap(entityMetaData, tableAlias);
	}

	public String whereByMap(String entityClassName, String tableAlias, String searchKeyPrefix) {
		EntityMetaData entityMetaData = meta(entityClassName);
		return BuilderAssistant.instance().whereByMap(entityMetaData, tableAlias, searchKeyPrefix);
	}

	public String updateSets(String entityClassName) {
		EntityMetaData entityMetaData = meta(entityClassName);
		return BuilderAssistant.instance().updateSets(entityMetaData, entityMetaData.getTableAlias());
	}

	public String updateSets(String entityClassName, String tableAlias) {
		EntityMetaData entityMetaData = meta(entityClassName);
		return BuilderAssistant.instance().updateSets(entityMetaData, tableAlias);
	}

	public String updateSelectiveSets(String entityClassName) {
		EntityMetaData entityMetaData = meta(entityClassName);
		return BuilderAssistant.instance().updateSelectiveSets(entityMetaData, entityMetaData.getTableAlias());
	}

	public String updateSelectiveSets(String entityClassName, String tableAlias) {
		EntityMetaData entityMetaData = meta(entityClassName);
		return BuilderAssistant.instance().updateSelectiveSets(entityMetaData, tableAlias);
	}

	public String insertColumns(String entityClassName) {
		EntityMetaData entityMetaData = meta(entityClassName);
		return BuilderAssistant.instance().insertColumns(entityMetaData);
	}

	public String insertColumns(String entityClassName, boolean keyGenerated) {
		EntityMetaData entityMetaData = meta(entityClassName);
		return BuilderAssistant.instance().insertColumns(entityMetaData, keyGenerated);
	}

	public String insertSelectiveColumns(String entityClassName) {
		EntityMetaData entityMetaData = meta(entityClassName);
		return BuilderAssistant.instance().insertSelectiveColumns(entityMetaData);
	}

	public String insertSelectiveColumns(String entityClassName, boolean keyGenerated) {
		EntityMetaData entityMetaData = meta(entityClassName);
		return BuilderAssistant.instance().insertSelectiveColumns(entityMetaData, keyGenerated);
	}


	public String insertValues(String entityClassName) {
		EntityMetaData entityMetaData = meta(entityClassName);
		return BuilderAssistant.instance().insertValues(entityMetaData);
	}

	public String insertValues(String entityClassName, boolean keyGenerated) {
		EntityMetaData entityMetaData = meta(entityClassName);
		return BuilderAssistant.instance().insertValues(entityMetaData, keyGenerated);
	}

	public String insertSelectiveValues(String entityClassName) {
		EntityMetaData entityMetaData = meta(entityClassName);
		return BuilderAssistant.instance().insertSelectiveValues(entityMetaData);
	}

	public String insertSelectiveValues(String entityClassName, boolean keyGenerated) {
		EntityMetaData entityMetaData = meta(entityClassName);
		return BuilderAssistant.instance().insertSelectiveValues(entityMetaData, keyGenerated);
	}


}
