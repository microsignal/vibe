package io.microvibe.booster.core.base.mybatis.builder;

import io.microvibe.booster.commons.utils.StringUtils;
import io.microvibe.booster.core.base.entity.NullUpdateable;
import io.microvibe.booster.core.base.mybatis.MybatisConstants;
import io.microvibe.booster.core.base.persistence.EntityMetaData;
import io.microvibe.booster.core.base.persistence.FieldMetaData;
import io.microvibe.booster.core.base.persistence.JoinMetaData;
import io.microvibe.booster.core.base.utils.NameCastor;
import io.microvibe.booster.core.search.ISymbol;
import io.microvibe.booster.core.search.SearchKey;
import io.microvibe.booster.core.search.Searches;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class BuilderAssistant {

	private static BuilderAssistant instance = new BuilderAssistant();

	private BuilderAssistant() {
	}

	public static BuilderAssistant instance() {
		return instance;
	}

	private String getEmptyTesting(Class<?> javaType, String javaProperty) {
		return String.format("@%s@isNotEmpty(%s)", Expr.NAME, "isNotEmpty", javaProperty);
		/*if (String.class == javaType) {
			 return String.format("%s != null and %s != ''", javaProperty, javaProperty);
		} else {
			return String.format("%s != null", javaProperty);
		}*/
	}

	/**
	 * 装配sql中动态参数的占位符 #{paramterName,jdbcType=,typeHandler=}
	 */
	public final String resolveSqlParameter(FieldMetaData fieldMeta) {
		return resolveSqlParameter(fieldMeta, null, null);
	}

	/**
	 * 装配sql中动态参数的占位符 #{alias.searchKey,jdbcType=,typeHandler=}
	 */
	public final String resolveSqlParameter(FieldMetaData fieldMeta, String fieldKey, String beanAlias) {
		beanAlias = StringUtils.trimToNull(beanAlias);
		fieldKey = StringUtils.trimToNull(fieldKey);
		StringBuilder sql = new StringBuilder();
		sql.append("#{");
		if (beanAlias != null && !"".equals(beanAlias)) {
			sql.append(beanAlias).append(".");
		}
		if (fieldKey != null) {
			sql.append(fieldKey);
		} else {
			sql.append(fieldMeta.getJavaProperty());
		}
		// jdbcType
		if (fieldMeta.getJdbcTypeAlias() != null) {
			sql.append(", jdbcType=").append(fieldMeta.getJdbcTypeAlias());
		}
		// typeHandler
		if (fieldMeta.getTypeHandlerClass() != null) {
			sql.append(", typeHandler=").append(fieldMeta.getTypeHandlerClass().getName());
//			if(!BlankableEnumTypeHandler.class.isAssignableFrom(fieldMeta.getTypeHandlerClass())) {
			// javaType
			sql.append(", javaType=").append(fieldMeta.getJavaType().getName());
//			}
		}
		sql.append("}");
		return sql.toString();
	}

	/**
	 * 表字段列表
	 * <p>
	 * {@linkplain #columns(EntityMetaData, String, String, boolean)}
	 *
	 * @param entityMetaData    实体元数据
	 * @param tableAlias        别名
	 * @param propertyAliasable 是否使用列的别名
	 * @return
	 */
	public String columns(EntityMetaData entityMetaData, String tableAlias, boolean propertyAliasable) {
		return columns(entityMetaData, tableAlias, null, propertyAliasable);
	}

	/**
	 * 表字段列表
	 * <ul>
	 * <li>无前缀与别名: {column_name}</li>
	 * <li>有前缀无别名: {column_name} as {prefix}{column_name}</li>
	 * <li>有前缀有别名: {column_name} as {prefix}{JavaProperty}</li>
	 * <li>无前缀有别名: {column_name} as {javaProperty}</li>
	 * </ul>
	 *
	 * @param entityMetaData    实体元数据
	 * @param tableAlias        别名
	 * @param columnPrefix      列前缀
	 * @param propertyAliasable 是否使用类的字段别名
	 * @return
	 */
	public String columns(EntityMetaData entityMetaData, String tableAlias, String columnPrefix, boolean propertyAliasable) {
		return columns(entityMetaData, tableAlias, columnPrefix, propertyAliasable, true);
	}

	/**
	 * 表字段列表
	 * <ul>
	 * <li>无前缀与别名: {column_name}</li>
	 * <li>有前缀无别名: {column_name} as {prefix}{column_name}</li>
	 * <li>有前缀有别名: {column_name} as {prefix}{JavaProperty}</li>
	 * <li>无前缀有别名: {column_name} as {javaProperty}</li>
	 * </ul>
	 *
	 * @param entityMetaData    实体元数据
	 * @param tableAlias        别名
	 * @param columnPrefix      列前缀
	 * @param propertyAliasable 是否使用类的字段别名
	 * @param includeFormula    是否包含 formula 字段
	 * @return
	 */
	public String columns(EntityMetaData entityMetaData, String tableAlias, String columnPrefix, boolean propertyAliasable, boolean includeFormula) {
		StringBuilder sql = new StringBuilder();
		tableAlias = StringUtils.trimToNull(tableAlias);
		columnPrefix = StringUtils.trimToNull(columnPrefix);

		String tableName = entityMetaData.getTableName();

		List<FieldMetaData> allFields = entityMetaData.getAllColumnFields();
		Iterator<FieldMetaData> iter = allFields.iterator();
		if (iter.hasNext()) {
			FieldMetaData fieldMeta = iter.next();
			if (tableAlias != null) {
				sql.append(tableAlias).append(".");
			}
			appendFieldColumn(sql, fieldMeta, columnPrefix, propertyAliasable);
		}
		while (iter.hasNext()) {
			FieldMetaData fieldMeta = iter.next();
			sql.append(", ");
			if (tableAlias != null) {
				sql.append(tableAlias).append(".");
			}
			appendFieldColumn(sql, fieldMeta, columnPrefix, propertyAliasable);
		}
		if (includeFormula) {
			Iterator<FieldMetaData> formulaIter = entityMetaData.getFormulaFields().iterator();
			while (formulaIter.hasNext()) {
				FieldMetaData fieldMeta = formulaIter.next();
				if (sql.length() > 0) {
					sql.append(", ");
				}
				appendFormulaColumn(sql, tableName, tableAlias,
					fieldMeta, columnPrefix, propertyAliasable);
			}
		}
		return sql.toString();
	}

	/**
	 * 拼接表字段
	 * <ul>
	 * <li>无前缀与别名: {column_name}</li>
	 * <li>有前缀无别名: {column_name} as {prefix}{column_name}</li>
	 * <li>有前缀有别名: {column_name} as {prefix}{JavaProperty}</li>
	 * <li>无前缀有别名: {column_name} as {javaProperty}</li>
	 * </ul>
	 *
	 * @param sql
	 * @param fieldMeta         列元数据
	 * @param columnPrefix      列前缀
	 * @param propertyAliasable 是否使用类的字段别名
	 */
	private void appendFieldColumn(StringBuilder sql, FieldMetaData fieldMeta, String columnPrefix, boolean propertyAliasable) {
		sql.append(fieldMeta.getColumnName());
		if (propertyAliasable) {
			if (columnPrefix != null) {
				sql.append(" as \"").append(columnPrefix)
					.append(NameCastor.lowerCamelToUpperCamel(fieldMeta.getJavaProperty()))
					.append("\"");
			} else {
				sql.append(" as \"").append(fieldMeta.getJavaProperty()).append("\"");
			}
		} else if (columnPrefix != null) {
			sql.append(" as ").append(columnPrefix).append(fieldMeta.getColumnName());
		}
	}

	private void appendFormulaColumn(StringBuilder sql, String tableName, String tableAlias,
		FieldMetaData fieldMeta, String columnPrefix, boolean propertyAliasable) {
		sql.append(columnExpression(tableName, tableAlias, fieldMeta));
		sql.append(" as ");
		if (propertyAliasable) {
			if (columnPrefix != null) {
				sql.append("\"").append(columnPrefix)
					.append(NameCastor.lowerCamelToUpperCamel(fieldMeta.getJavaProperty()))
					.append("\"");
			} else {
				sql.append("\"").append(fieldMeta.getJavaProperty()).append("\"");
			}
		} else {
			if (columnPrefix != null) {
				sql.append(columnPrefix);
			}
			sql.append(fieldMeta.getColumnName());
		}
	}

	public String columnExpression(String tableAlias, FieldMetaData fieldMeta) {
		return columnExpression(fieldMeta.getEntity().getTableName(), tableAlias, fieldMeta);
	}

	public String columnExpression(String tableName, String tableAlias, FieldMetaData fieldMeta) {
		StringBuilder sql = new StringBuilder();
		if (fieldMeta.isFormulable()) {
			String formula = fieldMeta.getFormula();
			try {
				Statement statement = CCJSqlParserUtil.parse(formula);
				SelectBody selectBody = ((Select) (statement)).getSelectBody();
				if (selectBody instanceof PlainSelect) {
					Expression where = ((PlainSelect) selectBody).getWhere();
					where.accept(new ExpressionVisitorAdapter() {
						@Override
						public void visit(Column column) {
							Table table = column.getTable();
							if (table == null || StringUtils.isBlank(table.getName())) {
								Table t = new Table(tableName);
								if (tableAlias != null) {
									t.setAlias(new Alias(tableAlias, false));
								}
								column.setTable(t);
							}
						}
					});
				}
				if (!formula.startsWith("(") && !formula.endsWith(")")) {
					sql.append("(").append(statement.toString()).append(")");
				} else {
					sql.append(statement.toString());
				}
			} catch (JSQLParserException e) {
				throw new IllegalArgumentException();
			}
		} else {
			if (tableAlias != null) {
				sql.append(tableAlias).append(".");
			}
			sql.append(fieldMeta.getColumnName());
		}
		return sql.toString();
	}

	public String joinColumns(EntityMetaData entityMetaData, boolean propertyAliasable) {
		return joinColumns(entityMetaData.getJoinFields(), propertyAliasable, true);
	}

	public String joinColumns(EntityMetaData entityMetaData, boolean propertyAliasable, boolean includeFormula) {
		return joinColumns(entityMetaData.getJoinFields(), propertyAliasable, includeFormula);
	}

	public String joinColumns(Collection<JoinMetaData> joinFields, boolean propertyAliasable) {
		return joinColumns(joinFields, propertyAliasable, true);
	}

	public String joinColumns(Collection<JoinMetaData> joinFields, boolean propertyAliasable, boolean includeFormula) {
		StringBuilder sql = new StringBuilder();
		if (joinFields != null && joinFields.size() > 0) {
			for (JoinMetaData joinField : joinFields) {
				if (sql.length() > 0) {
					sql.append(",\n");
				}
				sql.append(columns(joinField.getTableMetaData(), joinField.getTableAlias(),
					joinField.getColumnPrefix(), propertyAliasable, includeFormula));
			}
		}
		return sql.toString();
	}

	public String columns(EntityMetaData entityMetaData, String tableAlias, boolean propertyAliasable,
		boolean addDefaultJoins, Collection<JoinMetaData> additionalJoins) {
		StringBuilder sql = new StringBuilder();
		sql.append(columns(entityMetaData, tableAlias, propertyAliasable));
		if (addDefaultJoins) {
			String joinColumns = joinColumns(entityMetaData, propertyAliasable);
			if (joinColumns.length() > 0) {
				sql.append("\n,");
				sql.append(joinColumns);
			}
		}
		if (additionalJoins != null) {
			String joinColumns = joinColumns(additionalJoins, propertyAliasable);
			if (joinColumns.length() > 0) {
				sql.append("\n,");
				sql.append(joinColumns);
			}
		}
		return sql.toString();
	}

	/**
	 * 表名
	 * <ul>
	 * <li>{table_name}</li>
	 * <li>{table_name}&emsp;{tableAlias}</li>
	 * </ul>
	 *
	 * @param entityMetaData 元数据
	 * @param tableAlias     别名
	 * @return
	 */
	public String table(EntityMetaData entityMetaData, String tableAlias) {
		StringBuilder sql = new StringBuilder();
		tableAlias = StringUtils.trimToNull(tableAlias);
		sql.append(" ").append(entityMetaData.getTableName());
		if (tableAlias != null) {
			sql.append(" ").append(tableAlias);
		}
		return sql.toString();
	}

	public String joinTables(EntityMetaData entityMetaData, String tableAlias) {
		tableAlias = StringUtils.trimToNull(tableAlias);
		final String mainAlias;
		if (tableAlias == null) {
			mainAlias = entityMetaData.getTableName();
		} else {
			mainAlias = tableAlias;
		}

		List<JoinMetaData> joinFields = entityMetaData.getJoinFields();
		return joinTables(joinFields, mainAlias);
	}

	public String joinTables(Collection<JoinMetaData> joinFields, String mainAlias) {
		StringBuilder sql = new StringBuilder();
		if (joinFields != null && joinFields.size() > 0) {
			for (JoinMetaData joinField : joinFields) {
				if (sql.length() > 0) {
					sql.append("\n");
				}
				sql.append(joinField.getJoinType().name()).append(" join ");
				sql.append(table(joinField.getTableMetaData(), joinField.getTableAlias()));
				sql.append(" on ");
				String on = joinField.getOn();
				try {
					Expression onCond = CCJSqlParserUtil.parseCondExpression(on);
					onCond.accept(new ExpressionVisitorAdapter() {
						@Override
						public void visit(Column column) {
							net.sf.jsqlparser.schema.Table table = column.getTable();
							if (table == null) {
								table = new net.sf.jsqlparser.schema.Table(mainAlias);
								column.setTable(table);
							} else {
								if (StringUtils.isBlank(table.getName())) {
									table.setName(mainAlias);
								}
							}
						}
					});
					on = onCond.toString();
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
				sql.append(on);
			}
		}
		return sql.toString();
	}

	public String table(EntityMetaData entityMetaData, String tableAlias, boolean addDefaultJoins,
		Collection<JoinMetaData> additionalJoins) {
		StringBuilder sql = new StringBuilder();

		tableAlias = StringUtils.trimToNull(tableAlias);
		final String mainAlias;
		if (tableAlias == null) {
			mainAlias = entityMetaData.getTableName();
		} else {
			mainAlias = tableAlias;
		}

		sql.append(table(entityMetaData, tableAlias));
		if (addDefaultJoins) {
			sql.append("\n");
			sql.append(joinTables(entityMetaData, mainAlias));
		}
		if (additionalJoins != null) {
			sql.append("\n");
			sql.append(joinTables(additionalJoins, mainAlias));
		}
		return sql.toString();
	}

	/**
	 * <ul>
	 * <li>id = #{id}</li>
	 * <li>id1 = #{id1} and id2 = #{id2} and ...</li>
	 * </ul>
	 *
	 * @param entityMetaData 元数据
	 * @param tableAlias     别名
	 * @return
	 */
	public String whereById(EntityMetaData entityMetaData, String tableAlias) {
		List<FieldMetaData> primaryFields = entityMetaData.getPkColumnFields();
		Iterator<FieldMetaData> priIter = primaryFields.iterator();
		StringBuilder sql = new StringBuilder();
		tableAlias = StringUtils.trimToNull(tableAlias);
		if (primaryFields.size() > 0) {
			FieldMetaData fieldMeta = priIter.next();
			if (tableAlias != null) {
				sql.append(tableAlias).append(".");
			}
			sql.append(fieldMeta.getColumnName());
			if (priIter.hasNext()) {
				sql.append(" = ").append(resolveSqlParameter(fieldMeta));
			} else {
				sql.append(" = #{").append(MybatisConstants.PARAM_ID).append("}");
				sql.append("\n");
			}
			while (priIter.hasNext()) {
				fieldMeta = priIter.next();
				sql.append(" and ");
				if (tableAlias != null) {
					sql.append(tableAlias).append(".");
				}
				sql.append(fieldMeta.getColumnName());
				sql.append(" = ").append(resolveSqlParameter(fieldMeta));
				sql.append("\n");
			}
		}
		return sql.toString();
	}

	/**
	 * <ul>
	 * <li>and version_column = #{versionColumnField}</li>
	 * </ul>
	 *
	 * @param entityMetaData
	 * @param tableAlias
	 * @return
	 */
	public String whereByVersion(EntityMetaData entityMetaData, String tableAlias) {
		return whereByVersion(entityMetaData, tableAlias, " and ");
	}

	/**
	 * <ul>
	 * <li>and version_column = #{versionColumnField}</li>
	 * </ul>
	 *
	 * @param entityMetaData
	 * @param tableAlias
	 * @param conj
	 * @return
	 */
	public String whereByVersion(EntityMetaData entityMetaData, String tableAlias, String conj) {
		return whereByVersion(entityMetaData, tableAlias, conj, true);
	}

	/**
	 * <ul>
	 * <li>and version_column = #{versionColumnField}</li>
	 * </ul>
	 *
	 * @param entityMetaData
	 * @param tableAlias
	 * @param conj
	 * @return
	 */
	public String whereByVersion(EntityMetaData entityMetaData, String tableAlias, String conj, boolean required) {
		FieldMetaData versionField = entityMetaData.getVersionColumnField();
		if (versionField == null) {
			return "";
		}
		StringBuilder sql = new StringBuilder();
		if (!required) {
			// sql.append(String.format("<if test=\"%s\">", getEmptyTesting(versionField.getJavaType(), versionField.getJavaProperty())));
			sql.append("<if test=\"@").append(Expr.NAME).append("@isNotEmpty(").append(versionField.getJavaProperty()).append(")\">");
			sql.append("\n");
		}
		sql.append(conj);
		if (tableAlias != null) {
			sql.append(tableAlias).append(".");
		}
		sql.append(versionField.getColumnName());
		sql.append(" = ").append(resolveSqlParameter(versionField));
		if (!required) {
			sql.append("\n");
			sql.append("</if>");
		}
		return sql.toString();
	}

	/**
	 * <code>
	 * &lt;if test="property != null"><br>
	 * and [tableAlias.]column_name symbol [#{property}]<br>
	 * &lt;/if><br>
	 * </code>
	 *
	 * @param fieldMeta  元数据
	 * @param property   属性
	 * @param symbol     操作符
	 * @param tableAlias 别名
	 * @return
	 */
	public String symbolCondition(FieldMetaData fieldMeta, String property, ISymbol symbol, String tableAlias) {
		property = StringUtils.trimToNull(property);
		tableAlias = StringUtils.trimToNull(tableAlias);
		if (property == null) {
			property = fieldMeta.getJavaProperty();
		}
		property = Searches.toSearchKeyString(property, symbol);

		StringBuilder sql = new StringBuilder();
		// sql.append(String.format("<if test=\"%s\">", getEmptyTesting(fieldMeta.getJavaType(), property)));
		sql.append("<if test=\"@").append(Expr.NAME).append("@isNotEmpty(").append(property).append(")\">");
		sql.append("\n");
		appendSymbolCondition(sql, fieldMeta, property, symbol, tableAlias);
		sql.append("\n");
		sql.append("</if>");
		return sql.toString();
	}

	private void appendSymbolCondition(StringBuilder sql, FieldMetaData fieldMeta, String property, ISymbol symbol, String tableAlias) {
		sql.append("and ");
		sql.append(columnExpression(fieldMeta.getEntity().getTableName(), tableAlias, fieldMeta)).append(" ");
		if (symbol.isSingleValue()) {
			sql.append("<![CDATA[ ");
			sql.append(symbol.symbol()).append(" ");
			sql.append(resolveSqlParameter(fieldMeta, property, null));
			sql.append(" ]]>");
		} else if (symbol.isDoubleValue()) { // between
			sql.append("<![CDATA[ ");
			sql.append(symbol.symbol()).append(" ");
			sql.append(resolveSqlParameter(fieldMeta, property + "[0]", null));
			sql.append(" and ");
			sql.append(resolveSqlParameter(fieldMeta, property + "[1]", null));
			sql.append(" ]]>");
		} else if (symbol.isListValue()) {
			sql.append("<![CDATA[ ");
			sql.append(symbol.symbol()).append(" ");
			sql.append(" ]]>");
			sql.append("\n");
			sql.append("<foreach item=\"item\" index=\"itemIndex\" collection=\"")
				.append(property).append("\" open=\"(\" separator=\",\" close=\")\">");
			sql.append("\n");
			sql.append(resolveSqlParameter(fieldMeta, "item", null));
			sql.append("\n");
			sql.append("</foreach>");
		} else if (symbol.isNoValue()) {
			sql.append("<![CDATA[ ");
			sql.append(symbol.symbol()).append(" ");
			sql.append(" ]]>");
		} else {
			// 不存在的
		}
	}

	public String wrapByTrim(String str, String prefix, String prefixOverrides) {
		StringBuilder sql = new StringBuilder();
		sql.append("<trim prefix=\"")
			.append(StringUtils.trimToEmpty(prefix))
			.append("\" suffix=\"\" suffixOverrides=\"\" prefixOverrides=\"")
			.append(StringUtils.trimToEmpty(prefixOverrides))
			.append("\" > ")
			.append(str)
			.append("</trim>");
		return sql.toString();
	}

	public String wrapByTrim(String str, String prefixOverrides) {
		return wrapByTrim(str, null, prefixOverrides);
	}

	public String wrapByTrim(String str) {
		return wrapByTrim(str, "and", "and");
	}

	public String whereByMap(EntityMetaData entityMetaData, String tableAlias) {
		return whereByMap(entityMetaData, tableAlias, null);
	}

	public String whereByMap(EntityMetaData entityMetaData, String tableAlias, String searchKeyPrefix) {
		return whereByMap(entityMetaData, tableAlias, searchKeyPrefix, null);
	}

	/**
	 * <code>
	 * &lt;trim prefixOverrides="and"><br>
	 * ...<br>
	 * &lt;if test="container.property != null"><br>
	 * and [tableAlias.]column_name symbol [#{container.(searchKeyPrefix + property)}]<br>
	 * &lt;/if><br>
	 * ...<br>
	 * &lt;/trim><br>
	 * </code>
	 *
	 * @param entityMetaData
	 * @param tableAlias
	 * @param searchKeyPrefix 查询条件前缀
	 * @param container
	 * @return
	 */
	public String whereByMap(EntityMetaData entityMetaData, String tableAlias, String searchKeyPrefix, String container) {
		//List<FieldMetaData> allColumnFields = entityMetaData.getAllColumnFields();
		List<FieldMetaData> allFields = new ArrayList<>();
		allFields.addAll(entityMetaData.getPkColumnFields());
		allFields.addAll(entityMetaData.getNormalColumnFields());
		allFields.addAll(entityMetaData.getFormulaFields());

		String tableName = entityMetaData.getTableName();
		tableAlias = StringUtils.trimToNull(tableAlias);
		searchKeyPrefix = StringUtils.trimToNull(searchKeyPrefix);

		String containerPrefix = StringUtils.isBlank(container) ? "" : container + ".";

		StringBuilder sql = new StringBuilder();

		sql.append("<trim prefix=\"\" suffix=\"\" suffixOverrides=\"\" prefixOverrides=\"and\" > ");
		sql.append("\n");

		for (FieldMetaData fieldMeta : allFields) {
			if (StringUtils.isNotBlank(container)) {
				sql.append("<if test=\"@").append(Expr.NAME).append("@isNotEmpty(")
					.append(container).append(")\">");
				sql.append("\n");
			}

			// sql.append(String.format("<if test=\"%s\">", getEmptyTesting(fieldMeta.getJavaType(), fieldMeta.getJavaProperty())));
			sql.append("<if test=\"@").append(Expr.NAME).append("@isNotEmpty(")
				.append(containerPrefix).append(fieldMeta.getJavaProperty()).append(")\">");
			sql.append("\n");
			sql.append("and ");

			sql.append(columnExpression(fieldMeta.getEntity().getTableName(), tableAlias, fieldMeta)).append(" = ");

			String searchKey = fieldMeta.getJavaProperty();
			if (searchKeyPrefix != null) {
				searchKey = searchKeyPrefix + NameCastor.lowerCamelToUpperCamel(searchKey);
			}
			searchKey = containerPrefix + searchKey;
			sql.append(resolveSqlParameter(fieldMeta, searchKey, null));
			sql.append("\n");
			sql.append("</if>");
			sql.append("\n");

			for (ISymbol symbol : Searches.symbols()) {
				if (symbol.symbol() != null) {
					sql.append(symbolCondition(fieldMeta, searchKey, symbol, tableAlias));
				}
				sql.append("\n");
			}

			if (StringUtils.isNotBlank(container)) {
				sql.append("</if>");
				sql.append("\n");
			}
		}
		sql.append("</trim>");

		return sql.toString();
	}

	public String whereByJoinMap(EntityMetaData entityMetaData) {
		List<JoinMetaData> joinFields = entityMetaData.getJoinFields();
		return whereByJoinMap(joinFields);
	}

	public String whereByJoinMap(Collection<JoinMetaData> joinFields) {
		StringBuilder sql = new StringBuilder();
		if (joinFields != null && joinFields.size() > 0) {
			for (JoinMetaData joinField : joinFields) {
				if (sql.length() > 0) {
					sql.append("\n");
				}
				sql.append(wrapByTrim(whereByMap(joinField.getTableMetaData(), joinField.getTableAlias(), null,
					joinField.getProperty())));
			}
		}
		return wrapByTrim(sql.toString());
	}

	/**
	 * <code>
	 * &lt;trim prefixOverrides="and"><br>
	 * ...<br>
	 * &lt;if test="property != null"><br>
	 * and [tableAlias.]column_name symbol [#{property}]<br>
	 * &lt;/if><br>
	 * ...<br>
	 * &lt;/trim><br>
	 * </code>
	 *
	 * @param entityMetaData
	 * @param tableAlias
	 * @return
	 */
	public String whereByEntity(EntityMetaData entityMetaData, String tableAlias) {
		return whereByEntity(entityMetaData, tableAlias, null);
	}

	/**
	 * <code>
	 * &lt;trim prefixOverrides="and"><br>
	 * ...<br>
	 * &lt;if test="container.property != null"><br>
	 * and [tableAlias.]column_name symbol [#{container.property}]<br>
	 * &lt;/if><br>
	 * ...<br>
	 * &lt;/trim><br>
	 * </code>
	 *
	 * @param entityMetaData
	 * @param tableAlias
	 * @param container
	 * @return
	 */
	public String whereByEntity(EntityMetaData entityMetaData, String tableAlias, String container) {
		//List<FieldMetaData> allColumnFields = entityMetaData.getAllColumnFields();
		List<FieldMetaData> allFields = new ArrayList<>();
		allFields.addAll(entityMetaData.getPkColumnFields());
		allFields.addAll(entityMetaData.getNormalColumnFields());
		allFields.addAll(entityMetaData.getFormulaFields());

		String tableName = entityMetaData.getTableName();
		tableAlias = StringUtils.trimToNull(tableAlias);
		String containerPrefix = StringUtils.isBlank(container) ? "" : container + ".";

		StringBuilder sql = new StringBuilder();

		sql.append("<trim prefix=\"\" suffix=\"\" suffixOverrides=\"\" prefixOverrides=\"and\" > ");
		sql.append("\n");

		for (FieldMetaData fieldMeta : allFields) {
			if (StringUtils.isNotBlank(container)) {
				sql.append("<if test=\"@").append(Expr.NAME).append("@isNotEmpty(")
					.append(container).append(")\">");
				sql.append("\n");
			}

			sql.append("<if test=\"@").append(Expr.NAME).append("@isNotEmpty(")
				.append(containerPrefix).append(fieldMeta.getJavaProperty()).append(")\">");
			sql.append("\n");
			sql.append("and ");

			sql.append(columnExpression(fieldMeta.getEntity().getTableName(), tableAlias, fieldMeta)).append(" = ");

			String searchKey = containerPrefix + fieldMeta.getJavaProperty();
			sql.append(resolveSqlParameter(fieldMeta, searchKey, null));
			sql.append("\n");
			sql.append("</if>");
			sql.append("\n");

			if (StringUtils.isNotBlank(container)) {
				sql.append("</if>");
				sql.append("\n");
			}
		}

		Collection<FieldMetaData> transientFields = entityMetaData.getTransientFields().values();
		for (FieldMetaData transientField : transientFields) {
			if (StringUtils.isNotBlank(container)) {
				sql.append("<if test=\"@").append(Expr.NAME).append("@isNotEmpty(")
					.append(container).append(")\">");
				sql.append("\n");
			}
			String javaProperty = transientField.getJavaProperty();
			sql.append("<if test=\"@").append(Expr.NAME).append("@isNotEmpty(")
				.append(containerPrefix).append(javaProperty).append(")\">");
			sql.append("\n");
			SearchKey searchKey = Searches.parseSearchKey(javaProperty);
			ISymbol symbol = searchKey.getSymbol();
			appendSymbolCondition(sql, transientField,
				containerPrefix + javaProperty, symbol, tableAlias);
			sql.append("\n");
			sql.append("</if>");
			sql.append("\n");
			if (StringUtils.isNotBlank(container)) {
				sql.append("</if>");
				sql.append("\n");
			}
		}

		sql.append("</trim>");

		return sql.toString();
	}

	public String whereByJoinEntity(EntityMetaData entityMetaData) {
		List<JoinMetaData> joinFields = entityMetaData.getJoinFields();
		return whereByJoinEntity(joinFields);
	}

	public String whereByJoinEntity(Collection<JoinMetaData> joinFields) {
		StringBuilder sql = new StringBuilder();
		if (joinFields != null && joinFields.size() > 0) {
			for (JoinMetaData joinField : joinFields) {
				if (sql.length() > 0) {
					sql.append("\n");
				}
				sql.append(wrapByTrim(whereByEntity(joinField.getTableMetaData(), joinField.getTableAlias(),
					joinField.getProperty())));
			}
		}
		return wrapByTrim(sql.toString());
	}

	/**
	 * <code>
	 * column_name = #{property},<br>
	 * column_name = #{property},<br>
	 * ...<br>
	 * </code>
	 *
	 * @param entityMetaData
	 * @param tableAlias
	 * @return
	 */
	public String updateSets(EntityMetaData entityMetaData, String tableAlias) {
		List<FieldMetaData> primaryFields = entityMetaData.getPkColumnFields();
		List<FieldMetaData> normalFields = entityMetaData.getNormalColumnFields();
		String tableName = entityMetaData.getTableName();

		StringBuilder sql = new StringBuilder();
		tableAlias = StringUtils.trimToNull(tableAlias);

		Iterator<FieldMetaData> norIter = normalFields.iterator();
		FieldMetaData fieldMeta = norIter.next();
		sql.append(String.format("%s = %s", fieldMeta.getColumnName(), resolveSqlParameter(fieldMeta)));
		while (norIter.hasNext()) {
			fieldMeta = norIter.next();
			if (!fieldMeta.isUpdatable()) {
				continue;
			}
			sql.append(String.format(",\n%s = %s", fieldMeta.getColumnName(), resolveSqlParameter(fieldMeta)));
		}
		return sql.toString();
	}

	/**
	 * <code>
	 * &lt;trim suffixOverrides=","><br>
	 * ...<br>
	 * &lt;if test="property != null"><br>
	 * column_name = #{property},<br>
	 * &lt;/if><br>
	 * ...<br>
	 * &lt;/trim>
	 * </code>
	 *
	 * @param entityMetaData
	 * @param tableAlias
	 * @return
	 */
	public String updateSelectiveSets(EntityMetaData entityMetaData, String tableAlias) {
		List<FieldMetaData> primaryFields = entityMetaData.getPkColumnFields();
		List<FieldMetaData> normalFields = entityMetaData.getNormalColumnFields();

		StringBuilder sql = new StringBuilder();
		tableAlias = StringUtils.trimToNull(tableAlias);

		sql.append("<trim prefix=\"\" suffix=\"\" suffixOverrides=\",\" > ");
		sql.append("\n");
		/*
		for (FieldMetaData fieldMeta : pkColumnFields) {
			sql.append(String.format("<if test=\"%s\">", getEmptyTesting(fieldMeta)));
			sql.append(String.format("%s = %s,", fieldMeta.getColumnName(), resolveSqlParameter(fieldMeta)));
			sql.append("</if>");
		}
		*/
		for (FieldMetaData fieldMeta : normalFields) {
			if (!fieldMeta.isUpdatable()) {
				continue;
			}
			sql.append(String.format("<if test=\"%s != null\">", fieldMeta.getJavaProperty()));
			sql.append("\n");
			sql.append(String.format("%s = %s,", fieldMeta.getColumnName(), resolveSqlParameter(fieldMeta)));
			sql.append("\n");
			sql.append("</if>");
			sql.append("\n");
			if (NullUpdateable.class.isAssignableFrom(entityMetaData.getEntityClass())) {
				sql.append(String.format("<if test=\"updatingNullFields != null and updatingNullFields.contains('%s')\">", fieldMeta.getJavaProperty()));
				sql.append("\n");
				sql.append(String.format("%s = null,", fieldMeta.getColumnName()));
				sql.append("\n");
				sql.append("</if>");
				sql.append("\n");
			}
		}
		sql.append("</trim>");
		return sql.toString();
	}

	/**
	 * {@linkplain #insertSelectiveColumns(EntityMetaData, boolean)}
	 *
	 * @param entityMetaData
	 * @return
	 */
	public String insertSelectiveColumns(EntityMetaData entityMetaData) {
		return insertSelectiveColumns(entityMetaData, false);
	}

	/**
	 * <code>
	 * &lt;trim suffixOverrides=","><br>
	 * ...<br>
	 * &lt;if test="property != null"><br>
	 * column_name,<br>
	 * &lt;/if><br>
	 * ...<br>
	 * &lt;/trim>
	 * </code>
	 *
	 * @param entityMetaData
	 * @param keyGenerated   主键是否为自增列等自动赋值机制, 是则不插入主键值
	 * @return
	 */
	public String insertSelectiveColumns(EntityMetaData entityMetaData, boolean keyGenerated) {
		List<FieldMetaData> primaryFields = entityMetaData.getPkColumnFields();
		List<FieldMetaData> normalFields = entityMetaData.getNormalColumnFields();

		StringBuilder sql = new StringBuilder();

		sql.append("<trim prefix=\"\" suffix=\"\" suffixOverrides=\",\" > ");
		sql.append("\n");
		if (!keyGenerated) {
			for (FieldMetaData fieldMeta : primaryFields) {
				sql.append(String.format("<if test=\"%s != null\">", fieldMeta.getJavaProperty()));
				sql.append(fieldMeta.getColumnName()).append(",");
				sql.append("</if>");
				sql.append("\n");
			}
		}
		for (FieldMetaData fieldMeta : normalFields) {
			if (!fieldMeta.isInsertable()) {
				continue;
			}
			sql.append(String.format("<if test=\"%s != null\">", fieldMeta.getJavaProperty()));
			sql.append(fieldMeta.getColumnName()).append(",");
			sql.append("</if>");
			sql.append("\n");
		}
		sql.append("\n");
		sql.append("</trim>");
		return sql.toString();
	}

	/**
	 * {@linkplain #insertColumns(EntityMetaData, boolean)}
	 *
	 * @param entityMetaData
	 * @return
	 */
	public String insertColumns(EntityMetaData entityMetaData) {
		return insertColumns(entityMetaData, false);
	}

	/**
	 * <code>
	 * ...<br>
	 * column_name,<br>
	 * column_name,<br>
	 * column_name,<br>
	 * ...<br>
	 * </code>
	 *
	 * @param entityMetaData
	 * @param keyGenerated   主键是否为自增列等自动赋值机制, 是则不插入主键值
	 * @return
	 */
	public String insertColumns(EntityMetaData entityMetaData, boolean keyGenerated) {
		List<FieldMetaData> primaryFields = entityMetaData.getPkColumnFields();
		List<FieldMetaData> normalFields = entityMetaData.getNormalColumnFields();

		StringBuilder sql = new StringBuilder();

		if (!keyGenerated) {
			Iterator<FieldMetaData> priIter = primaryFields.iterator();
			FieldMetaData fieldMeta = priIter.next();
			sql.append(fieldMeta.getColumnName());
			while (priIter.hasNext()) {
				fieldMeta = priIter.next();
				sql.append(",").append(fieldMeta.getColumnName());
			}
		}
		Iterator<FieldMetaData> norIter = normalFields.iterator();
		if (sql.length() == 0) {
			while (norIter.hasNext()) {
				FieldMetaData fieldMeta = norIter.next();
				if (!fieldMeta.isInsertable()) {
					continue;
				}
				sql.append(fieldMeta.getColumnName());
				break;
			}
		}
		while (norIter.hasNext()) {
			FieldMetaData fieldMeta = norIter.next();
			if (!fieldMeta.isInsertable()) {
				continue;
			}
			sql.append(",").append(fieldMeta.getColumnName());
		}
		return sql.toString();
	}

	/**
	 * {@linkplain #insertSelectiveColumns(EntityMetaData, boolean)}
	 *
	 * @param entityMetaData
	 * @return
	 */
	public String insertSelectiveValues(EntityMetaData entityMetaData) {
		return insertSelectiveValues(entityMetaData, false);
	}

	/**
	 * <code>
	 * &lt;trim suffixOverrides=","><br>
	 * ...<br>
	 * &lt;if test="property != null"><br>
	 * #{property},<br>
	 * &lt;/if><br>
	 * ...<br>
	 * &lt;/trim>
	 * </code>
	 *
	 * @param entityMetaData
	 * @param keyGenerated   主键是否为自增列等自动赋值机制, 是则不插入主键值
	 * @return
	 */
	public String insertSelectiveValues(EntityMetaData entityMetaData, boolean keyGenerated) {
		List<FieldMetaData> primaryFields = entityMetaData.getPkColumnFields();
		List<FieldMetaData> normalFields = entityMetaData.getNormalColumnFields();

		StringBuilder sql = new StringBuilder();

		sql.append("<trim prefix=\"\" suffix=\"\" suffixOverrides=\",\" > ");
		sql.append("\n");
		if (!keyGenerated) {
			for (FieldMetaData fieldMeta : primaryFields) {
				sql.append(String.format("<if test=\"%s != null\">", fieldMeta.getJavaProperty()));
				sql.append(resolveSqlParameter(fieldMeta)).append(",");
				sql.append("</if>");
				sql.append("\n");
			}
		}
		for (FieldMetaData fieldMeta : normalFields) {
			if (!fieldMeta.isInsertable()) {
				continue;
			}
			sql.append(String.format("<if test=\"%s != null\">", fieldMeta.getJavaProperty()));
			sql.append(resolveSqlParameter(fieldMeta)).append(",");
			sql.append("</if>");
			sql.append("\n");
		}
		sql.append("\n");
		sql.append("</trim>");
		return sql.toString();
	}

	/**
	 * {@linkplain #insertValues(EntityMetaData, boolean)}
	 *
	 * @param entityMetaData
	 * @return
	 */
	public String insertValues(EntityMetaData entityMetaData) {
		return insertValues(entityMetaData, false);
	}

	/**
	 * <code>
	 * ...<br>
	 * #{property},<br>
	 * #{property},<br>
	 * #{property},<br>
	 * ...<br>
	 * </code>
	 *
	 * @param entityMetaData
	 * @param keyGenerated   主键是否为自增列等自动赋值机制, 是则不插入主键值
	 * @return
	 */
	public String insertValues(EntityMetaData entityMetaData, boolean keyGenerated) {
		List<FieldMetaData> primaryFields = entityMetaData.getPkColumnFields();
		List<FieldMetaData> normalFields = entityMetaData.getNormalColumnFields();

		StringBuilder sql = new StringBuilder();
		/*
		sql.append("<trim prefix=\"\" suffix=\"\" suffixOverrides=\",\" > ");
		if (!keyGenerated) {
			for (FieldMetaData fieldMeta : pkColumnFields) {
				sql.append(resolveSqlParameter(fieldMeta)).append(",");
			}
		}
		for (FieldMetaData fieldMeta : normalColumnFields) {
			sql.append(resolveSqlParameter(fieldMeta)).append(",");
		}
		sql.append("</trim>");
		*/

		if (!keyGenerated) {
			Iterator<FieldMetaData> priIter = primaryFields.iterator();
			FieldMetaData fieldMeta = priIter.next();
			sql.append(resolveSqlParameter(fieldMeta));
			while (priIter.hasNext()) {
				fieldMeta = priIter.next();
				sql.append(",").append(resolveSqlParameter(fieldMeta));
			}
		}
		Iterator<FieldMetaData> norIter = normalFields.iterator();
		if (sql.length() == 0) {
			while (norIter.hasNext()) {
				FieldMetaData fieldMeta = norIter.next();
				if (!fieldMeta.isInsertable()) {
					continue;
				}
				sql.append(resolveSqlParameter(fieldMeta));
				break;
			}
		}
		while (norIter.hasNext()) {
			FieldMetaData fieldMeta = norIter.next();
			if (!fieldMeta.isInsertable()) {
				continue;
			}
			sql.append(",").append(resolveSqlParameter(fieldMeta));
		}

		return sql.toString();
	}

	/**
	 * <code>
	 * &lt;foreach item="rowData" index="rowIndex" collection="list" separator=","><br>
	 * &lt;trim suffixOverrides=","><br>
	 * ...<br>
	 * #{rowData.property},<br>
	 * ...<br>
	 * &lt;/trim><br>
	 * &lt;/foreach><br>
	 * </code>
	 *
	 * @param entityMetaData
	 * @param keyGenerated   主键是否为自增列等自动赋值机制, 是则不插入主键值
	 * @return
	 */
	public String insertBatchValues(EntityMetaData entityMetaData, boolean keyGenerated) {
		List<FieldMetaData> primaryFields = entityMetaData.getPkColumnFields();
		List<FieldMetaData> normalFields = entityMetaData.getNormalColumnFields();

		StringBuilder sql = new StringBuilder();

		sql.append("<foreach item=\"rowData\" index=\"rowIndex\" collection=\"list\" separator=\",\">");
		sql.append("\n");
		sql.append("(");
		sql.append("\n");
		sql.append("<trim prefix=\"\" suffix=\"\" suffixOverrides=\",\" > ");
		sql.append("\n");
		if (!keyGenerated) {
			for (FieldMetaData fieldMeta : primaryFields) {
				sql.append(resolveSqlParameter(fieldMeta, null, "rowData")).append(",");
			}
		}
		for (FieldMetaData fieldMeta : normalFields) {
			if (!fieldMeta.isInsertable()) {
				continue;
			}
			sql.append(resolveSqlParameter(fieldMeta, null, "rowData")).append(",");
		}
		sql.append("\n");
		sql.append("</trim>");
		sql.append("\n");
		sql.append(")");
		sql.append("\n");
		sql.append("</foreach>");
		return sql.toString();
	}

	/**
	 * <code>
	 * &lt;if test="@Expr@hasWhereExtClause(_parameter)"><br>
	 * ${whereExtClause}
	 * &lt;if><br>
	 * </code>
	 *
	 * @return
	 */
	public String whereExtClause(String alias) {
		alias = StringUtils.trimToNull(alias);
		StringBuilder sql = new StringBuilder();
		sql.append("<trim prefix=\"and\" suffix=\"\" suffixOverrides=\"\" prefixOverrides=\"and\" >\n");
		sql.append("<if test=\"@").append(Expr.NAME).append("@hasWhereExtClause(_parameter)\">\n");
		if (alias == null) {
			sql.append("${").append(MybatisConstants.PARAM_WHERE_EXTENSION_CLAUSE).append("}\n");
		} else {
			sql.append("${@").append(Expr.NAME).append("@withAlias(")
				.append(MybatisConstants.PARAM_WHERE_EXTENSION_CLAUSE).append(",'").append(alias).append("'")
				.append(")}\n");
		}

		sql.append("</if>\n");
		sql.append("</trim>");
		return sql.toString();
	}

	/**
	 * <code>
	 * &lt;if test="@Expr@hasOrderByClause(_parameter)"><br>
	 * order by ${orderByClause}
	 * &lt;if><br>
	 * </code>
	 *
	 * @return
	 */
	public String orderByClause(String alias) {
		alias = StringUtils.trimToNull(alias);
		if (alias == null) {
			return String.format(
				"<if test=\"@" + Expr.NAME + "@hasOrderByClause(_parameter)\">\n" +
					"order by ${%s}\n" +
					"</if>"
				, MybatisConstants.PARAM_ORDER_BY);
		} else {
			return String.format(
				"<if test=\"@" + Expr.NAME + "@hasOrderByClause(_parameter)\">\n" +
					"order by ${%s}\n" +
					"</if>"
				, "@" + Expr.NAME + "@withAlias(" + MybatisConstants.PARAM_ORDER_BY
					+ ",'" + alias + "')");
		}
	}

	/**
	 * whereByExample
	 *
	 * @return
	 */
	public String whereByExample() {
		StringBuilder sql = new StringBuilder();
		sql.append(ExampleXml.getWhereByExample());
		return sql.toString();
	}
}
