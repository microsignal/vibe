package io.microvibe.booster.core.base.repository.support;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.io.Serializable;

/**
 * 适配 <b>spring-data-commons:1.6.1.RELEASE</b> 的<b>Page</b>接口方法,
 * 使用{@link WrappedPageImpl}代替{@link PageImpl}
 *
 * @param <T>  the type of the entity to handle
 * @param <ID> the type of the entity's identifier
 * @author Qt
 */
public class WrappedSimpleJpaRepository<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> {

	public WrappedSimpleJpaRepository(Class<T> domainClass, EntityManager em) {
		super(domainClass, em);
	}

	public WrappedSimpleJpaRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);
	}

	@Override
	public Page<T> findAll(Specification<T> spec, Pageable pageable) {
		Page<T> page = super.findAll(spec, pageable);
		return pageable == null ? new WrappedPageImpl<T>(page.getContent())
			: new WrappedPageImpl<T>(page.getContent(), pageable, page.getTotalElements());
	}

	@Override
	public <S extends T> Page<S> findAll(Example<S> example, Pageable pageable) {
		Page<S> page = super.findAll(example, pageable);
		return pageable == null ? new WrappedPageImpl<S>(page.getContent())
			: new WrappedPageImpl<S>(page.getContent(), pageable, page.getTotalElements());
	}

	@Override
	@Deprecated
	protected Page<T> readPage(TypedQuery<T> query, Pageable pageable, Specification<T> spec) {
		Page<T> page = super.readPage(query, pageable, spec);
		return pageable == null ? new WrappedPageImpl<T>(page.getContent())
			: new WrappedPageImpl<T>(page.getContent(), pageable, page.getTotalElements());
	}

	@Override
	protected <S extends T> Page<S> readPage(TypedQuery<S> query, Class<S> domainClass, Pageable pageable,
											 Specification<S> spec) {
		Page<S> page = super.readPage(query, domainClass, pageable, spec);
		return pageable == null ? new WrappedPageImpl<S>(page.getContent())
			: new WrappedPageImpl<S>(page.getContent(), pageable, page.getTotalElements());
	}

}
