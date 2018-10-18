package io.microvibe.booster.core.base.service;

import io.microvibe.booster.core.api.model.Data;
import io.microvibe.booster.core.api.model.BodyModel;
import io.microvibe.booster.core.base.repository.support.WrappedPageImpl;
import io.microvibe.booster.core.base.search.builder.QLParameter;
import io.microvibe.booster.core.base.search.builder.QLStatement;
import io.microvibe.booster.core.base.search.tools.SearchKeyExtracter;
import io.microvibe.booster.core.base.search.tools.SortKeyExtracter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Persistable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.microvibe.booster.core.base.search.tools.QLStatements.buildQLStatement;
import static io.microvibe.booster.core.base.search.tools.QLStatements.buildSimpleQLStatement;

/**
 * 通用的业务逻辑服务类
 *
 * @author Qt
 * @version 1.0.1
 * @since Mar 27, 2018
 */
@Service
@Slf4j
public class SharedJpaService implements InitializingBean {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public void afterPropertiesSet() throws Exception {
	}

	private SortKeyExtracter defaultNoGreedySortKeyExtracter = SortKeyExtracter.config().defaultIncluded(false);

	public <E> Page<E> findPage(Data apiData, Class<E> entityClass) {
		return findPage(apiData, entityClass, null, null);
	}

	public <E> Page<E> findPage(Data apiData, Class<E> entityClass, SearchKeyExtracter searchKeyExtracter) {
		return findPage(apiData, entityClass, searchKeyExtracter, null);
	}

	public <E> Page<E> findPageWithoutSort(Data apiData, Class<E> entityClass) {
		return findPage(apiData, entityClass, null, defaultNoGreedySortKeyExtracter);
	}

	public <E> Page<E> findPageWithoutSort(Data apiData, Class<E> entityClass,
										   SearchKeyExtracter searchKeyExtracter) {
		return findPage(apiData, entityClass, searchKeyExtracter, defaultNoGreedySortKeyExtracter);
	}

