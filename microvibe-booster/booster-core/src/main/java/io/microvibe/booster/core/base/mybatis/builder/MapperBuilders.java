package io.microvibe.booster.core.base.mybatis.builder;

import io.microvibe.booster.commons.utils.StringUtils;
import io.microvibe.booster.core.base.persistence.EntityMetaData;
import io.microvibe.booster.core.base.persistence.JoinMetaData;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.function.Supplier;

public class MapperBuilders {

	public static Class<?>[] getMapperGenerics(Class<?> mapperType) {
		Type[] types = mapperType.getGenericInterfaces();
		for (Type type : types) {
			if (!(type instanceof ParameterizedType)) {
				continue;
			}
			ParameterizedType parameterizedType = (ParameterizedType) type;
			/*if (!EntityMapper.class.isAssignableFrom((Class<?>) parameterizedType.getRawType())) {
				continue;
			}*/
			Type[] typeArguments = parameterizedType.getActualTypeArguments();
			Class<?>[] generics = new Class[typeArguments.length];
			for (int i = 0; i < typeArguments.length; i++) {
				generics[i] = (Class<?>) typeArguments[i];
			}
			return generics;
		}
		return null;
	}

	public static String buildExistsById(EntityMetaData entityMetaData) {
		String tableAlias = StringUtils.trimToNull(entityMetaData.getTableAlias());
		return buildExistsById(entityMetaData, tableAlias);
	}

	public static String buildExistsById(EntityMetaData entityMetaData, String tableAlias) {
		BuilderAssistant assistant = BuilderAssistant.instance();

		StringBuilder sql = new StringBuilder();
		sql.append("select count(*)");
		sql.append("\n");

		sql.append("from ").append(assistant.table(entityMetaData, tableAlias));
		sql.append(assistant.joinTables(entityMetaData, tableAlias));

		sql.append("\n");
		sql.append("where ");
		sql.append("\n");
		sql.append(assistant.whereById(entityMetaData, tableAlias));
		return sql.toString();
	}


	public static String buildSelectByExample(EntityMetaData entityMetaData) {
		String tableAlias = StringUtils.trimToNull(entityMetaData.getTableAlias());
		return buildSelectByExample(entityMetaData, tableAlias);
	}

	public static String buildSelectByExample(EntityMetaData entityMetaData, String tableAlias) {
		return buildSelectByExample(entityMetaData, tableAlias, false);
	}

	public static String buildSelectByExample(EntityMetaData entityMetaData, String tableAlias, boolean propertyAliasable) {
		BuilderAssistant assistant = BuilderAssistant.instance();
		return buildSelectBy(entityMetaData, tableAlias, propertyAliasable, true,
			null, () -> assistant.whereByExample());
	}

	public static String buildSelectByMap(EntityMetaData entityMetaData) {
		String tableAlias = StringUtils.trimToNull(entityMetaData.getTableAlias());
		return buildSelectByMap(entityMetaData, tableAlias);
	}

	public static String buildSelectByMap(EntityMetaData entityMetaData, String tableAlias) {
		return buildSelectByMap(entityMetaData, tableAlias, false);
	}

	public static String buildSelectByMap(EntityMetaData entityMetaData, String tableAlias, boolean propertyAliasable) {
		BuilderAssistant assistant = BuilderAssistant.instance();
		return buildSelectBy(entityMetaData, tableAlias, propertyAliasable, true,
			null, () -> assistant.wrapByTrim(assistant.whereByMap(entityMetaData, tableAlias))
				+ "\n" + assistant.wrapByTrim(assistant.whereByJoinMap(entityMetaData)));
	}

	public static String buildSelectByEntity(EntityMetaData entityMetaData) {
		String tableAlias = StringUtils.trimToNull(entityMetaData.getTableAlias());
		return buildSelectByEntity(entityMetaData, tableAlias, false);
	}

	public static String buildSelectByEntity(EntityMetaData entityMetaData, String tableAlias) {
		return buildSelectByEntity(entityMetaData, tableAlias, false);
	}

