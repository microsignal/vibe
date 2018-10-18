package io.microvibe.booster.core.base.search.tools;

import io.microvibe.booster.core.api.model.Data;
import io.microvibe.booster.core.api.model.SearchModel;
import io.microvibe.booster.core.api.model.SortModel;
import io.microvibe.booster.core.search.ISymbol;
import io.microvibe.booster.core.base.search.builder.QL;
import io.microvibe.booster.core.base.search.builder.QLStatement;
import io.microvibe.booster.core.base.search.builder.QLStruct;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("rawtypes")
public class QLStatements {

	private static SearchKeyExtracter<?> defaultSearchKeyExtracter = SearchKeyExtracter.config();
	private static SortKeyExtracter<?> defaultSortKeyExtracter = SortKeyExtracter.config();

	// region 简单查询建构方法

	/**
	 * 简单的单表查询, 按 SELECT_BY_ENTITY ... FROM ... -> WHERE -> ORDER BY 事件顺序, 生成SQL查询语句
	 *
	 * @param apiData     查询接口参数
	 * @param entityClass 单表实体
	 * @return
	 */
	public static <E> QLStatement buildSimpleQLStatement(Data apiData, Class<E> entityClass) {
		return buildSimpleQLStatement(apiData, entityClass, defaultSearchKeyExtracter, defaultSortKeyExtracter);
	}

	/**
	 * 简单的单表查询, 按 SELECT_BY_ENTITY ... FROM ... -> WHERE -> ORDER BY 事件顺序, 生成SQL查询语句
	 *
	 * @param apiData            查询接口参数
	 * @param entityClass        单表实体
	 * @param searchKeyExtracter 查询条件提取配置
	 * @return
	 */
	public static <E> QLStatement buildSimpleQLStatement(Data apiData, Class<E> entityClass,
														 SearchKeyExtracter searchKeyExtracter) {
		return buildSimpleQLStatement(apiData, entityClass, searchKeyExtracter, defaultSortKeyExtracter);
	}

	/**
	 * 简单的单表查询, 按 SELECT_BY_ENTITY ... FROM ... -> WHERE -> ORDER BY 事件顺序, 生成SQL查询语句
	 *
	 * @param apiData          查询接口参数
	 * @param entityClass      单表实体
	 * @param sortKeyExtracter 排序条件提取配置
	 * @return
	 */
	public static <E> QLStatement buildSimpleQLStatement(Data apiData, Class<E> entityClass,
														 SortKeyExtracter sortKeyExtracter) {
		return buildSimpleQLStatement(apiData, entityClass, defaultSearchKeyExtracter, sortKeyExtracter);
	}

	/**
	 * 简单的单表查询, 按 SELECT_BY_ENTITY ... FROM ... -> WHERE -> ORDER BY 事件顺序, 生成SQL查询语句
	 *
	 * @param apiData            查询接口参数
	 * @param entityClass        单表实体
	 * @param searchKeyExtracter 查询条件提取配置
	 * @param sortKeyExtracter   排序条件提取配置
	 * @return
	 */
	public static <E> QLStatement buildSimpleQLStatement(Data apiData, Class<E> entityClass,
														 SearchKeyExtracter searchKeyExtracter, SortKeyExtracter sortKeyExtracter) {
		return buildQLStatement(apiData, data -> QL.FROM(entityClass),
			data -> appendWhereClause(data, searchKeyExtracter), (Consumer<Data>) null,
			data -> appendOrderByClause(data, sortKeyExtracter));
	}

	/**
	 * 简单的单表查询, 按 SELECT_BY_ENTITY ... FROM ... -> WHERE -> ORDER BY 事件顺序, 生成SQL查询语句
	 *
	 * @param apiData            查询接口参数
	 * @param entityClass        单表实体
	 * @param searchKeyExtracter 查询条件提取配置
	 * @param sortKeyExtracter   排序条件提取配置
	 * @param appendOrderByEvent ORDER BY 事件
	 * @return
	 */
	public static <E> QLStatement buildSimpleQLStatement(Data apiData, Class<E> entityClass,
														 SearchKeyExtracter searchKeyExtracter,
														 SortKeyExtracter sortKeyExtracter, BiConsumer<Data, SortKeyExtracter> appendOrderByEvent) {
		return buildQLStatement(apiData, data -> QL.FROM(entityClass),
			data -> appendWhereClause(data, searchKeyExtracter), (Consumer<Data>) null,
			data -> appendOrderByEvent.accept(data, sortKeyExtracter));
	}

