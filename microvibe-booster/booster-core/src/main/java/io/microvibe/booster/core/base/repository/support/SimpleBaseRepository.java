package io.microvibe.booster.core.base.repository.support;

import io.microvibe.booster.core.base.entity.DeletedRecordable;
import io.microvibe.booster.core.base.repository.BaseRepository;
import io.microvibe.booster.core.base.repository.RepositoryHelper;
import io.microvibe.booster.core.base.repository.annotation.QueryJoin;
import com.google.common.collect.Sets;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.CrudMethodMetadata;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

import static org.springframework.data.jpa.repository.query.QueryUtils.toOrders;


/**
 * 抽象基础Custom Repository 实现
 * @author Qt
 */
public class SimpleBaseRepository<M, ID extends Serializable> extends SimpleJpaRepository<M, ID>
	implements BaseRepository<M, ID> {

	public static final String LOGIC_DELETE_ALL_QUERY_STRING = "update %s x set x.deleted=true where x in (?1)";
	public static final String DELETE_ALL_QUERY_STRING = "delete from %s x where x in (?1)";
	public static final String FIND_QUERY_STRING = "from %s x where 1=1 ";
	public static final String COUNT_QUERY_STRING = "select count(x) from %s x where 1=1 ";

	private final EntityManager em;
	private final JpaEntityInformation<M, ID> entityInformation;

	private final RepositoryHelper repositoryHelper;

	// private LockMetadataProvider lockMetadataProvider;
	private CrudMethodMetadata metadata;

	private Class<M> entityClass;
	private String entityName;
	private String idName;

	/**
	 * 查询所有的QL
	 */
	private String findAllQL;
	/**
	 * 统计QL
	 */
	private String countAllQL;

	private QueryJoin[] joins;

	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	public SimpleBaseRepository(JpaEntityInformation<M, ID> entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);

		this.entityInformation = entityInformation;
		this.entityClass = this.entityInformation.getJavaType();
		this.entityName = this.entityInformation.getEntityName();
		this.idName = this.entityInformation.getIdAttributeNames().iterator().next();
		this.em = entityManager;

		repositoryHelper = new RepositoryHelper(entityClass);

		findAllQL = String.format(FIND_QUERY_STRING, entityName);
		countAllQL = String.format(COUNT_QUERY_STRING, entityName);
	}

