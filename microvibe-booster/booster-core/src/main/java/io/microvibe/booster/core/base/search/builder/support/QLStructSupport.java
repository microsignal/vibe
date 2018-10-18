package io.microvibe.booster.core.base.search.builder.support;

import io.microvibe.booster.core.base.search.builder.QL;
import io.microvibe.booster.core.base.search.builder.QLParameter;
import io.microvibe.booster.core.base.search.builder.QLStatement;
import io.microvibe.booster.core.base.search.builder.QLStruct;
import io.microvibe.booster.core.search.ISymbol;
import io.microvibe.booster.core.search.SearchOper;
import lombok.AccessLevel;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class QLStructSupport implements QLStruct, QLStatement {

	private Deque<QLStructSupport> subQLStructs = new ArrayDeque<>();
	@Setter(AccessLevel.PRIVATE)
	private boolean isNative = false;
	@Setter(AccessLevel.PRIVATE)
	private QLType qlType = QLType.part;
	@Setter(AccessLevel.PRIVATE)
	private boolean distinct = false;
	private List<String> selects = new ArrayList<>();// SELECT_BY_ENTITY 子句
	private List<Table> froms = new ArrayList<>();// FROM 子句
	private List<Where> wheres = new ArrayList<>();// WHERE 子句
	private List<String> groupbys = new ArrayList<>();// GROUP BY 子句
	private List<Where> havings = new ArrayList<>();// HAVING 子句
	private List<String> orderbys = new ArrayList<>();// ORDER BY 子句

	private Class<?> updateEntityClass;// UPDATE_BY_ID 目标对象
	private String updateEntityAlias;// UPDATE_BY_ID 目标对象别名
	private List<Set> sets = new ArrayList<>();// UPDATE_BY_ID SET 子句

	private Class<?> deleteEntityClass;// DELETE_BY_ID 目标对象
	private String deleteEntityAlias;// DELETE_BY_ID 目标对象别名

	private Class<?> insertEntityClass;// INSERT 目标对象
	private String insertEntityAlias;// INSERT 目标对象别名
	private List<String> columns = new ArrayList<>();// INSERT 列名
	private List<Object> values = new ArrayList<>();// INSERT 值
	private QLStatement insertSubQuery;// INSERT 子查询

	private List<QLParameter> bindedValues = new ArrayList<>();// 绑定变量列表
	private String countStatement = "";// 查询总数语句
	private List<Where> lastWheres = wheres;// 上次调用的 WHERE 或 HAVING 子句对象

	public void clear() {
		distinct = false;
		selects.clear();
		froms.clear();
		wheres.clear();
		updateEntityClass = null;
		updateEntityAlias = null;
		sets.clear();

		deleteEntityClass = null;
		deleteEntityAlias = null;

		insertEntityClass = null;
		insertEntityAlias = null;
		columns.clear();
		values.clear();
		insertSubQuery = null;
		bindedValues.clear();
	}

	public QLStatement build() {
		QL ql = new QL();
		String st = statement();
		ql.isNative(isNative);
		ql.statement(st);
		ql.countStatement(countStatement);
		ql.bindedValues(bindedValues);
		return ql;
	}

	public boolean isNative() {
		return isNative;
	}

	@Override
	public List<QLParameter> bindedValues() {
		return bindedValues;
	}

	@Override
	public String countStatement() {
		return countStatement;
	}

	@Override
	public String statement() {
		countStatement = "";
		bindedValues = new ArrayList<>();
		StringBuilder builder = new StringBuilder();
		switch (qlType) {
			case part:
				buildSelectClause(builder);
				buildFromClause(builder);
				buildWhereClause(builder);
				buildGroupByClause(builder);
				buildHavingClause(builder);
				buildOrderByClause(builder);
				break;
			case select:
				buildSelectClause(builder);
				StringBuilder countBuilder = new StringBuilder();
				StringBuilder partBuilder = new StringBuilder();
				buildFromClause(partBuilder);
				buildWhereClause(partBuilder);
				buildGroupByClause(partBuilder);
				buildHavingClause(partBuilder);
				if (partBuilder.length() > 0) {
					countBuilder.append("SELECT_BY_ENTITY count(*) \n")
						.append(partBuilder);
					builder.append("\n");
					builder.append(partBuilder);
				}
				countStatement = countBuilder.toString();
				buildOrderByClause(builder);
				break;
			case update:
				buildUpdateClause(builder);
				buildSetClause(builder);
				buildWhereClause(builder);
				break;
			case delete:
				buildDeleteClause(builder);
				buildWhereClause(builder);
				break;
			case insert:
				buildInsertClause(builder);
				buildColumnsClause(builder);
				buildValuesClause(builder);
				break;
			default:
				break;
		}

		return builder.toString();
	}

	private void buildSelectClause(StringBuilder builder) {
		if (!selects.isEmpty()) {
			if (distinct) {
				builder.append("SELECT_BY_ENTITY DISTINCT ");
			} else {
				builder.append("SELECT_BY_ENTITY ");
			}
			Iterator<String> iter = selects.iterator();
			builder.append(iter.next());
			while (iter.hasNext()) {
				builder.append(", ").append(iter.next());
			}
		}
	}

	private void buildFromClause(StringBuilder builder) {
		if (!froms.isEmpty()) {
			if (builder.length() != 0) {
				builder.append("\n");
			}
			Iterator<Table> iter = froms.iterator();
			Table table = iter.next();
			builder.append("FROM ").append(table.statement());
			while (iter.hasNext()) {
				table = iter.next();
				builder.append(table.conjunction()).append(table.statement());
				List<QLParameter> bindedValues = table.bindedValues();
				if (bindedValues != null) {
					this.bindedValues.addAll(bindedValues);
				}
			}
		}
	}

	private void buildWhereClause(StringBuilder builder) {
		if (!wheres.isEmpty()) {
			if (builder.length() != 0) {
				builder.append("\nWHERE ");
			}
			boolean started = true;
			// bind from context
			if (!froms.isEmpty()) {
				Table.buildContext(froms);
			}
			for (Where where : wheres) {
				String clause = where.statement();
				if (StringUtils.isNotBlank(clause)) {
					if (!started) {
						builder.append(where.conjunction());
					}
					started = false;
					builder.append(clause);
					List<QLParameter> bindedValues = where.bindedValues();
					if (bindedValues != null) {
						this.bindedValues.addAll(bindedValues);
					}
				}
			}
			// bind from context
			if (!froms.isEmpty()) {
				Table.clearContext();
			}
		}
	}

	private void buildGroupByClause(StringBuilder builder) {
		if (!groupbys.isEmpty()) {
			builder.append("\nGROUP BY ");
			Iterator<String> iter = groupbys.iterator();
			builder.append(iter.next());
			while (iter.hasNext()) {
				builder.append(", ").append(iter.next());
			}
		}
	}

	private void buildHavingClause(StringBuilder builder) {
		if (!havings.isEmpty()) {
			if (builder.length() != 0) {
				builder.append("\nHAVING ");
			}
			boolean started = true;
			for (Where where : havings) {
				String clause = where.statement();
				if (StringUtils.isNotBlank(clause)) {
					if (!started) {
						builder.append(where.conjunction());
					}
					started = false;
					builder.append(clause);
					List<QLParameter> bindedValues = where.bindedValues();
					if (bindedValues != null) {
						this.bindedValues.addAll(bindedValues);
					}
				}
			}
		}

	}

	private void buildOrderByClause(StringBuilder builder) {
		if (!orderbys.isEmpty()) {
			builder.append("\nORDER BY ");
			Iterator<String> iter = orderbys.iterator();
			builder.append(iter.next());
			while (iter.hasNext()) {
				builder.append(", ").append(iter.next());
			}
		}
	}

	private void buildUpdateClause(StringBuilder builder) {
		if (updateEntityClass != null) {
			builder.append("UPDATE_BY_ID ").append(updateEntityClass.getName());
			if (updateEntityAlias != null) {
				builder.append(" ").append(updateEntityAlias);
			}
		}
	}

	private void buildSetClause(StringBuilder builder) {
		if (!sets.isEmpty()) {
			builder.append("\nSET\n");
			boolean started = true;
			for (Set set : sets) {
				if (!started) {
					builder.append(",\n");
				}
				started = false;
				builder.append(set.getKey());
				if (set.getVal() != null) {
					String bindedKey = BindedKeys.nextBindedKey();
					builder.append(" = :").append(bindedKey);
					bindedValues.add(new QLParameter(bindedKey, set.getVal().orElse(null)));
				} else if (set.getSubQuery() != null) {
					QLStatement st = set.toStatement();
					builder.append(" = ( ").append(st.statement()).append(" )");
					bindedValues.addAll(st.bindedValues());
				}
			}
		}
	}

	private void buildDeleteClause(StringBuilder builder) {
		if (deleteEntityClass != null) {
			builder.append("DELETE_BY_ID FROM ").append(deleteEntityClass.getName());
			if (deleteEntityAlias != null) {
				builder.append(" ").append(deleteEntityAlias);
			}
		}
	}

	private void buildInsertClause(StringBuilder builder) {
		if (insertEntityClass != null) {
			builder.append("INSERT INTO ").append(insertEntityClass.getName());
			if (insertEntityAlias != null) {
				builder.append(" ").append(insertEntityAlias);
			}
		}
	}

	private void buildColumnsClause(StringBuilder builder) {
		if (!columns.isEmpty()) {
			builder.append("\n(");
			boolean started = true;
			for (String col : columns) {
				if (!started) {
					builder.append(", ");
				}
				started = false;
				builder.append(col);
			}
			builder.append(")");
		}
	}

	private void buildValuesClause(StringBuilder builder) {
		if (insertSubQuery != null) {
			builder.append("\n").append(insertSubQuery.statement());
			insertSubQuery.bindedValues();
			this.bindedValues.addAll(insertSubQuery.bindedValues());
		} else if (!values.isEmpty()) {
			builder.append("\nVALUES\n(");
			boolean started = true;
			for (Object val : values) {
				if (!started) {
					builder.append(", ");
				}
				started = false;
				String bindedKey = BindedKeys.nextBindedKey();
				builder.append(":").append(bindedKey);
				bindedValues.add(new QLParameter(bindedKey, val));
			}
			builder.append(")");
		}
	}

	@Override
	public QLStruct NATIVE(boolean isNative) {
		this.isNative = isNative;
		return this;
	}

	@Override
	public QLStruct SELECT(String... columns) {
		setQlType(QLType.select);
		for (String column : columns) {
			selects.add(column);
		}
		return this;
	}

	@Override
	public QLStruct SELECT_DISTINCT(String... columns) {
		setDistinct(true);
		return SELECT(columns);
	}

	@Override
	public QLStruct FROM(Class<?> entityClass) {
		return FROM(entityClass, null);
	}

	@Override
	public QLStruct FROM(Class<?> entityClass, String alias) {
		if (StringUtils.isBlank(alias)) {
			alias = org.springframework.util.ClassUtils.getShortNameAsProperty(entityClass);
		}
		froms.add(new Table(entityClass, alias));
		return this;
	}

	@Override
	public QLStruct FROM(String subQuery, String alias) {
		froms.add(new Table(subQuery, alias));
		return this;
	}

	@Override
	public QLStruct FROM(QLStatement st, String alias) {
		froms.add(new ProxyTable(st, alias));
		return this;
	}

	@Override
	public QLStruct FROM(Function<QLStruct, QLStatement> function, String alias) {
		return FROM(function.apply(new QLStructSupport()), alias);
	}

	@Override
	public QLStruct JOIN(Class<?> entityClass, String alias, String onCondition) {
		return JOIN("JOIN", entityClass, alias, onCondition);
	}

	@Override
	public QLStruct INNER_JOIN(Class<?> entityClass, String alias, String onCondition) {
		return JOIN("INNER JOIN", entityClass, alias, onCondition);
	}

	@Override
	public QLStruct OUTER_JOIN(Class<?> entityClass, String alias, String onCondition) {
		return JOIN("OUTER JOIN", entityClass, alias, onCondition);
	}

	@Override
	public QLStruct LEFT_OUTER_JOIN(Class<?> entityClass, String alias, String onCondition) {
		return JOIN("LEFT OUTER JOIN", entityClass, alias, onCondition);
	}

	@Override
	public QLStruct RIGHT_OUTER_JOIN(Class<?> entityClass, String alias, String onCondition) {
		return JOIN("RIGHT OUTER JOIN", entityClass, alias, onCondition);
	}

	private QLStruct JOIN(String join, Class<?> entityClass, String alias, String onCondition) {
		froms.add(new JoinTable(entityClass, alias, join, onCondition));
		return this;
	}

	@Override
	public QLStruct JOIN(String subQuery, String alias, String onCondition) {
		return JOIN("JOIN", subQuery, alias, onCondition);
	}

	@Override
	public QLStruct INNER_JOIN(String subQuery, String alias, String onCondition) {
		return JOIN("INNER JOIN", subQuery, alias, onCondition);
	}

	@Override
	public QLStruct OUTER_JOIN(String subQuery, String alias, String onCondition) {
		return JOIN("OUTER JOIN", subQuery, alias, onCondition);
	}

	@Override
	public QLStruct LEFT_OUTER_JOIN(String subQuery, String alias, String onCondition) {
		return JOIN("LEFT OUTER JOIN", subQuery, alias, onCondition);
	}

	@Override
	public QLStruct RIGHT_OUTER_JOIN(String subQuery, String alias, String onCondition) {
		return JOIN("RIGHT OUTER JOIN", subQuery, alias, onCondition);
	}

	private QLStruct JOIN(String join, String subQuery, String alias, String onCondition) {
		froms.add(new JoinTable(subQuery, alias, join, onCondition));
		return this;
	}

	@Override
	public QLStruct JOIN(QLStatement st, String alias, String onCondition) {
		return JOIN("JOIN", st, alias, onCondition);
	}

	@Override
	public QLStruct INNER_JOIN(QLStatement st, String alias, String onCondition) {
		return JOIN("INNER JOIN", st, alias, onCondition);
	}

	@Override
	public QLStruct OUTER_JOIN(QLStatement st, String alias, String onCondition) {
		return JOIN("OUTER JOIN", st, alias, onCondition);
	}

	@Override
	public QLStruct LEFT_OUTER_JOIN(QLStatement st, String alias, String onCondition) {
		return JOIN("LEFT OUTER JOIN", st, alias, onCondition);
	}

	@Override
	public QLStruct RIGHT_OUTER_JOIN(QLStatement st, String alias, String onCondition) {
		return JOIN("RIGHT OUTER JOIN", st, alias, onCondition);
	}

	@Override
	public QLStruct JOIN(Function<QLStruct, QLStatement> function, String alias, String onCondition) {
		return JOIN("JOIN", function, alias, onCondition);
	}

	@Override
	public QLStruct INNER_JOIN(Function<QLStruct, QLStatement> function, String alias, String onCondition) {
		return JOIN("INNER JOIN", function, alias, onCondition);
	}

	@Override
	public QLStruct OUTER_JOIN(Function<QLStruct, QLStatement> function, String alias, String onCondition) {
		return JOIN("OUTER JOIN", function, alias, onCondition);
	}

	@Override
	public QLStruct LEFT_OUTER_JOIN(Function<QLStruct, QLStatement> function, String alias, String onCondition) {
		return JOIN("LEFT OUTER JOIN", function, alias, onCondition);
	}

	@Override
	public QLStruct RIGHT_OUTER_JOIN(Function<QLStruct, QLStatement> function, String alias, String onCondition) {
		return JOIN("RIGHT OUTER JOIN", function, alias, onCondition);
	}

	private QLStruct JOIN(String join, QLStatement st, String alias, String onCondition) {
		froms.add(new ProxyJoinTable(st, alias, join, onCondition));
		return this;
	}

	private QLStruct JOIN(String join, Function<QLStruct, QLStatement> function, String alias, String onCondition) {
		return JOIN(join, function.apply(new QLStructSupport()), alias, onCondition);
	}

	public QLStruct AND() {
		QLStructSupport subQLStruct = new QLStructSupport();
		QLStruct and = AND(subQLStruct);
		subQLStructs.push(subQLStruct);
		return and;
	}

	public QLStruct OR() {
		QLStructSupport subQLStruct = new QLStructSupport();
		QLStruct or = OR(subQLStruct);
		subQLStructs.push(subQLStruct);
		return or;
	}

	public QLStruct END() {
		subQLStructs.pop();
		return this;
	}

	@Override
	public QLStruct AND(QLStatement st) {
		assertNotSelf(st);
		QLStructSupport subQLStruct = subQLStructs.peek();
		if (subQLStruct != null) {
			subQLStruct.lastWheres.add(new And(st));
		}else {
			lastWheres.add(new And(st));
		}
		return this;
	}

	@Override
	public QLStruct AND(Function<QLStruct, QLStatement> function) {
		return AND(function.apply(new QLStructSupport()));
	}

	@Override
	public QLStruct OR(QLStatement st) {
		assertNotSelf(st);
		QLStructSupport subQLStruct = subQLStructs.peek();
		if (subQLStruct != null) {
			subQLStruct.lastWheres.add(new Or(st));
		}else {
			lastWheres.add(new Or(st));
		}
		return this;
	}

	@Override
	public QLStruct OR(Function<QLStruct, QLStatement> function) {
		return OR(function.apply(new QLStructSupport()));
	}

	@Override
	public QLStruct WHERE(String where) {
		return WHERE(new Condition(where));
	}

	@Override
	public QLStruct WHERE(String where, Object... val) {
		return WHERE(new Condition(where, SearchOper.proto, val));
	}

	@Override
	public QLStruct WHERE(Class<?> entityClass, String key, SearchOper op, Object... val) {
		Condition cond = new Condition(key, op, val);
		cond.setEntityClass(entityClass);
		return WHERE(cond);
	}

	@Override
	public QLStruct WHERE(String alias, String key, SearchOper op, Object... val) {
		Condition cond = new Condition(key, op, val);
		cond.setAlias(alias);
		return WHERE(cond);
	}

	@Override
	public QLStruct WHERE(String key, SearchOper op, Object... val) {
		return WHERE(new Condition(key, op, val));
	}

	@Override
	public QLStruct WHERE(Condition cond) {
		QLStructSupport subQLStruct = subQLStructs.peek();
		if (subQLStruct == null) {
			addCondition(cond);
		} else {
			subQLStruct.addCondition(cond);
		}
		lastWheres = wheres;
		return this;
	}

	@Override
	public QLStruct WHERE(QLStatement st) {
		assertNotSelf(st);
		return WHERE(new ProxyCondition(st));
	}

	@Override
	public QLStruct WHERE(Function<QLStruct, QLStatement> function) {
		return WHERE(function.apply(new QLStructSupport()));
	}

	private void addCondition(Condition where) {
		String key = StringUtils.trimToNull(where.getKey());
		if (key == null) {
			throw new IllegalArgumentException("search key is empty!");
		}
		wheres.add(where);
	}

	@Override
	public QLStruct GROUP_BY(String... columns) {
		for (String column : columns) {
			groupbys.add(column);
		}
		return this;
	}

	@Override
	public QLStruct ORDER_BY(String... columns) {
		for (String column : columns) {
			orderbys.add(column);
		}
		return this;
	}

	@Override
	public QLStruct HAVING(String having) {
		return HAVING(new Having(having, SearchOper.proto));
	}

	@Override
	public QLStruct HAVING(String having, Object... val) {
		if (val.length == 0) {
			return HAVING(having);
		} else if (val.length == 1) {
			return HAVING(new Having(having, SearchOper.proto, val[0]));
		} else {
			return HAVING(new Having(having, SearchOper.proto, val));
		}
	}

	@Override
	public QLStruct HAVING(String key, SearchOper op, Object... val) {
		if (val.length == 0) {
			return HAVING(new Having(key, op));
		} else if (val.length == 1) {
			return HAVING(new Having(key, op, val[0]));
		} else {
			return HAVING(new Having(key, op, val));
		}
	}

	@Override
	public QLStruct HAVING(Having having) {
		QLStructSupport subQLStruct = subQLStructs.peek();
		if (subQLStruct == null) {
			addHaving(having);
		} else {
			subQLStruct.addHaving(having);
		}
		lastWheres = havings;
		return this;
	}

	@Override
	public QLStruct HAVING(QLStatement st) {
		assertNotSelf(st);
		return HAVING(new ProxyHaving(st));
	}

	@Override
	public QLStruct HAVING(Function<QLStruct, QLStatement> function) {
		return HAVING(function.apply(new QLStructSupport()));
	}

	private void addHaving(Having having) {
		String key = StringUtils.trimToNull(having.getKey());
		if (key == null) {
			throw new IllegalArgumentException("having key is empty!");
		}
		havings.add(having);
	}

	@Override
	public QLStruct UPDATE(Class<?> entityClass) {
		setQlType(QLType.update);
		this.updateEntityClass = entityClass;
		return this;
	}

	@Override
	public QLStruct UPDATE(Class<?> entityClass, String alias) {
		setQlType(QLType.update);
		this.updateEntityClass = entityClass;
		this.updateEntityAlias = StringUtils.trimToNull(alias);
		return this;
	}

	@Override
	public QLStruct SET(String key, Object val) {
		Assert.notNull(updateEntityClass, "缺少 UPDATE_BY_ID 子句!");
		Set set = new Set();
		set.setKey(key);
		set.setVal(Optional.ofNullable(Conversions.toConvertedValue(updateEntityClass, key, val)));
		sets.add(set);
		return this;
	}

	@Override
	public QLStruct SET(String key, String subQuery, Object[] args) {
		Assert.notNull(updateEntityClass, "缺少 UPDATE_BY_ID 子句!");
		Set set = new Set();
		set.setKey(key);
		set.setVal(null);
		set.setSubQuery(Optional.ofNullable(subQuery));
		set.setArgs(args);
		sets.add(set);
		return this;
	}

	@Override
	public QLStruct SET(String key) {
		Assert.notNull(updateEntityClass, "缺少 UPDATE_BY_ID 子句!");
		Set set = new Set();
		set.setKey(key);
		set.setVal(null);
		sets.add(set);
		return this;
	}

	@Override
	public QLStruct DELETE(Class<?> entityClass) {
		setQlType(QLType.delete);
		this.deleteEntityClass = entityClass;
		return this;
	}

	@Override
	public QLStruct DELETE(Class<?> entityClass, String alias) {
		setQlType(QLType.delete);
		this.deleteEntityClass = entityClass;
		this.deleteEntityAlias = StringUtils.trimToNull(alias);
		return this;
	}

	@Override
	public QLStruct INSERT(Class<?> entityClass) {
		setQlType(QLType.insert);
		this.insertEntityClass = entityClass;
		return this;
	}

	@Override
	public QLStruct INSERT(Class<?> entityClass, String alias) {
		setQlType(QLType.insert);
		this.insertEntityClass = entityClass;
		this.insertEntityAlias = StringUtils.trimToNull(alias);
		return this;
	}

	@Override
	public QLStruct VALUES(String key, Object val) {
		Assert.notNull(insertEntityClass, "缺少 INSERT 子句");
		if (columns.size() != values.size()) {
			throw new IllegalStateException("已使用子查询方式!");
		}
		columns.add(key);
		values.add(Conversions.toConvertedValue(insertEntityClass, key, val));
		return this;
	}

	@Override
	public QLStruct COLUMNS(String key) {
		Assert.notNull(insertEntityClass, "缺少 INSERT 子句!");
		if (values.size() > 0) {
			throw new IllegalStateException("已使用 VALUES 子句!");
		}
		columns.add(key);
		return this;
	}

	@Override
	public QLStruct VALUES(QLStatement insertSubQuery) {
		Assert.notNull(insertEntityClass, "缺少 INSERT 子句!");
		if (values.size() > 0) {
			throw new IllegalStateException("已使用 VALUES 子句!");
		}
		this.insertSubQuery = insertSubQuery;
		return this;
	}

	@Override
	public QLStruct VALUES(Function<QLStruct, QLStatement> function) {
		return VALUES(function.apply(new QLStructSupport()));
	}

	private void assertNotSelf(QLStatement st) {
		if (this == st) {
			throw new IllegalArgumentException("it's self reference!");
		}
	}

	@Override
	public QLStruct consume(Consumer<QLStruct> consumer) {
		consumer.accept(this);
		return this;
	}
}
