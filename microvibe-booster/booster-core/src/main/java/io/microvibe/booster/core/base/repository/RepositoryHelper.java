package io.microvibe.booster.core.base.repository;

import io.microvibe.booster.commons.spring.ApplicationContextHolder;
import io.microvibe.booster.core.base.repository.annotation.EnableQueryCache;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.orm.jpa.SharedEntityManagerCreator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.util.List;
import java.util.Map;

/**
 * 仓库辅助类
 *
 * @author Qt
 */
public class RepositoryHelper {

	private static EntityManager entityManager;
	private Class<?> entityClass;
	private boolean enableQueryCache = false;

	/**
	 * @param entityClass 是否开启查询缓存
	 */
	public RepositoryHelper(Class<?> entityClass) {
		this.entityClass = entityClass;

		EnableQueryCache enableQueryCacheAnnotation = AnnotationUtils.findAnnotation(entityClass,
			EnableQueryCache.class);

		boolean enableQueryCache = false;
		if (enableQueryCacheAnnotation != null) {
			enableQueryCache = enableQueryCacheAnnotation.value();
		}
		this.enableQueryCache = enableQueryCache;
	}

//    public static void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
//        entityManager = SharedEntityManagerCreator.createSharedEntityManager(entityManagerFactory);
//    }

	private static void initEntityManager() {
		if (entityManager == null) {
			synchronized (RepositoryHelper.class) {
				if (entityManager == null) {
					Map<String, EntityManager> ems = ApplicationContextHolder.getApplicationContext()
						.getBeansOfType(EntityManager.class);
					if (ems.size() > 0) {
						entityManager = ems.entrySet().iterator().next().getValue();
					} else {
						EntityManagerFactory entityManagerFactory = ApplicationContextHolder
							.getBean(EntityManagerFactory.class);
						entityManager = SharedEntityManagerCreator.createSharedEntityManager(entityManagerFactory);
					}
				}
			}
		}
	}

	public static EntityManager getEntityManager() {
		initEntityManager();
		return entityManager;
	}

	public static void flush() {
		getEntityManager().flush();
	}

	public static void clear() {
		flush();
		getEntityManager().clear();
	}


	/**
	 * @param ql
	 * @param params
	 * @param <M>
	 * @return
	 * @see RepositoryHelper#findAll(String, org.springframework.data.domain.Pageable, Object...)
	 */
	public <M> List<M> findAll(final String ql, final Object... params) {

		// 此处必须 (Pageable) null 否则默认有调用自己了 可变参列表
		return findAll(ql, (Pageable) null, params);

	}

	/**
	 * 根据原生ql
	 *
	 * @param ql
	 * @param params
	 * @param <M>
	 * @return
	 * @see RepositoryHelper#findAll(String, org.springframework.data.domain.Pageable, Object...)
	 */
	public <M> List<M> findAllNativeQuery(final String ql, final Object... params) {
		// 此处必须 (Pageable) null 否则默认有调用自己了 可变参列表
		return findAllNativeQuery(ql, (Pageable) null, params);
	}

	/**
	 * <p>根据ql和按照索引顺序的params执行ql，pageable存储分页信息 null表示不分页<br/>
	 * 具体使用请参考测试用例：{@see io.microvibe.booster.commons.repository.UserRepository2ImplIT#testFindAll()}
	 *
	 * @param ql
	 * @param pageable null表示不分页
	 * @param params
	 * @param <M>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <M> List<M> findAll(final String ql, final Pageable pageable, final Object... params) {
		Query query = getEntityManager().createQuery(ql + prepareOrder(pageable != null ? pageable.getSort() : null));
		applyEnableQueryCache(query);
		setParameters(query, params);
		if (pageable != null) {
			query.setFirstResult(pageable.getOffset());
			query.setMaxResults(pageable.getPageSize());
		}
		return query.getResultList();
	}

	public List<Map<String, Object>> findAllWithMap(final String ql, final Object... params) {
		// 此处必须 (Pageable) null 否则默认有调用自己了 可变参列表
		return findAllWithMap(ql, (Pageable) null, params);
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findAllWithMap(final String ql, final Pageable pageable, final Object... params) {
		Query query = getEntityManager().createQuery(ql + prepareOrder(pageable != null ? pageable.getSort() : null));
		applyEnableQueryCache(query);
		setParameters(query, params);
		if (pageable != null) {
			query.setFirstResult(pageable.getOffset());
			query.setMaxResults(pageable.getPageSize());
		}
		return query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}

	/**
	 * ql为原生ql
	 * <p>根据ql和按照索引顺序的params执行ql，pageable存储分页信息 null表示不分页<br/>
	 * 具体使用请参考测试用例：{@see io.microvibe.booster.commons.repository.UserRepository2ImplIT#testFindAll()}
	 *
	 * @param ql
	 * @param pageable null表示不分页
	 * @param params
	 * @param <M>
	 * @return
	 */
	public <M> List<M> findAllNativeQuery(final String ql, final Pageable pageable, final Object... params) {

		Query query = getEntityManager()
			.createNativeQuery(ql + prepareOrder(pageable != null ? pageable.getSort() : null));
		applyEnableQueryCache(query);
		setParameters(query, params);
		if (pageable != null) {
			query.setFirstResult(pageable.getOffset());
			query.setMaxResults(pageable.getPageSize());
		}

		return query.getResultList();
	}