	public static String buildSelectByEntity(EntityMetaData entityMetaData, String tableAlias, boolean propertyAliasable) {
		BuilderAssistant assistant = BuilderAssistant.instance();
		return buildSelectBy(entityMetaData, tableAlias, propertyAliasable, true,
			null, () -> assistant.wrapByTrim(assistant.whereByEntity(entityMetaData, tableAlias))
				+ "\n" + assistant.wrapByTrim(assistant.whereByJoinEntity(entityMetaData)));
	}

	public static String buildCountByMap(EntityMetaData entityMetaData) {
		String tableAlias = StringUtils.trimToNull(entityMetaData.getTableAlias());
		return buildCountByMap(entityMetaData, tableAlias);
	}

	public static String buildCountByMap(EntityMetaData entityMetaData, String tableAlias) {
		BuilderAssistant assistant = BuilderAssistant.instance();
		return buildCountBy(entityMetaData, tableAlias, true,
			null, () -> assistant.wrapByTrim(assistant.whereByMap(entityMetaData, tableAlias))
				+ "\n" + assistant.wrapByTrim(assistant.whereByJoinMap(entityMetaData)));
	}

	public static String buildCountByEntity(EntityMetaData entityMetaData) {
		String tableAlias = StringUtils.trimToNull(entityMetaData.getTableAlias());
		return buildCountByEntity(entityMetaData, tableAlias);
	}

	public static String buildCountByEntity(EntityMetaData entityMetaData, String tableAlias) {
		BuilderAssistant assistant = BuilderAssistant.instance();
		return buildCountBy(entityMetaData, tableAlias, true,
			null, () -> assistant.wrapByTrim(assistant.whereByEntity(entityMetaData, tableAlias))
				+ "\n" + assistant.wrapByTrim(assistant.whereByJoinEntity(entityMetaData)));
	}

	public static String buildCountByExample(EntityMetaData entityMetaData) {
		String tableAlias = StringUtils.trimToNull(entityMetaData.getTableAlias());
		return buildCountByExample(entityMetaData, tableAlias);
	}

	public static String buildCountByExample(EntityMetaData entityMetaData, String tableAlias) {
		BuilderAssistant assistant = BuilderAssistant.instance();
		return buildCountBy(entityMetaData, tableAlias, true,
			null, () -> assistant.whereByExample());
	}

	public static String buildSelectBy(EntityMetaData entityMetaData, String tableAlias, boolean propertyAliasable,
		boolean addDefaultJoins, Collection<JoinMetaData> additionalJoins, Supplier<String> whereSupplier) {
		BuilderAssistant assistant = BuilderAssistant.instance();
		StringBuilder sql = new StringBuilder();
		sql.append("select ");
		sql.append(assistant.columns(entityMetaData, tableAlias, propertyAliasable, addDefaultJoins, additionalJoins));
		sql.append("\n");

		sql.append("from ").append(assistant.table(entityMetaData, tableAlias, addDefaultJoins, additionalJoins));
		sql.append("\n");

		sql.append("<where>");
		sql.append("\n");
		sql.append(whereSupplier.get());
		sql.append("\n");
		sql.append(assistant.whereExtClause(tableAlias));
		sql.append("\n");
		sql.append("</where>");
		sql.append("\n");
		sql.append(assistant.orderByClause(tableAlias));
		return sql.toString();
	}

	public static String buildCountBy(EntityMetaData entityMetaData, String tableAlias,
		boolean addDefaultJoins, Collection<JoinMetaData> additionalJoins, Supplier<String> whereSupplier) {
		BuilderAssistant assistant = BuilderAssistant.instance();
		StringBuilder sql = new StringBuilder();
		sql.append("select count(*)");
		sql.append("\n");

		sql.append("from ").append(assistant.table(entityMetaData, tableAlias, addDefaultJoins, additionalJoins));
		sql.append("\n");

		sql.append("<where>");
		sql.append("\n");
		sql.append(whereSupplier.get());
		sql.append("\n");
		sql.append(assistant.whereExtClause(tableAlias));
		sql.append("\n");
		sql.append("</where>");
		return sql.toString();
	}

	public static String buildGetById(EntityMetaData entityMetaData) {
		String tableAlias = StringUtils.trimToNull(entityMetaData.getTableAlias());
		return buildGetById(entityMetaData, tableAlias, false, true, null);
	}

