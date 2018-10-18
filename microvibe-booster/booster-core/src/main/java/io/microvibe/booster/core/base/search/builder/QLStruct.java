package io.microvibe.booster.core.base.search.builder;

import io.microvibe.booster.core.base.search.builder.support.Condition;
import io.microvibe.booster.core.base.search.builder.support.Having;
import io.microvibe.booster.core.search.SearchOper;

import java.util.function.Consumer;
import java.util.function.Function;

public interface QLStruct {

	/**
	 * 构建为 {@linkplain QLStatement} 对象
	 *
	 * @return
	 */
	QLStatement build();

	QLStruct consume(Consumer<QLStruct> consumer);

	QLStruct NATIVE(boolean isNative);

	QLStruct SELECT(String... columns);

	QLStruct SELECT_DISTINCT(String... columns);

	QLStruct FROM(String subQuery, String alias);

	QLStruct FROM(Class<?> entityClass, String alias);

	QLStruct FROM(Class<?> entityClass);

	QLStruct FROM(QLStatement st, String alias);

	QLStruct FROM(Function<QLStruct, QLStatement> function, String alias);

	QLStruct JOIN(Class<?> entityClass, String alias, String onCondition);

	QLStruct INNER_JOIN(Class<?> entityClass, String alias, String onCondition);

	QLStruct OUTER_JOIN(Class<?> entityClass, String alias, String onCondition);

	QLStruct LEFT_OUTER_JOIN(Class<?> entityClass, String alias, String onCondition);

	QLStruct RIGHT_OUTER_JOIN(Class<?> entityClass, String alias, String onCondition);

	QLStruct JOIN(String subQuery, String alias, String onCondition);

	QLStruct INNER_JOIN(String subQuery, String alias, String onCondition);

	QLStruct OUTER_JOIN(String subQuery, String alias, String onCondition);

	QLStruct LEFT_OUTER_JOIN(String subQuery, String alias, String onCondition);

	QLStruct RIGHT_OUTER_JOIN(String subQuery, String alias, String onCondition);

	QLStruct JOIN(QLStatement st, String alias, String onCondition);

	QLStruct INNER_JOIN(QLStatement st, String alias, String onCondition);

	QLStruct OUTER_JOIN(QLStatement st, String alias, String onCondition);

	QLStruct LEFT_OUTER_JOIN(QLStatement st, String alias, String onCondition);

	QLStruct RIGHT_OUTER_JOIN(QLStatement st, String alias, String onCondition);

	QLStruct JOIN(Function<QLStruct, QLStatement> function, String alias, String onCondition);

	QLStruct INNER_JOIN(Function<QLStruct, QLStatement> function, String alias, String onCondition);

	QLStruct OUTER_JOIN(Function<QLStruct, QLStatement> function, String alias, String onCondition);

	QLStruct LEFT_OUTER_JOIN(Function<QLStruct, QLStatement> function, String alias, String onCondition);

	QLStruct RIGHT_OUTER_JOIN(Function<QLStruct, QLStatement> function, String alias, String onCondition);

	QLStruct AND();

	QLStruct OR();

	QLStruct END();

	QLStruct AND(QLStatement st);

	QLStruct AND(Function<QLStruct, QLStatement> function);

	QLStruct OR(QLStatement st);

	QLStruct OR(Function<QLStruct, QLStatement> function);

	/**
	 * 添加原生语句格式的查询条件.
	 *
	 * @param where 如: <code>alias.id = 1L</code>
	 */
	QLStruct WHERE(String where);

	/**
	 * 添加原生语句格式的查询条件.
	 *
	 * @param where 如: <code>alias.id = {}</code>
	 * @param val   如: <code>1L</code>
	 */
	QLStruct WHERE(String where, Object... val);

	QLStruct WHERE(String key, SearchOper op, Object... val);

	QLStruct WHERE(String alias, String key, SearchOper op, Object... val);

	QLStruct WHERE(Class<?> entityClass, String key, SearchOper op, Object... val);

	QLStruct WHERE(Condition cond);

	QLStruct WHERE(QLStatement st);

	QLStruct WHERE(Function<QLStruct, QLStatement> where);

	QLStruct GROUP_BY(String... groupByColumns);

	QLStruct HAVING(String having);

	QLStruct HAVING(String having, Object... val);

	QLStruct HAVING(String key, SearchOper op, Object... val);

	QLStruct HAVING(Having having);

	QLStruct HAVING(QLStatement st);

	QLStruct HAVING(Function<QLStruct, QLStatement> having);

	QLStruct ORDER_BY(String... orderByColumns);

	QLStruct UPDATE(Class<?> entityClass);

	QLStruct UPDATE(Class<?> entityClass, String alias);

	QLStruct SET(String key, Object val);

	QLStruct SET(String key);

	QLStruct SET(String key, String subQuery, Object[] args);

	QLStruct DELETE(Class<?> entityClass);

	QLStruct DELETE(Class<?> entityClass, String alias);

	QLStruct INSERT(Class<?> entityClass);

	QLStruct INSERT(Class<?> entityClass, String alias);

	QLStruct VALUES(String key, Object val);

	QLStruct COLUMNS(String key);

	/**
	 * <pre>
	 * QLStatement subQuery = QL.SELECT_BY_ENTITY(..).FROM(..).WHERE(..).build();
	 * QL.INSERT(Entity.class).COLUMNS("id,name").VALUES(subQuery);
	 * </pre>
	 *
	 * @param insertSubQuery
	 * @return
	 */
	QLStruct VALUES(QLStatement insertSubQuery);

	QLStruct VALUES(Function<QLStruct, QLStatement> function);

}