	/**
	 * 简单的单表查询, 按 SELECT_BY_ENTITY ... FROM ... -> WHERE -> ORDER BY 事件顺序, 生成SQL查询语句
	 *
	 * @param apiData            查询接口参数
	 * @param entityClass        单表实体
	 * @param appendWhereEvent   WHERE事件
	 * @param searchKeyExtracter 查询条件提取配置
	 * @param sortKeyExtracter   排序条件提取配置
	 * @return
	 */
	public static <E> QLStatement buildSimpleQLStatement(Data apiData, Class<E> entityClass,
														 SearchKeyExtracter searchKeyExtracter, BiConsumer<Data, SearchKeyExtracter> appendWhereEvent,
														 SortKeyExtracter sortKeyExtracter) {
		return buildQLStatement(apiData, data -> QL.FROM(entityClass),
			data -> appendWhereEvent.accept(data, searchKeyExtracter), (Consumer<Data>) null,
			data -> appendOrderByClause(data, sortKeyExtracter));
	}

	/**
	 * 简单的单表查询, 按 SELECT_BY_ENTITY ... FROM ... -> WHERE -> ORDER BY 事件顺序, 生成SQL查询语句
	 *
	 * @param apiData            查询接口参数
	 * @param entityClass        单表实体
	 * @param appendWhereEvent   WHERE事件
	 * @param appendOrderByEvent ORDER BY 事件
	 * @param searchKeyExtracter 查询条件提取配置
	 * @param sortKeyExtracter   排序条件提取配置
	 * @return
	 */
	public static <E> QLStatement buildSimpleQLStatement(Data apiData, Class<E> entityClass,
														 SearchKeyExtracter searchKeyExtracter, BiConsumer<Data, SearchKeyExtracter> appendWhereEvent,
														 SortKeyExtracter sortKeyExtracter, BiConsumer<Data, SortKeyExtracter> appendOrderByEvent) {
		return buildQLStatement(apiData, data -> QL.FROM(entityClass),
			data -> appendWhereEvent.accept(data, searchKeyExtracter), (Consumer<Data>) null,
			data -> appendOrderByEvent.accept(data, sortKeyExtracter));
	}

	/**
	 * 简单的单表查询, 按 SELECT_BY_ENTITY ... FROM ... -> WHERE -> ORDER BY 事件顺序, 生成SQL查询语句
	 *
	 * @param apiData            查询接口参数
	 * @param entityClass        单表实体
	 * @param appendWhereEvent   WHERE事件
	 * @param appendOrderByEvent ORDER BY 事件
	 * @return
	 */
	public static <E> QLStatement buildSimpleQLStatement(Data apiData, Class<E> entityClass,
														 Consumer<Data> appendWhereEvent, Consumer<Data> appendOrderByEvent) {
		return buildQLStatement(apiData, data -> QL.FROM(entityClass), appendWhereEvent, (Consumer<Data>) null,
			appendOrderByEvent);
	}

	// endregion 简单查询建构方法

	// region 复杂查询建构方法

	/**
	 * 按 code>SELECT_BY_ENTITY ... FROM ... -> WHERE ...</code> 事件顺序,
	 * 生成SQL查询语句
	 *
	 * @param apiData          查询接口参数
	 * @param fromEvent        FROM事件
	 * @param appendWhereEvent WHERE事件
	 * @return
	 */
	public static <E> QLStatement buildQLStatement(Data apiData, Consumer<Data> fromEvent,
												   Consumer<Data> appendWhereEvent) {
		return buildQLStatement(apiData, fromEvent, appendWhereEvent, (Consumer<Data>) null,
			(Consumer<Data>) null);
	}

	/**
	 * 按 code>SELECT_BY_ENTITY ... FROM ... -> WHERE ... -> ORDER BY ... </code> 事件顺序,
	 * 生成SQL查询语句
	 *
	 * @param apiData            查询接口参数
	 * @param fromEvent          FROM事件
	 * @param appendWhereEvent   WHERE事件
	 * @param appendOrderByEvent ORDER BY 事件
	 * @return
	 */
	public static <E> QLStatement buildQLStatement(Data apiData, Consumer<Data> fromEvent,
												   Consumer<Data> appendWhereEvent, Consumer<Data> appendOrderByEvent) {
		return buildQLStatement(apiData, fromEvent, appendWhereEvent, (Consumer<Data>) null, appendOrderByEvent);
	}

