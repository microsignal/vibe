package io.microvibe.booster.core.base.search.builder;

import io.microvibe.booster.core.base.search.builder.support.Condition;
import io.microvibe.booster.core.base.search.builder.support.Having;
import io.microvibe.booster.core.base.search.builder.support.QLStructSupport;
import io.microvibe.booster.core.search.SearchOper;
import lombok.ToString;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.Function;

/**
 * SQL构建器
 *
 * @author Qt
 * @version 1.0.1
 * @since Mar 05, 2018
 */
@ToString
public class QL implements QLStatement {
	//    private static ThreadLocal<QLStruct> local = ThreadLocal.withInitial(() -> new QLStructSupport());
	private static ThreadLocal<Deque<QLStruct>> context = ThreadLocal.withInitial(() -> new ArrayDeque<>());

	private boolean isNative;
	private String statement;
	private String countStatement;
	private List<QLParameter> bindedValues = new ArrayList<>();

	public QL() {
		super();
	}

	public QL(boolean isNative, String statement, List<QLParameter> bindedValues) {
		super();
		this.isNative = isNative;
		this.statement = statement;
		this.bindedValues = bindedValues;
	}

	public QL(boolean isNative, String statement, String countStatement, List<QLParameter> bindedValues) {
		super();
		this.isNative = isNative;
		this.statement = statement;
		this.countStatement = countStatement;
		this.bindedValues = bindedValues;
	}

	public void isNative(boolean isNative) {
		this.isNative = isNative;
	}

	public void statement(String statement) {
		this.statement = statement;
	}

	public void countStatement(String countStatement) {
		this.countStatement = countStatement;
	}

	public void bindedValues(List<QLParameter> bindedValues) {
		this.bindedValues = bindedValues;
	}

	@Override
	public boolean isNative() {
		return isNative;
	}

	@Override
	public String statement() {
		return statement;
	}

	@Override
	public String countStatement() {
		return countStatement;
	}

	@Override
	public List<QLParameter> bindedValues() {
		return bindedValues;
	}

	public static void clear() {
		context.remove();
	}

	public static QLStruct newStruct() {
		Deque<QLStruct> deque = context.get();
		deque.push(new QLStructSupport());
		return deque.peek();
	}

	public static QLStruct struct() {
		Deque<QLStruct> deque = context.get();
		if (deque.isEmpty()) {
			deque.push(new QLStructSupport());
		}
		return deque.peek();
	}

	/**
	 * 构建为 {@linkplain QLStatement} 对象, 并清空上下文线程变量{@linkplain QLStruct}
	 *
	 * @return
	 */
	public static QLStatement build() {
		try {
			return struct().build();
		} finally {
			context.get().pop();
		}
	}

	public static QLStruct NATIVE(boolean isNative) {
		return struct().NATIVE(isNative);
	}

	public static QLStruct SELECT(String... columns) {
		return struct().SELECT(columns);
	}

	public static QLStruct SELECT_DISTINCT(String... columns) {
		return struct().SELECT_DISTINCT(columns);
	}

	public static QLStruct FROM(Class<?> entityClass) {
		return struct().FROM(entityClass);
	}

	public static QLStruct FROM(Class<?> entityClass, String alias) {
		return struct().FROM(entityClass, alias);
	}

	public static QLStruct FROM(String subQuery, String alias) {
		return struct().FROM(subQuery, alias);
	}

	public static QLStruct FROM(QLStatement subQuery, String alias) {
		return struct().FROM(subQuery, alias);
	}

	public static QLStruct FROM(Function<QLStruct, QLStatement> subQuery, String alias) {
		return struct().FROM(subQuery, alias);
	}

	public static QLStruct JOIN(Class<?> entityClass, String alias, String onCondition) {
		return struct().JOIN(entityClass, alias, onCondition);
	}

	public static QLStruct INNER_JOIN(Class<?> entityClass, String alias, String onCondition) {
		return struct().INNER_JOIN(entityClass, alias, onCondition);
	}

	public static QLStruct OUTER_JOIN(Class<?> entityClass, String alias, String onCondition) {
		return struct().OUTER_JOIN(entityClass, alias, onCondition);
	}

	public static QLStruct LEFT_OUTER_JOIN(Class<?> entityClass, String alias, String onCondition) {
		return struct().LEFT_OUTER_JOIN(entityClass, alias, onCondition);
	}

	public static QLStruct RIGHT_OUTER_JOIN(Class<?> entityClass, String alias, String onCondition) {
		return struct().RIGHT_OUTER_JOIN(entityClass, alias, onCondition);
	}

	public static QLStruct JOIN(String subQuery, String alias, String onCondition) {
		return struct().JOIN(subQuery, alias, onCondition);
	}

	public static QLStruct INNER_JOIN(String subQuery, String alias, String onCondition) {
		return struct().INNER_JOIN(subQuery, alias, onCondition);
	}

	public static QLStruct OUTER_JOIN(String subQuery, String alias, String onCondition) {
		return struct().OUTER_JOIN(subQuery, alias, onCondition);
	}

	public static QLStruct LEFT_OUTER_JOIN(String subQuery, String alias, String onCondition) {
		return struct().LEFT_OUTER_JOIN(subQuery, alias, onCondition);
	}

	public static QLStruct RIGHT_OUTER_JOIN(String subQuery, String alias, String onCondition) {
		return struct().RIGHT_OUTER_JOIN(subQuery, alias, onCondition);
	}

	public static QLStruct JOIN(QLStatement subQuery, String alias, String onCondition) {
		return struct().JOIN(subQuery, alias, onCondition);
	}

	public static QLStruct INNER_JOIN(QLStatement subQuery, String alias, String onCondition) {
		return struct().INNER_JOIN(subQuery, alias, onCondition);
	}