	public <E> Page<E> findPage(Data apiData, Class<E> entityClass, SearchKeyExtracter searchKeyExtracter,
								SortKeyExtracter sortKeyExtracter) {
		BodyModel data = apiData.getBody();
		Pageable pageable = data.getPageable();
		QLStatement st = buildSimpleQLStatement(apiData, entityClass, searchKeyExtracter,
			sortKeyExtracter);
		log.info("QLStatement: {}", st);
		Query query;
		if (st.isNative()) {
			query = entityManager.createNativeQuery(st.statement());
		} else {
			query = entityManager.createQuery(st.statement());
		}
		Query countQuery;
		if (st.isNative()) {
			countQuery = entityManager.createNativeQuery(st.countStatement());
		} else {
			countQuery = entityManager.createQuery(st.countStatement());
		}
		List<QLParameter> bindedValues = st.bindedValues();
		bindedValues.forEach(p -> {
			query.setParameter(p.getKey(), p.getValue());
			countQuery.setParameter(p.getKey(), p.getValue());
		});
		if (st.isNative()) {
			query.unwrap(org.hibernate.Query.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		}
		int offset = pageable.getOffset();
		int fetchSize = pageable.getPageSize();
		if (offset > 0) {
			query.setFirstResult(offset);
		}
		if (fetchSize > 0) {
			query.setMaxResults(fetchSize);
		}
		@SuppressWarnings("unchecked")
		List<E> resultList = query.getResultList();
		Long total = (Long) countQuery.getSingleResult();

		Page<E> page = new WrappedPageImpl<>(resultList, pageable, total);
		return page;
	}

	public <E> Long findCount(Data apiData, Class<E> entityClass) {
		return findCount(apiData, entityClass, null);
	}

	public <E> Long findCount(Data apiData, Class<E> entityClass, SearchKeyExtracter searchKeyExtracter) {
		QLStatement st = buildSimpleQLStatement(apiData, entityClass, searchKeyExtracter,
			defaultNoGreedySortKeyExtracter);
		log.info("QLStatement: {}", st);
		Query countQuery;
		if (st.isNative()) {
			countQuery = entityManager.createNativeQuery(st.countStatement());
		} else {
			countQuery = entityManager.createQuery(st.countStatement());
		}
		List<QLParameter> bindedValues = st.bindedValues();
		bindedValues.forEach(p -> {
			countQuery.setParameter(p.getKey(), p.getValue());
		});
		Long total = (Long) countQuery.getSingleResult();
		return total;
	}

	public <E> List<E> findTopListWithoutSort(Data apiData, Class<E> entityClass, int fetchSize) {
		return findLimitedList(apiData, entityClass, null, defaultNoGreedySortKeyExtracter, 0, fetchSize);
	}

	public <E> List<E> findTopListWithoutSort(Data apiData, Class<E> entityClass,
											  SearchKeyExtracter searchKeyExtracter, int fetchSize) {
		return findLimitedList(apiData, entityClass, searchKeyExtracter, defaultNoGreedySortKeyExtracter, 0, fetchSize);
	}

	public <E> List<E> findTopList(Data apiData, Class<E> entityClass, int fetchSize) {
		return findLimitedList(apiData, entityClass, null, null, 0, fetchSize);
	}

	public <E> List<E> findTopList(Data apiData, Class<E> entityClass, SearchKeyExtracter searchKeyExtracter,
								   int fetchSize) {
		return findLimitedList(apiData, entityClass, searchKeyExtracter, null, 0, fetchSize);
	}

	public <E> List<E> findTopList(Data apiData, Class<E> entityClass,
								   SortKeyExtracter sortKeyExtracter, int fetchSize) {
		return findLimitedList(apiData, entityClass, null, sortKeyExtracter, 0, fetchSize);
	}

	public <E> List<E> findTopList(Data apiData, Class<E> entityClass, SearchKeyExtracter searchKeyExtracter,
								   SortKeyExtracter sortKeyExtracter, int fetchSize) {
		return findLimitedList(apiData, entityClass, searchKeyExtracter, sortKeyExtracter, 0, fetchSize);
	}

	public <E> List<E> findList(Data apiData, Class<E> entityClass) {
		return findList(apiData, entityClass, null, null);
	}

	public <E> List<E> findList(Data apiData, Class<E> entityClass, SearchKeyExtracter searchKeyExtracter) {
		return findList(apiData, entityClass, searchKeyExtracter, null);
	}

	public <E> List<E> findListWithoutSort(Data apiData, Class<E> entityClass) {
		return findList(apiData, entityClass, null, defaultNoGreedySortKeyExtracter);
	}

	public <E> List<E> findListWithoutSort(Data apiData, Class<E> entityClass,
										   SearchKeyExtracter searchKeyExtracter) {
		return findList(apiData, entityClass, searchKeyExtracter, defaultNoGreedySortKeyExtracter);
	}

	public <E> List<E> findList(Data apiData, Class<E> entityClass, SearchKeyExtracter searchKeyExtracter,
								SortKeyExtracter sortKeyExtracter) {
		Pageable pageable = apiData.getBody().getPageable();
		if (pageable == null) {
			return findAll(apiData, entityClass, searchKeyExtracter, sortKeyExtracter);
		} else {
			return findLimitedList(apiData, entityClass, searchKeyExtracter, sortKeyExtracter, pageable.getOffset(),
				pageable.getPageSize());
		}
	}

	private <E> List<E> findLimitedList(Data apiData, Class<E> entityClass, SearchKeyExtracter searchKeyExtracter,
										SortKeyExtracter sortKeyExtracter, int offset, int fetchSize) {
		QLStatement st = buildSimpleQLStatement(apiData, entityClass, searchKeyExtracter,
			sortKeyExtracter);
		log.info("QLStatement: {}", st);
		Query query;
		if (st.isNative()) {
			query = entityManager.createNativeQuery(st.statement());
		} else {
			query = entityManager.createQuery(st.statement());
		}
		List<QLParameter> bindedValues = st.bindedValues();
		bindedValues.forEach(p -> {
			query.setParameter(p.getKey(), p.getValue());
		});
		if (st.isNative()) {
			query.unwrap(org.hibernate.Query.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		}
		if (offset > 0) {
			query.setFirstResult(offset);
		}
		if (fetchSize > 0) {
			query.setMaxResults(fetchSize);
		}
		@SuppressWarnings("unchecked")
		List<E> resultList = query.getResultList();
		return resultList;
	}

	public <E> List<E> findAll(Data apiData, Class<E> entityClass) {
		return findAll(apiData, entityClass, null, null);
	}

	public <E> List<E> findAll(Data apiData, Class<E> entityClass, SearchKeyExtracter searchKeyExtracter) {
		return findAll(apiData, entityClass, searchKeyExtracter, null);
	}

	public <E> List<E> findAllWithoutSort(Data apiData, Class<E> entityClass) {
		return findAll(apiData, entityClass, null, defaultNoGreedySortKeyExtracter);
	}

	public <E> List<E> findAllWithoutSort(Data apiData, Class<E> entityClass,
										  SearchKeyExtracter searchKeyExtracter) {
		return findAll(apiData, entityClass, searchKeyExtracter, defaultNoGreedySortKeyExtracter);
	}

	public <E> List<E> findAll(Data apiData, Class<E> entityClass, SearchKeyExtracter searchKeyExtracter,
							   SortKeyExtracter sortKeyExtracter) {
		QLStatement st = buildSimpleQLStatement(apiData, entityClass, searchKeyExtracter,
			sortKeyExtracter);
		log.info("QLStatement: {}", st);

		Query query;
		if (st.isNative()) {
			query = entityManager.createNativeQuery(st.statement());
		} else {
			query = entityManager.createQuery(st.statement());
		}
		List<QLParameter> bindedValues = st.bindedValues();
		bindedValues.forEach(p -> {
			query.setParameter(p.getKey(), p.getValue());
		});
		if (st.isNative()) {
			query.unwrap(org.hibernate.Query.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		}
		@SuppressWarnings("unchecked")
		List<E> resultList = query.getResultList();
		return resultList;
	}

	public Object findOne(QLStatement st) {
		log.info("QLStatement: {}", st);
		Query query;
		if (st.isNative()) {
			query = entityManager.createNativeQuery(st.statement());
		} else {
			query = entityManager.createQuery(st.statement());
		}
		List<QLParameter> bindedValues = st.bindedValues();
		bindedValues.forEach(p -> {
			query.setParameter(p.getKey(), p.getValue());
		});
		if (st.isNative()) {
			query.unwrap(org.hibernate.Query.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		}
		return query.getSingleResult();
	}

	public Object findOne(Supplier<QLStatement> buildFactory) {
		return findOne(buildFactory.get());
	}

	public Object findOne(Data apiData, Function<Data, QLStatement> buildFactory) {
		return findOne(buildFactory.apply(apiData));
	}

	public Object findOne(Data apiData, Consumer<Data> fromEvent,
						  Consumer<Data> appendWhereEvent, Consumer<Data> appendGroupByHavingEvent,
						  Consumer<Data> appendOrderByEvent) {
		QLStatement st = buildQLStatement(apiData, fromEvent, appendWhereEvent,
			appendGroupByHavingEvent, appendOrderByEvent);
		return findOne(st);
	}

	public Page<?> findPage(QLStatement st, Pageable pageable) {
		log.info("QLStatement: {}", st);
		return findPage(st, pageable.getOffset(), pageable.getPageSize());
	}

	public Page<?> findPage(QLStatement st, int fetchSize) {
		return findPage(st, 0, fetchSize);
	}

	public Page<?> findPage(QLStatement st, int offset, int fetchSize) {
		Query query;
		if (st.isNative()) {
			query = entityManager.createNativeQuery(st.statement());
		} else {
			query = entityManager.createQuery(st.statement());
		}
		Query countQuery;
		if (st.isNative()) {
			countQuery = entityManager.createNativeQuery(st.countStatement());
		} else {
			countQuery = entityManager.createQuery(st.countStatement());
		}

		List<QLParameter> bindedValues = st.bindedValues();
		bindedValues.forEach(p -> {
			query.setParameter(p.getKey(), p.getValue());
			countQuery.setParameter(p.getKey(), p.getValue());
		});

		if (st.isNative()) {
			query.unwrap(org.hibernate.Query.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		}
		if (offset > 0) {
			query.setFirstResult(offset);
		}
		if (fetchSize > 0) {
			query.setMaxResults(fetchSize);
		}

		List<?> resultList = query.getResultList();
		Long total = (Long) countQuery.getSingleResult();

		Page<?> page = new WrappedPageImpl<>(resultList, new PageRequest(offset / fetchSize, fetchSize), total);
		return page;
	}

	public Page<?> findPage(Data apiData, Function<Data, QLStatement> buildFactory) {
		return findPage(buildFactory.apply(apiData), apiData.getBody().getPageable());
	}

	public Page<?> findPage(Data apiData, Consumer<Data> fromEvent,
							Consumer<Data> appendWhereEvent, Consumer<Data> appendGroupByHavingEvent,
							Consumer<Data> appendOrderByEvent) {
		QLStatement st = buildQLStatement(apiData, fromEvent, appendWhereEvent,
			appendGroupByHavingEvent, appendOrderByEvent);
		return findPage(st, apiData.getBody().getPageable());
	}

	public List<?> findList(QLStatement st) {
		return findList(st, 0, 0);
	}

	public List<?> findList(QLStatement st, int fetchSize) {
		return findList(st, 0, fetchSize);
	}

	public List<?> findList(QLStatement st, Pageable pageable) {
		if (pageable == null) {
			return findList(st);
		} else {
			return findList(st, pageable.getOffset(), pageable.getPageSize());
		}
	}

	public List<?> findList(QLStatement st, int offset, int fetchSize) {
		Query query;
		if (st.isNative()) {
			query = entityManager.createNativeQuery(st.statement());
		} else {
			query = entityManager.createQuery(st.statement());
		}
		List<QLParameter> bindedValues = st.bindedValues();
		bindedValues.forEach(p -> {
			query.setParameter(p.getKey(), p.getValue());
		});
		if (offset > 0) {
			query.setFirstResult(offset);
		}
		if (fetchSize > 0) {
			query.setMaxResults(fetchSize);
		}
		if (st.isNative()) {
			query.unwrap(org.hibernate.Query.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		}
		return query.getResultList();
	}

	public List<?> findList(Supplier<QLStatement> buildFactory) {
		return findList(buildFactory.get());
	}

	public List<?> findList(Data apiData, Function<Data, QLStatement> buildFactory) {
		return findList(buildFactory.apply(apiData), apiData.getBody().getPageable());
	}

	public List<?> findList(Data apiData, Consumer<Data> fromEvent,
							Consumer<Data> appendWhereEvent, Consumer<Data> appendGroupByHavingEvent,
							Consumer<Data> appendOrderByEvent) {
		QLStatement st = buildQLStatement(apiData, fromEvent, appendWhereEvent,
			appendGroupByHavingEvent, appendOrderByEvent);
		return findList(st, apiData.getBody().getPageable());
	}

	public List<?> findAll(Data apiData, Function<Data, QLStatement> buildFactory) {
		return findList(buildFactory.apply(apiData));
	}

	public List<?> findAll(Data apiData, Consumer<Data> fromEvent,
						   Consumer<Data> appendWhereEvent, Consumer<Data> appendGroupByHavingEvent,
						   Consumer<Data> appendOrderByEvent) {
		QLStatement st = buildQLStatement(apiData, fromEvent, appendWhereEvent,
			appendGroupByHavingEvent, appendOrderByEvent);
		return findList(st);
	}

	public int update(QLStatement st) {
		Query query;
		if (st.isNative()) {
			query = entityManager.createNativeQuery(st.statement());
		} else {
			query = entityManager.createQuery(st.statement());
		}
		List<QLParameter> bindedValues = st.bindedValues();
		bindedValues.forEach(p -> {
			query.setParameter(p.getKey(), p.getValue());
		});
		if (st.isNative()) {
			query.unwrap(org.hibernate.Query.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		}
		return query.executeUpdate();
	}

	public int update(Supplier<QLStatement> buildFactory) {
		return update(buildFactory.get());
	}

	/**
	 * 通过回调接口,查询数据并返回结果
	 *
	 * @param callable
	 * @return
	 */
	public <T> T find(Callable<T> callable) {
		return callable.call(entityManager);
	}

	/**
	 * 通过回调接口,执行事务操作
	 *
	 * @param callable
	 * @return
	 */
	public <T> T executeAndGet(Callable<T> callable) {
		return callable.call(entityManager);
	}

	/**
	 * 通过回调接口,执行事务操作并返回结果
	 *
	 * @param executeable
	 */
	public void execute(Executeable executeable) {
		executeable.execute(entityManager);
	}

	public <T extends Persistable<ID>, ID extends Serializable> T findOne(Class<T> entityClass, ID primaryKey) {
		return entityManager.find(entityClass, primaryKey);
	}

	public <T extends Persistable<ID>, ID extends Serializable> T save(T entity) {
		if (entity.isNew()) {
			entityManager.persist(entity);
			return entity;
		} else {
			return entityManager.merge(entity);
		}
	}

	public <T extends Persistable<ID>, ID extends Serializable> T saveAndFlush(T entity) {
		try {
			return save(entity);
		} finally {
			flush();
		}
	}

	public <T extends Persistable<ID>, ID extends Serializable> int delete(Class<T> entityClass, ID primaryKey) {
		Persistable<ID> entity = findOne(entityClass, primaryKey);
		if (entity != null) {
			entityManager.remove(entity);
			return 1;
		} else {
			return 0;
		}
	}

	public <T extends Persistable<ID>, ID extends Serializable> int[] delete(Class<T> entityClass, ID[] primaryKeys) {
		int[] rs = new int[primaryKeys.length];
		for (int i = 0; i < primaryKeys.length; i++) {
			rs[i] = delete(entityClass, primaryKeys[i]);
		}
		return rs;
	}

	public <T extends Persistable<ID>, ID extends Serializable> void delete(T entity) {
		entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
	}

	public <T extends Persistable<ID>, ID extends Serializable> void delete(T[] entities) {
		for (T entity : entities) {
			delete(entity);
		}
	}

	public <T extends Persistable<ID>, ID extends Serializable> void delete(Iterable<T> entities) {
		for (T entity : entities) {
			delete(entity);
		}
	}

	public <T extends Persistable<ID>, ID extends Serializable> void refresh(T entity) {
		entityManager.refresh(entity);
	}

	public void flush() {
		entityManager.flush();
	}
}