	/**
	 * 按 code>SELECT_BY_ENTITY ... FROM ... -> WHERE ... -> GROUP BY ... HAVING ... -> ORDER BY ... </code> 事件顺序,
	 * 生成SQL查询语句
	 *
	 * @param apiData                  查询接口参数
	 * @param fromEvent                FROM事件
	 * @param appendWhereEvent         WHERE事件
	 * @param appendGroupByHavingEvent GROUP BY / HAVING 事件
	 * @param appendOrderByEvent       ORDER BY 事件
	 * @return
	 */
	public static <E> QLStatement buildQLStatement(Data apiData, Consumer<Data> fromEvent,
												   Consumer<Data> appendWhereEvent, Consumer<Data> appendGroupByHavingEvent,
												   Consumer<Data> appendOrderByEvent) {
		return buildQLStatement(() -> {
			if (fromEvent != null) {
				fromEvent.accept(apiData);
			}
			if (appendWhereEvent != null) {
				appendWhereEvent.accept(apiData);
			}
			if (appendGroupByHavingEvent != null) {
				appendGroupByHavingEvent.accept(apiData);
			}
			if (appendOrderByEvent != null) {
				appendOrderByEvent.accept(apiData);
			}
			return QL.build();
		});
	}

	/**
	 * 执行工厂方法生成SQL查询语句
	 *
	 * @param apiData      查询接口参数
	 * @param buildFactory 工厂
	 * @return
	 */
	public static <E> QLStatement buildQLStatement(Data apiData, Function<Data, QLStatement> buildFactory) {
		return buildFactory.apply(apiData);
	}

	/**
	 * 执行工厂方法生成SQL查询语句
	 *
	 * @param buildFactory 工厂
	 * @return
	 */
	public static <E> QLStatement buildQLStatement(Supplier<QLStatement> buildFactory) {
		return buildFactory.get();
	}

	// endregion 复杂查询建构方法

	/**
	 * 在当前SQL构建器中追加 WHERE 条件子句
	 *
	 * @param apiData 查询接口参数
	 */
	public static void appendWhereClause(Data apiData) {
		appendWhereClause(apiData, null);
	}

	/**
	 * 在当前SQL构建器中追加 WHERE 条件子句
	 *
	 * @param apiData            查询接口参数
	 * @param searchKeyExtracter 查询条件提取配置
	 */
	public static void appendWhereClause(Data apiData, SearchKeyExtracter searchKeyExtracter) {
		appendWhereClause(QL.struct(), apiData, searchKeyExtracter);
	}

	public static void appendWhereClause(QLStruct struct, Data apiData, SearchKeyExtracter searchKeyExtracter) {
		if (searchKeyExtracter == null) {
			searchKeyExtracter = defaultSearchKeyExtracter;
		}
		List<SearchModel> searchModelList = apiData.getBody().getSearches();
		for (SearchModel model : searchModelList) {
			String key = model.getKey();
			if (searchKeyExtracter.isIncluded(key) && !searchKeyExtracter.isExcluded(key)) {
				key = searchKeyExtracter.getMappingKey(key);
				ISymbol op = model.getOp();
				if (op.isNoValue()) {
					struct.WHERE(key, op);
				} else {
					Object val = model.getVal();
					if (val != null && !(val instanceof String && "".equals(val = ((String) val).trim()))) {
						struct.WHERE(key, op, val);
					}
				}
			}
		}
	}

	/**
	 * 在当前SQL构建器中追加 ORDER BY 条件子句
	 *
	 * @param apiData 查询接口参数
	 */
	public static void appendOrderByClause(Data apiData) {
		appendOrderByClause(apiData, null);
	}

	/**
	 * 在当前SQL构建器中追加 ORDER BY 条件子句
	 *
	 * @param apiData          查询接口参数
	 * @param sortKeyExtracter 排序条件提取配置
	 */
	public static void appendOrderByClause(Data apiData, SortKeyExtracter sortKeyExtracter) {
		appendOrderByClause(QL.struct(), apiData, sortKeyExtracter);
	}

	public static void appendOrderByClause(QLStruct struct, Data apiData, SortKeyExtracter sortKeyExtracter) {
		if (sortKeyExtracter == null) {
			sortKeyExtracter = defaultSortKeyExtracter;
		}
		List<SortModel> sortModelList = apiData.getBody().getSorts();
		for (SortModel model : sortModelList) {
			String key = model.getSortKey();
			if (sortKeyExtracter.isIncluded(key) && !sortKeyExtracter.isExcluded(key)) {
				key = sortKeyExtracter.getMappingKey(key);
				struct.ORDER_BY(key + " " + model.getSortDirection().name().toLowerCase());
			}
		}
	}

	public static Consumer<Data> simpleAppendWhereEvent() {
		return data -> appendWhereClause(data);
	}

	public static Consumer<Data> simpleAppendWhereEvent(SearchKeyExtracter searchKeyExtracter) {
		return data -> appendWhereClause(data, searchKeyExtracter);
	}

	public static Consumer<Data> simpleAppendOrderByEvent() {
		return data -> appendOrderByClause(data);
	}

	public static Consumer<Data> simpleAppendOrderByEvent(SortKeyExtracter sortKeyExtracter) {
		return data -> appendOrderByClause(data, sortKeyExtracter);
	}

}
