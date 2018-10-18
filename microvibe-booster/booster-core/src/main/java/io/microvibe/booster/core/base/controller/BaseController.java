package io.microvibe.booster.core.base.controller;

import io.microvibe.booster.commons.utils.ReflectionUtils;
import io.microvibe.booster.core.base.entity.BaseEntity;
import io.microvibe.booster.core.base.utils.InjectBaseDependencyHelper;
import io.microvibe.booster.core.base.service.JpaBaseService;
import io.microvibe.booster.core.base.service.MybatisBaseService;

import javax.annotation.PostConstruct;
import java.io.Serializable;

public abstract class BaseController<M extends BaseEntity<ID>, ID extends Serializable>
	extends AbstractBaseController {


	protected final Class<M> entityClass;
	protected JpaBaseService<M, ID> jpaBaseService;
	protected MybatisBaseService<M, ID> mybatisBaseService;

	public BaseController() {
		this.entityClass = (Class<M>) ReflectionUtils.firstParameterizedType(getClass());
	}

	@PostConstruct
	private void init() {
		jpaBaseService = InjectBaseDependencyHelper.findBaseComponent(this, JpaBaseService.class);
		mybatisBaseService = InjectBaseDependencyHelper.findBaseComponent(this, MybatisBaseService.class);
	}

}