//    /**
//     * Configures a custom {@link org.springframework.data.jpa.repository.support.LockMetadataProvider} to be used to detect {@link javax.persistence.LockModeType}s to be applied to
//     * queries.
//     *
//     * @param lockMetadataProvider
//     */
//    public void setLockMetadataProvider(LockMetadataProvider lockMetadataProvider) {
//        super.setLockMetadataProvider(lockMetadataProvider);
//        this.lockMetadataProvider = lockMetadataProvider;
//    }

	@Override
	protected CrudMethodMetadata getRepositoryMethodMetadata() {
		return metadata;
	}

	/**
	 * Configures a custom {@link CrudMethodMetadata} to be used to detect {@link LockModeType}s and query hints to be
	 * applied to queries.
	 *
	 * @param crudMethodMetadata
	 */
	@Override
	public void setRepositoryMethodMetadata(CrudMethodMetadata crudMethodMetadata) {
		this.metadata = crudMethodMetadata;
	}

	/**
	 * 设置查询所有的ql
	 *
	 * @param findAllQL
	 */
	public void setFindAllQL(String findAllQL) {
		this.findAllQL = findAllQL;
	}

	/**
	 * 设置统计的ql
	 *
	 * @param countAllQL
	 */
	public void setCountAllQL(String countAllQL) {
		this.countAllQL = countAllQL;
	}

	public void setJoins(QueryJoin[] joins) {
		this.joins = joins;
	}

	/////////////////////////////////////////////////
	//////// 覆盖默认spring data jpa的实现////////////
	/////////////////////////////////////////////////

	/**
	 * 根据主键删除相应实体
	 *
	 * @param id 主键
	 */
	@Transactional
	@Override
	public void delete(final ID id) {
		M entity = findOne(id);
		delete(entity);
	}

	/**
	 * 删除实体
	 *
	 * @param entity 实体
	 */
	@Transactional
	@Override
	public void delete(final M entity) {
		if (entity == null) {
			return;
		}
		if (entity instanceof DeletedRecordable) {
			((DeletedRecordable) entity).setDeleted(Boolean.TRUE);
			save(entity);
		} else {
			super.delete(entity);
		}
	}

	/**
	 * 根据主键删除相应实体
	 *
	 * @param ids 实体
	 */
	@Transactional
	@Override
	public void delete(final ID[] ids) {
		if (ArrayUtils.isEmpty(ids)) {
			return;
		}
		List<M> models = new ArrayList<M>();
		for (ID id : ids) {
			M model = null;
			try {
				model = entityClass.newInstance();
			} catch (Exception e) {
				throw new RuntimeException("batch delete " + entityClass + " error", e);
			}
			try {
				BeanUtils.setProperty(model, idName, id);
			} catch (Exception e) {
				throw new RuntimeException("batch delete " + entityClass + " error, can not set id", e);
			}
			models.add(model);
		}
		deleteInBatch(models);
	}

	@SuppressWarnings("rawtypes")
	@Transactional
	@Override
	public void deleteInBatch(final Iterable<M> entities) {
		Iterator<M> iter = entities.iterator();
		if (entities == null || !iter.hasNext()) {
			return;
		}

		Set models = Sets.newHashSet(iter);

		boolean logicDeleteableEntity = DeletedRecordable.class.isAssignableFrom(this.entityClass);

		if (logicDeleteableEntity) {
			String ql = String.format(LOGIC_DELETE_ALL_QUERY_STRING, entityName);
			repositoryHelper.batchUpdate(ql, models);
		} else {
			String ql = String.format(DELETE_ALL_QUERY_STRING, entityName);
			repositoryHelper.batchUpdate(ql, models);
		}
	}

	/**
	 * 按照主键查询
	 *
	 * @param id 主键
	 * @return 返回id对应的实体
	 */
	@Transactional
	@Override
	public M findOne(ID id) {
		if (id == null) {
			return null;
		}
        /*
        if (id instanceof Integer && ((Integer) id).intValue() == 0) {
            return null;
        }
        if (id instanceof Long && ((Long) id).longValue() == 0L) {
            return null;
        }*/
		return super.findOne(id);
	}

	//////// 根据Specification查询 直接从SimpleJpaRepository复制过来的///////////////////////////////////
	@Override
	public M findOne(Specification<M> spec) {
		try {
			return getQuery(spec, (Sort) null).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public List<M> findAll(Iterable<ID> ids) {
		return getQuery(new Specification<M>() {
			@Override
			public Predicate toPredicate(Root<M> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Path<?> path = root.get(entityInformation.getIdAttribute());
				return path.in(cb.parameter(Iterable.class, "ids"));
			}
		}, (Sort) null).setParameter("ids", ids).getResultList();
	}

	@Override
	public List<M> findAll(Specification<M> spec) {
		return getQuery(spec, (Sort) null).getResultList();
	}

	@Override
	public Page<M> findAll(Specification<M> spec, Pageable pageable) {

		TypedQuery<M> query = getQuery(spec, pageable);
		return pageable == null ? new WrappedPageImpl<M>(query.getResultList()) : readPage(query, pageable, spec);
	}

	@Override
	public List<M> findAll(Specification<M> spec, Sort sort) {
		return getQuery(spec, sort).getResultList();
	}

	@Override
	public long count(Specification<M> spec) {
		return getCountQuery(spec).getSingleResult();
	}

	/**
	 * Reads the given {@link TypedQuery} into a {@link org.springframework.data.domain.Page} applying the given {@link org.springframework.data.domain.Pageable} and
	 * {@link org.springframework.data.jpa.domain.Specification}.
	 *
	 * @param query    must not be {@literal null}.
	 * @param spec     can be {@literal null}.
	 * @param pageable can be {@literal null}.
	 * @return
	 */
	@Override
	protected Page<M> readPage(TypedQuery<M> query, Pageable pageable, Specification<M> spec) {
		query.setFirstResult(pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());
//        Long total = QueryUtils.executeCountQuery(getCountQuery(spec));
		long total = count(spec);
		List<M> content = total > pageable.getOffset() ? query.getResultList() : Collections.<M>emptyList();
		return new WrappedPageImpl<M>(content, pageable, total);
//        return readPage(query, this.entityClass, pageable, spec);
	}

	/**
	 * Creates a new count query for the given {@link org.springframework.data.jpa.domain.Specification}.
	 *
	 * @param spec can be {@literal null}.
	 * @return
	 */
	@Override
	protected TypedQuery<Long> getCountQuery(Specification<M> spec) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);

		Root<M> root = applySpecificationToCriteria(spec, query);

		if (query.isDistinct()) {
			query.select(builder.countDistinct(root));
		} else {
			query.select(builder.count(root));
		}

		TypedQuery<Long> q = em.createQuery(query);
		repositoryHelper.applyEnableQueryCache(q);
		return q;
	}

	/**
	 * Creates a new {@link TypedQuery} from the given {@link org.springframework.data.jpa.domain.Specification}.
	 *
	 * @param spec     can be {@literal null}.
	 * @param pageable can be {@literal null}.
	 * @return
	 */
	@Override
	protected TypedQuery<M> getQuery(Specification<M> spec, Pageable pageable) {
		Sort sort = pageable == null ? null : pageable.getSort();
		return getQuery(spec, sort);
	}

	/**
	 * Creates a {@link TypedQuery} for the given {@link org.springframework.data.jpa.domain.Specification} and {@link org.springframework.data.domain.Sort}.
	 *
	 * @param spec can be {@literal null}.
	 * @param sort can be {@literal null}.
	 * @return
	 */
	@Override
	protected TypedQuery<M> getQuery(Specification<M> spec, Sort sort) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<M> query = builder.createQuery(entityClass);

		Root<M> root = applySpecificationToCriteria(spec, query);
		query.select(root);

		applyJoins(root);

		if (sort != null) {
			query.orderBy(toOrders(sort, root, builder));
		}

		TypedQuery<M> q = em.createQuery(query);

		repositoryHelper.applyEnableQueryCache(q);

		return applyLockMode(q);
	}

	private void applyJoins(Root<M> root) {
		if (joins == null) {
			return;
		}

		for (QueryJoin join : joins) {
			root.join(join.property(), join.joinType());
		}
	}

	/**
	 * Applies the given {@link org.springframework.data.jpa.domain.Specification} to the given {@link CriteriaQuery}.
	 *
	 * @param spec  can be {@literal null}.
	 * @param query must not be {@literal null}.
	 * @return
	 */
	private <S> Root<M> applySpecificationToCriteria(Specification<M> spec, CriteriaQuery<S> query) {

		Assert.notNull(query, "CriteriaQuery must not be null!");
		Root<M> root = query.from(entityClass);

		if (spec == null) {
			return root;
		}

		CriteriaBuilder builder = em.getCriteriaBuilder();
		Predicate predicate = spec.toPredicate(root, query, builder);

		if (predicate != null) {
			query.where(predicate);
		}

		return root;
	}

	private TypedQuery<M> applyLockMode(TypedQuery<M> query) {
//        LockModeType type = lockMetadataProvider == null ? null : lockMetadataProvider.getLockModeType();
//        return type == null ? query : query.setLockMode(type);
		if (metadata == null) {
			return query;
		}
		LockModeType type = metadata.getLockModeType();
		TypedQuery<M> toReturn = type == null ? query : query.setLockMode(type);
		for (Entry<String, Object> hint : getQueryHints().entrySet()) {
			toReturn.setHint(hint.getKey(), hint.getValue());
		}
		return toReturn;
	}
	/////// 直接从SimpleJpaRepository复制过来的///////////////////////////////

	@Override
	public List<M> findAll() {
		return repositoryHelper.findAll(findAllQL);
	}

	@Override
	public List<M> findAll(final Sort sort) {
		return repositoryHelper.findAll(findAllQL, sort);
	}

	@Override
	public Page<M> findAll(final Pageable pageable) {
		return new WrappedPageImpl<M>(
			repositoryHelper.<M>findAll(findAllQL, pageable),
			pageable,
			repositoryHelper.count(countAllQL));
	}

	@Override
	public long count() {
		return repositoryHelper.count(countAllQL);
	}

	/**
	 * 重写默认的 这样可以走一级/二级缓存
	 *
	 * @param id
	 * @return
	 */
	@Override
	public boolean exists(ID id) {
		return findOne(id) != null;
	}

}