	public static QLStruct OUTER_JOIN(QLStatement subQuery, String alias, String onCondition) {
		return struct().OUTER_JOIN(subQuery, alias, onCondition);
	}

	public static QLStruct LEFT_OUTER_JOIN(QLStatement subQuery, String alias, String onCondition) {
		return struct().LEFT_OUTER_JOIN(subQuery, alias, onCondition);
	}

	public static QLStruct RIGHT_OUTER_JOIN(QLStatement subQuery, String alias,
											String onCondition) {
		return struct().RIGHT_OUTER_JOIN(subQuery, alias, onCondition);
	}

	public static QLStruct JOIN(Function<QLStruct, QLStatement> subQuery, String alias, String onCondition) {
		return struct().JOIN(subQuery, alias, onCondition);
	}

	public static QLStruct INNER_JOIN(Function<QLStruct, QLStatement> subQuery, String alias, String onCondition) {
		return struct().INNER_JOIN(subQuery, alias, onCondition);
	}

	public static QLStruct OUTER_JOIN(Function<QLStruct, QLStatement> subQuery, String alias, String onCondition) {
		return struct().OUTER_JOIN(subQuery, alias, onCondition);
	}

	public static QLStruct LEFT_OUTER_JOIN(Function<QLStruct, QLStatement> subQuery, String alias, String onCondition) {
		return struct().LEFT_OUTER_JOIN(subQuery, alias, onCondition);
	}

	public static QLStruct RIGHT_OUTER_JOIN(Function<QLStruct, QLStatement> subQuery, String alias,
											String onCondition) {
		return struct().RIGHT_OUTER_JOIN(subQuery, alias, onCondition);
	}

	public static QLStruct AND() {
		return struct().AND();
	}

	public static QLStruct AND(QLStatement function) {
		return struct().AND(function);
	}

	public static QLStruct AND(Function<QLStruct, QLStatement> function) {
		return struct().AND(function);
	}

	public static QLStruct OR() {
		return struct().OR();
	}

	public static QLStruct OR(QLStatement function) {
		return struct().OR(function);
	}

	public static QLStruct OR(Function<QLStruct, QLStatement> function) {
		return struct().OR(function);
	}

	public static QLStruct END() {
		return struct().END();
	}

	/**
	 * 添加原生语句格式的查询条件.
	 *
	 * @param where 如: <code>alias.id = 1L</code>
	 */
	public static QLStruct WHERE(String where) {
		return struct().WHERE(where);
	}

	public static QLStruct WHERE(QLStatement where) {
		return struct().WHERE(where);
	}

	public static QLStruct WHERE(Function<QLStruct, QLStatement> where) {
		return struct().WHERE(where);
	}

	public static QLStruct WHERE(String where, Object... val) {
		return struct().WHERE(where, val);
	}

	public static QLStruct WHERE(Class<?> entityClass, String key, SearchOper op, Object... val) {
		return struct().WHERE(entityClass, key, op, val);
	}

	public static QLStruct WHERE(String alias, String key, SearchOper op, Object... val) {
		return struct().WHERE(alias, key, op, val);
	}

	public static QLStruct WHERE(String key, SearchOper op, Object... val) {
		return struct().WHERE(key, op, val);
	}

	public static QLStruct WHERE(Condition cond) {
		return struct().WHERE(cond);
	}

	public static QLStruct GROUP_BY(String... columns) {
		return struct().GROUP_BY(columns);
	}

	public static QLStruct HAVING(String having) {
		return struct().HAVING(having);
	}

	public static QLStruct HAVING(QLStatement having) {
		return struct().HAVING(having);
	}

	public static QLStruct HAVING(Function<QLStruct, QLStatement> having) {
		return struct().HAVING(having);
	}

	public static QLStruct HAVING(String having, Object... val) {
		return struct().HAVING(having, val);
	}

	public static QLStruct HAVING(String key, SearchOper op, Object... val) {
		return struct().HAVING(key, op, val);
	}

	public static QLStruct HAVING(Having having) {
		return struct().HAVING(having);
	}

	public static QLStruct ORDER_BY(String... columns) {
		return struct().ORDER_BY(columns);
	}

	public static QLStruct UPDATE(Class<?> entityClass) {
		return struct().UPDATE(entityClass);
	}

	public static QLStruct UPDATE(Class<?> entityClass, String alias) {
		return struct().UPDATE(entityClass, alias);
	}

	public static QLStruct SET(String key, Object val) {
		return struct().SET(key, val);
	}

	public static QLStruct SET(String key) {
		return struct().SET(key);
	}

	public static QLStruct SET(String key, String subQuery, Object[] args) {
		return struct().SET(key, subQuery, args);
	}

	public static QLStruct DELETE(Class<?> entityClass) {
		return struct().DELETE(entityClass);
	}

	public static QLStruct DELETE(Class<?> entityClass, String alias) {
		return struct().DELETE(entityClass, alias);
	}

	public static QLStruct INSERT(Class<?> entityClass) {
		return struct().INSERT(entityClass);
	}

	public static QLStruct INSERT(Class<?> entityClass, String alias) {
		return struct().INSERT(entityClass, alias);
	}

	public static QLStruct VALUES(String key, Object val) {
		return struct().VALUES(key, val);
	}

	public static QLStruct COLUMNS(String key) {
		return struct().COLUMNS(key);
	}

	public static QLStruct VALUES(QLStatement insertSubQueryFunction) {
		return struct().VALUES(insertSubQueryFunction);
	}

	public static QLStruct VALUES(Function<QLStruct, QLStatement> insertSubQueryFunction) {
		return struct().VALUES(insertSubQueryFunction);
	}

}