	public static String buildGetById(EntityMetaData entityMetaData, String tableAlias) {
		return buildGetById(entityMetaData, tableAlias, false, true, null);
	}

	public static String buildGetById(EntityMetaData entityMetaData, String tableAlias, boolean propertyAliasable) {
		return buildGetById(entityMetaData, tableAlias, propertyAliasable, true, null);
	}

	public static String buildGetById(EntityMetaData entityMetaData, String tableAlias, boolean propertyAliasable,
		boolean addDefaultJoins, Collection<JoinMetaData> additionalJoins) {
		BuilderAssistant assistant = BuilderAssistant.instance();
		String tableName = entityMetaData.getTableName();

		StringBuilder sql = new StringBuilder();

		sql.append("select ");
		sql.append(assistant.columns(entityMetaData, tableAlias, propertyAliasable, addDefaultJoins, additionalJoins));
		sql.append("\n");

		sql.append("from ").append(assistant.table(entityMetaData, tableAlias));
		if (addDefaultJoins) {
			sql.append("\n");
			sql.append(assistant.joinTables(entityMetaData, tableAlias));
		}

		sql.append("\n");
		sql.append("where");
		sql.append("\n");
		sql.append(assistant.whereById(entityMetaData, tableAlias));
		return sql.toString();
	}

	public static String buildDeleteById(EntityMetaData entityMetaData) {
		String tableAlias = StringUtils.trimToNull(entityMetaData.getTableAlias());
		return buildDeleteById(entityMetaData, tableAlias);
	}

	public static String buildDeleteById(EntityMetaData entityMetaData, String tableAlias) {
		BuilderAssistant assistant = BuilderAssistant.instance();
		String tableName = entityMetaData.getTableName();
		// fix: mysql delete 子句中不能使用别名
		tableAlias = null;

		StringBuilder sql = new StringBuilder();

		sql.append("delete from ").append(tableName);
		if (tableAlias != null) {
			sql.append(" ").append(tableAlias);
		}
		sql.append("\n");
		sql.append("where ");
		sql.append("\n");
		sql.append(assistant.whereById(entityMetaData, tableAlias));
		if (entityMetaData.getVersionColumnField() != null) {
			sql.append("\n");
			sql.append(assistant.whereByVersion(entityMetaData, tableAlias, " and ", false));
		}
		return sql.toString();
	}

	public static String buildUpdateSelectiveById(EntityMetaData entityMetaData) {
		String tableAlias = StringUtils.trimToNull(entityMetaData.getTableAlias());
		return buildUpdateSelectiveById(entityMetaData, tableAlias);
	}

	public static String buildUpdateSelectiveById(EntityMetaData entityMetaData, String tableAlias) {
		BuilderAssistant assistant = BuilderAssistant.instance();
		String tableName = entityMetaData.getTableName();

		StringBuilder sql = new StringBuilder();

		sql.append("update ").append(tableName);
		if (tableAlias != null) {
			sql.append(" ").append(tableAlias);
		}
		sql.append("\n");
		sql.append(" set ");
		sql.append("\n");
		sql.append(assistant.updateSelectiveSets(entityMetaData, tableAlias));
		if (entityMetaData.getVersionColumnField() != null) {
			sql.append("\n");
			sql.append(",").append(entityMetaData.getVersionColumnField().getColumnName());
			sql.append(" = ").append(entityMetaData.getVersionColumnField().getColumnName());
			sql.append(" + 1");
		}
		sql.append("\n");
		sql.append(" where ");
		sql.append("\n");
		sql.append(assistant.whereById(entityMetaData, tableAlias));
		if (entityMetaData.getVersionColumnField() != null) {
			sql.append("\n");
			sql.append(assistant.whereByVersion(entityMetaData, tableAlias, " and "));
		}
		return sql.toString();
	}

	public static String buildUpdateById(EntityMetaData entityMetaData) {
		String tableAlias = StringUtils.trimToNull(entityMetaData.getTableAlias());
		return buildUpdateById(entityMetaData, tableAlias);
	}

