package io.microvibe.booster.core.base.service;

import io.microvibe.booster.commons.spring.ApplicationContextHolder;
import io.microvibe.booster.core.api.model.Data;
import io.microvibe.booster.core.base.search.builder.QLStatement;
import io.microvibe.booster.core.base.search.tools.SearchKeyExtracter;
import io.microvibe.booster.core.base.search.tools.SortKeyExtracter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SharedJpaServices {
	/**
	 * 获取 {@linkplain SharedJpaService} 实例
	 *
	 * @return
	 */
	public static SharedJpaService getService() {
		return ApplicationContextHolder.getBean(SharedJpaService.class);
	}

	public static <E> Page<E> findPage(Data apiData, Class<E> entityClass) {
		return getService().findPage(apiData, entityClass);
	}

	public static <E> Page<E> findPage(Data apiData, Class<E> entityClass, SearchKeyExtracter searchKeyExtracter) {
		return getService().findPage(apiData, entityClass, searchKeyExtracter);
	}

	public static <E> Page<E> findPageWithoutSort(Data apiData, Class<E> entityClass) {
		return getService().findPageWithoutSort(apiData, entityClass);
	}

	public static <E> Page<E> findPageWithoutSort(Data apiData, Class<E> entityClass,
												  SearchKeyExtracter searchKeyExtracter) {
		return getService().findPageWithoutSort(apiData, entityClass, searchKeyExtracter);
	}

	public static <E> Page<E> findPage(Data apiData, Class<E> entityClass, SearchKeyExtracter searchKeyExtracter,
									   SortKeyExtracter sortKeyExtracter) {
		return getService().findPage(apiData, entityClass, searchKeyExtracter, sortKeyExtracter);
	}

	public static <E> Long findCount(Data apiData, Class<E> entityClass) {
		return getService().findCount(apiData, entityClass);
	}

	public static <E> Long findCount(Data apiData, Class<E> entityClass, SearchKeyExtracter searchKeyExtracter) {
		return getService().findCount(apiData, entityClass, searchKeyExtracter);
	}

	public static <E> List<E> findTopListWithoutSort(Data apiData, Class<E> entityClass, int fetchSize) {
		return getService().findTopListWithoutSort(apiData, entityClass, fetchSize);
	}

	public static <E> List<E> findTopListWithoutSort(Data apiData, Class<E> entityClass,
													 SearchKeyExtracter searchKeyExtracter, int fetchSize) {
		return getService().findTopListWithoutSort(apiData, entityClass, searchKeyExtracter, fetchSize);
	}

	public static <E> List<E> findTopList(Data apiData, Class<E> entityClass, int fetchSize) {
		return getService().findTopList(apiData, entityClass, fetchSize);
	}

	public static <E> List<E> findTopList(Data apiData, Class<E> entityClass, SearchKeyExtracter searchKeyExtracter,
										  int fetchSize) {
		return getService().findTopList(apiData, entityClass, searchKeyExtracter, fetchSize);
	}

	public static <E> List<E> findTopList(Data apiData, Class<E> entityClass, SortKeyExtracter sortKeyExtracter,
										  int fetchSize) {
		return getService().findTopList(apiData, entityClass, sortKeyExtracter, fetchSize);
	}

	public static <E> List<E> findTopList(Data apiData, Class<E> entityClass, SearchKeyExtracter searchKeyExtracter,
										  SortKeyExtracter sortKeyExtracter, int fetchSize) {
		return getService().findTopList(apiData, entityClass, searchKeyExtracter, sortKeyExtracter,
			fetchSize);
	}

	public static <E> List<E> findList(Data apiData, Class<E> entityClass) {
		return getService().findList(apiData, entityClass);
	}

	public static <E> List<E> findList(Data apiData, Class<E> entityClass, SearchKeyExtracter searchKeyExtracter) {
		return getService().findList(apiData, entityClass, searchKeyExtracter);
	}

	public static <E> List<E> findListWithoutSort(Data apiData, Class<E> entityClass) {
		return getService().findListWithoutSort(apiData, entityClass);
	}

	public static <E> List<E> findListWithoutSort(Data apiData, Class<E> entityClass,
												  SearchKeyExtracter searchKeyExtracter) {
		return getService().findListWithoutSort(apiData, entityClass, searchKeyExtracter);
	}

	public static <E> List<E> findList(Data apiData, Class<E> entityClass, SearchKeyExtracter searchKeyExtracter,
									   SortKeyExtracter sortKeyExtracter) {
		return getService().findList(apiData, entityClass, searchKeyExtracter, sortKeyExtracter);
	}

	public static <E> List<E> findAll(Data apiData, Class<E> entityClass) {
		return getService().findAll(apiData, entityClass);
	}

	public static <E> List<E> findAll(Data apiData, Class<E> entityClass, SearchKeyExtracter searchKeyExtracter) {
		return getService().findAll(apiData, entityClass, searchKeyExtracter);
	}

	public static <E> List<E> findAllWithoutSort(Data apiData, Class<E> entityClass) {
		return getService().findAllWithoutSort(apiData, entityClass);
	}

	public static <E> List<E> findAllWithoutSort(Data apiData, Class<E> entityClass,
												 SearchKeyExtracter searchKeyExtracter) {
		return getService().findAllWithoutSort(apiData, entityClass, searchKeyExtracter);
	}

	public static <E> List<E> findAll(Data apiData, Class<E> entityClass, SearchKeyExtracter searchKeyExtracter,
									  SortKeyExtracter sortKeyExtracter) {
		return getService().findAll(apiData, entityClass, searchKeyExtracter, sortKeyExtracter);
	}

	public static Object findOne(QLStatement st) {
		return getService().findOne(st);
	}

	public static Object findOne(Supplier<QLStatement> buildFactory) {
		return getService().findOne(buildFactory);
	}

	public static Object findOne(Data apiData, Function<Data, QLStatement> buildFactory) {
		return getService().findOne(apiData, buildFactory);
	}

	public static Object findOne(Data apiData, Consumer<Data> fromEvent, Consumer<Data> appendWhereEvent,
								 Consumer<Data> appendGroupByHavingEvent, Consumer<Data> appendOrderByEvent) {
		return getService().findOne(apiData, fromEvent, appendWhereEvent, appendGroupByHavingEvent,
			appendOrderByEvent);
	}

	public static Page<?> findPage(QLStatement st, Pageable pageable) {
		return getService().findPage(st, pageable);
	}

	public static Page<?> findPage(QLStatement st, int fetchSize) {
		return getService().findPage(st, fetchSize);
	}

	public static Page<?> findPage(QLStatement st, int offset, int fetchSize) {
		return getService().findPage(st, offset, fetchSize);
	}

	public static Page<?> findPage(Data apiData, Function<Data, QLStatement> buildFactory) {
		return getService().findPage(apiData, buildFactory);
	}

	public static Page<?> findPage(Data apiData, Consumer<Data> fromEvent, Consumer<Data> appendWhereEvent,
								   Consumer<Data> appendGroupByHavingEvent, Consumer<Data> appendOrderByEvent) {
		return getService().findPage(apiData, fromEvent, appendWhereEvent, appendGroupByHavingEvent,
			appendOrderByEvent);
	}

	public static List<?> findList(QLStatement st) {
		return getService().findList(st);
	}

	public static List<?> findList(QLStatement st, int fetchSize) {
		return getService().findList(st, fetchSize);
	}

	public static List<?> findList(QLStatement st, Pageable pageable) {
		return getService().findList(st, pageable);
	}

	public static List<?> findList(QLStatement st, int offset, int fetchSize) {
		return getService().findList(st, offset, fetchSize);
	}

	public static List<?> findList(Supplier<QLStatement> buildFactory) {
		return getService().findList(buildFactory);
	}

	public static List<?> findList(Data apiData, Function<Data, QLStatement> buildFactory) {
		return getService().findList(apiData, buildFactory);
	}

	public static List<?> findList(Data apiData, Consumer<Data> fromEvent, Consumer<Data> appendWhereEvent,
								   Consumer<Data> appendGroupByHavingEvent, Consumer<Data> appendOrderByEvent) {
		return getService().findList(apiData, fromEvent, appendWhereEvent, appendGroupByHavingEvent,
			appendOrderByEvent);
	}

	public static List<?> findAll(Data apiData, Function<Data, QLStatement> buildFactory) {
		return getService().findAll(apiData, buildFactory);
	}

	public static List<?> findAll(Data apiData, Consumer<Data> fromEvent, Consumer<Data> appendWhereEvent,
								  Consumer<Data> appendGroupByHavingEvent, Consumer<Data> appendOrderByEvent) {
		return getService().findAll(apiData, fromEvent, appendWhereEvent, appendGroupByHavingEvent,
			appendOrderByEvent);
	}

	public static int update(QLStatement st) {
		return getService().update(st);
	}

	public static int update(Supplier<QLStatement> buildFactory) {
		return getService().update(buildFactory);
	}

	public static <T> T find(Callable<T> callable) {
		return getService().find(callable);
	}

	public static <T> T executeAndGet(Callable<T> callable) {
		return getService().executeAndGet(callable);
	}

	public static void execute(Executeable executeable) {
		getService().execute(executeable);
	}

	public static <T extends Persistable<ID>, ID extends Serializable> T findOne(Class<T> entityClass, ID primaryKey) {
		return getService().findOne(entityClass, primaryKey);
	}

	public static <T extends Persistable<ID>, ID extends Serializable> T save(T entity) {
		return getService().save(entity);
	}

	public static <T extends Persistable<ID>, ID extends Serializable> T saveAndFlush(T entity) {
		return getService().saveAndFlush(entity);
	}

	public static <T extends Persistable<ID>, ID extends Serializable> int delete(Class<T> entityClass, ID primaryKey) {
		return getService().delete(entityClass, primaryKey);
	}

	public static <T extends Persistable<ID>, ID extends Serializable> int[] delete(Class<T> entityClass,
																					ID[] primaryKeys) {
		return getService().delete(entityClass, primaryKeys);
	}

	public static <T extends Persistable<ID>, ID extends Serializable> void delete(T entity) {
		getService().delete(entity);
	}

	public static <T extends Persistable<ID>, ID extends Serializable> void delete(T[] entities) {
		getService().delete(entities);
	}

	public static <T extends Persistable<ID>, ID extends Serializable> void delete(Iterable<T> entities) {
		getService().delete(entities);
	}

	public static <T extends Persistable<ID>, ID extends Serializable> void refresh(T entity) {
		getService().refresh(entity);
	}

	public static void flush() {
		getService().flush();
	}

}
