package io.microvibe.booster.core.base.mybatis.builder;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Qt
 * @since Aug 10, 2018
 */
@Slf4j
public class SqlParsers {


	public static String restructSelect(String sql, Function<Collection<Table>, String> conditionSupplier) throws JSQLParserException {
		return restructSelect(sql, conditionSupplier, null);
	}

	public static String restructSelect(String sql, Consumer<SelectColumn> columnFilter) throws JSQLParserException {
		return restructSelect(sql, null, columnFilter);
	}

	public static String restructSelect(String sql, Function<Collection<Table>, String> conditionSupplier, Consumer<SelectColumn> columnFilter) throws JSQLParserException {
		Statement stmt = CCJSqlParserUtil.parse(sql);
		Select select = (Select) stmt;
		SelectBody selectBody = select.getSelectBody();
		restruct(selectBody, conditionSupplier, columnFilter);
		return select.toString();
	}

	private static void restruct(SelectBody selectBody, Function<Collection<Table>, String> conditionSupplier, Consumer<SelectColumn> columnFilter) throws JSQLParserException {
		if (selectBody instanceof PlainSelect) {
			PlainSelect plainSelect = (PlainSelect) selectBody;
			restruct(plainSelect, conditionSupplier, columnFilter);
		} else if (selectBody instanceof WithItem) {
			WithItem withItem = (WithItem) selectBody;
			if (withItem.getSelectBody() != null) {
				restruct(withItem.getSelectBody(), conditionSupplier, columnFilter);
			}
		} else if (selectBody instanceof SetOperationList) {
			SetOperationList operationList = (SetOperationList) selectBody;
			if (operationList.getSelects() != null && operationList.getSelects().size() > 0) {
				List<SelectBody> plainSelects = operationList.getSelects();
				for (SelectBody plainSelect : plainSelects) {
					restruct(plainSelect, conditionSupplier, columnFilter);
				}
			}
		}
	}

	private static void restruct(PlainSelect plainSelect, Function<Collection<Table>, String> conditionSupplier,
		Consumer<SelectColumn> columnFilter) throws JSQLParserException {
		Map<String, Table> tables = new LinkedHashMap<>();
		{
			FromItem fromItem = plainSelect.getFromItem();
			fetchTables(tables, fromItem);
		}
		List<Join> joins = plainSelect.getJoins();
		if (joins != null) {
			for (Join join : joins) {
				FromItem item = join.getRightItem();
				fetchTables(tables, item);
			}
		}
		if (conditionSupplier != null) {
			String conditionSql = StringUtils.trimToNull(conditionSupplier.apply(tables.values()));
			if (conditionSql != null) {
				Expression condition = CCJSqlParserUtil.parseCondExpression(conditionSql);
				Expression where = plainSelect.getWhere();
				if (where == null) {
					plainSelect.setWhere(condition);
				} else {
					if (!(where instanceof Parenthesis)) {
						where = new Parenthesis(where);
					}
					if (!(condition instanceof Parenthesis)) {
						condition = new Parenthesis(condition);
					}
					AndExpression andExpression = new AndExpression(where, condition);
					plainSelect.setWhere(andExpression);
				}
			}
		}
		if (columnFilter != null) {
			List<SelectItem> selectItems = plainSelect.getSelectItems();
			Iterator<SelectItem> iter = selectItems.iterator();
			while (iter.hasNext()) {
				SelectItem selectItem = iter.next();
				if (selectItem instanceof SelectExpressionItem) {
					((SelectExpressionItem) selectItem).getAlias();
					Expression expression = ((SelectExpressionItem) selectItem).getExpression();
					if (expression instanceof Column) {
						Table colTable = ((Column) expression).getTable();
						if (colTable != null) {
							String tableAlias = colTable.getName();
							Table table = tables.get(tableAlias);
							if (table != null) {
								SelectColumn selectColumn = new SelectColumn(table, ((Column) expression).getColumnName());
								columnFilter.accept(selectColumn);
								if (selectColumn.isSkip()) {
									iter.remove();
								}
							}
						}
					} else {
						// 其他表达式忽略
					}
				} else if (selectItem instanceof AllTableColumns) {
					// 其他表达式忽略
				} else if (selectItem instanceof AllColumns) {
					// 其他表达式忽略
				}
			}
		}
	}

	private static void fetchTables(Map<String, Table> tables, FromItem fromItem) {
		if (fromItem instanceof Table) {
			Alias alias = ((Table) fromItem).getAlias();
			if (alias != null) {
				tables.put(alias.getName(), (Table) fromItem);
			} else {
				tables.put(((Table) fromItem).getName(), (Table) fromItem);
			}
		}
	}

	public static class SelectColumn {
		@Getter
		Table table;
		@Getter
		String columnName;
		boolean skip = false;
		//List<String> replaceColumns;

		SelectColumn(Table table, String columnName) {
			this.table = table;
			this.columnName = StringUtils.trimToNull(columnName);
			Assert.notNull(this.columnName, "column name is required");
		}

		public void skip() {
			this.skip = true;
		}

		public boolean isSkip() {
			return skip;
		}

		/*
		public void reset() {
			skip = false;
			replaceColumns = null;
		}
		public boolean isReplaced() {
			return replaceColumns != null;
		}


		public void replace(String... columns) {
			List<String> replaceColumns = new ArrayList<>(columns.length);
			for (String column : columns) {
				column = StringUtils.trimToNull(column);
				if (column != null) {
					replaceColumns.add(column);
				}
			}
			this.replaceColumns = Collections.unmodifiableList(replaceColumns);
		}*/
	}

}