	public static String buildUpdateById(EntityMetaData entityMetaData, String tableAlias) {
		BuilderAssistant assistant = BuilderAssistant.instance();
		String tableName = entityMetaData.getTableName();

		StringBuilder sql = new StringBuilder();
		sql.append("update ").append(tableName);
		if (tableAlias != null) {
			sql.append(" ").append(tableAlias);
		}
		sql.append("\n");
		sql.append(" set ");
		sql.append("\n");
		sql.append(assistant.updateSets(entityMetaData, tableAlias));
		if (entityMetaData.getVersionColumnField() != null) {
			sql.append("\n");
			sql.append(",").append(entityMetaData.getVersionColumnField().getColumnName());
			sql.append(" = ").append(entityMetaData.getVersionColumnField().getColumnName());
			sql.append(" + 1");
		}
		sql.append("\n");
		sql.append(" where ");
		sql.append("\n");
		sql.append(assistant.whereById(entityMetaData, tableAlias));
		if (entityMetaData.getVersionColumnField() != null) {
			sql.append("\n");
			sql.append(assistant.whereByVersion(entityMetaData, tableAlias, " and "));
		}
		return sql.toString();
	}

	public static String buildInsertSelective(EntityMetaData entityMetaData, boolean keyGenerated) {
		BuilderAssistant assistant = BuilderAssistant.instance();
		String tableName = entityMetaData.getTableName();

		StringBuilder sql = new StringBuilder();
		sql.append("insert into ").append(tableName);
		sql.append("\n");
		sql.append(" ( ");
		sql.append("\n");
		sql.append(assistant.insertSelectiveColumns(entityMetaData, keyGenerated));
		if (entityMetaData.getVersionColumnField() != null) {
			sql.append("\n");
			sql.append(",").append(entityMetaData.getVersionColumnField().getColumnName());
		}
		sql.append("\n");
		sql.append(" )");
		sql.append("\n");
		sql.append(" values ( ");
		sql.append("\n");
		sql.append(assistant.insertSelectiveValues(entityMetaData, keyGenerated));
		if (entityMetaData.getVersionColumnField() != null) {
			sql.append("\n");
			sql.append(",0");
		}
		sql.append("\n");
		sql.append(" )");
		return sql.toString();
	}

	public static String buildInsert(EntityMetaData entityMetaData, boolean keyGenerated) {
		BuilderAssistant assistant = BuilderAssistant.instance();
		String tableName = entityMetaData.getTableName();

		StringBuilder sql = new StringBuilder();
		sql.append("insert into ").append(tableName);
		sql.append("\n");
		sql.append(" ( ");
		sql.append("\n");
		sql.append(assistant.insertColumns(entityMetaData, keyGenerated));
		if (entityMetaData.getVersionColumnField() != null) {
			sql.append("\n");
			sql.append(",").append(entityMetaData.getVersionColumnField().getColumnName());
		}
		sql.append("\n");
		sql.append(" )");
		sql.append(" values ( ");
		sql.append("\n");
		sql.append(assistant.insertValues(entityMetaData, keyGenerated));
		if (entityMetaData.getVersionColumnField() != null) {
			sql.append("\n");
			sql.append(",0");
		}
		sql.append("\n");
		sql.append(" )");
		return sql.toString();
	}

	public static String buildInsertBatch(EntityMetaData entityMetaData, boolean keyGenerated) {
		BuilderAssistant assistant = BuilderAssistant.instance();
		String tableName = entityMetaData.getTableName();

		StringBuilder sql = new StringBuilder();
		sql.append("insert into ").append(tableName);
		sql.append("\n");
		sql.append(" ( ");
		sql.append("\n");
		sql.append(assistant.insertColumns(entityMetaData, keyGenerated));
		if (entityMetaData.getVersionColumnField() != null) {
			sql.append("\n");
			sql.append(",").append(entityMetaData.getVersionColumnField().getColumnName());
		}
		sql.append("\n");
		sql.append(" )");
		sql.append(" values ");
		sql.append("\n");
		sql.append(assistant.insertBatchValues(entityMetaData, keyGenerated));
		if (entityMetaData.getVersionColumnField() != null) {
			sql.append("\n");
			sql.append(",0");
		}
		sql.append("\n");
		sql.append("");
		return sql.toString();
	}


}
