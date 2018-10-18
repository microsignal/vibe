package io.microvibe.booster.core.base.service;

import io.microvibe.booster.commons.utils.ReflectionUtils;
import io.microvibe.booster.core.base.entity.BaseEntity;
import io.microvibe.booster.core.base.mybatis.mapping.EntityMapper;
import io.microvibe.booster.core.base.utils.InjectBaseDependencyHelper;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.io.Serializable;

/**
 * @author Qt
 * @since Aug 29, 2018
 */
@Slf4j
public class AbstractBaseService<Entity extends BaseEntity<ID>, ID extends Serializable>
	implements BaseService<Entity, ID> {

	protected EntityMapper<Entity, ID> entityMapper;
	protected Class<Entity> entityClass;

	@PostConstruct
	private void init() {
		try {
			this.entityMapper = InjectBaseDependencyHelper.findBaseComponent(this, EntityMapper.class);
			Class entityClass = ReflectionUtils.firstParameterizedType(this.getClass());
			this.entityClass = entityClass;
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
		}
	}

	@Override
	public long recover(Entity entity) {
		return 0;
	}

	@Override
	public long recover(ID id) {
		return 0;
	}

	@Override
	public long[] recover(ID... ids) {
		return new long[0];
	}

}