	public <M> List<M> findAll(final String ql) {

		Query query = getEntityManager().createQuery(ql);
		applyEnableQueryCache(query);
		return query.getResultList();
	}

	/**
	 * <p>根据ql和按照索引顺序的params执行ql，sort存储排序信息 null表示不排序<br/>
	 * 具体使用请参考测试用例：{@see io.microvibe.booster.commons.repository.UserRepository2ImplIT#testFindAll()}
	 *
	 * @param ql
	 * @param sort   null表示不排序
	 * @param params
	 * @param <M>
	 * @return
	 */
	public <M> List<M> findAll(final String ql, final Sort sort, final Object... params) {

		Query query = getEntityManager().createQuery(ql + prepareOrder(sort));
		applyEnableQueryCache(query);
		setParameters(query, params);

		return query.getResultList();
	}

	/**
	 * <p>根据ql和按照索引顺序的params查询一个实体<br/>
	 * 具体使用请参考测试用例：{@see io.microvibe.booster.commons.repository.UserRepository2ImplIT#testFindOne()}
	 *
	 * @param ql
	 * @param params
	 * @param <M>
	 * @return
	 */
	public <M> M findOne(final String ql, final Object... params) {

		List<M> list = findAll(ql, new PageRequest(0, 1), params);

		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * <p>根据ql和按照索引顺序的params执行ql统计<br/>
	 * 具体使用请参考测试用例：io.microvibe.booster.commons.repository.UserRepository2ImplIT#testCountAll()
	 *
	 * @param ql
	 * @param params
	 * @return
	 */
	public long count(final String ql, final Object... params) {
//        Query query = entityManager.createQuery(ql);
		Query query = getEntityManager().createQuery(ql);
		applyEnableQueryCache(query);
		setParameters(query, params);

		return (Long) query.getSingleResult();
	}

	/**
	 * <p>执行批处理语句.如 之间insert, update, delete 等.<br/>
	 * 具体使用请参考测试用例：{@see io.microvibe.booster.commons.repository.UserRepository2ImplIT#testBatchUpdate()}
	 *
	 * @param ql
	 * @param params
	 * @return
	 */
	public int batchUpdate(final String ql, final Object... params) {

		Query query = getEntityManager().createQuery(ql);
		setParameters(query, params);

		return query.executeUpdate();
	}

	/**
	 * 按顺序设置Query参数
	 *
	 * @param query
	 * @param params
	 */
	public void setParameters(Query query, Object[] params) {
		if (params != null) {
			for (int i = 0; i < params.length; i++) {
				query.setParameter(i + 1, params[i]);
			}
		}
	}

	/**
	 * 拼排序
	 *
	 * @param sort
	 * @return
	 */
	public String prepareOrder(Sort sort) {
		if (sort == null || !sort.iterator().hasNext()) {
			return "";
		}
		StringBuilder orderBy = new StringBuilder("");
		orderBy.append(" order by ");
		orderBy.append(sort.toString().replace(":", " "));
		return orderBy.toString();
	}

	public <T> JpaEntityInformation<T, ?> getMetadata(Class<T> entityClass) {
//        return JpaEntityInformationSupport.getMetadata(entityClass, entityManager);//FIXME springboot
		return JpaEntityInformationSupport.getEntityInformation(entityClass, getEntityManager());
	}

	public String getEntityName(Class<?> entityClass) {
		return getMetadata(entityClass).getEntityName();
	}

	public void applyEnableQueryCache(Query query) {
		if (enableQueryCache) {
			query.setHint(org.hibernate.jpa.QueryHints.HINT_CACHEABLE, true);// 开启查询缓存
		}
	}

}
