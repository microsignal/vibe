package io.microvibe.booster.core.base.service;

import io.microvibe.booster.core.base.entity.BaseEntity;

import java.io.Serializable;

public interface BaseService<Entity extends BaseEntity<ID>, ID extends Serializable> {

	/**
	 * 恢复被删除记录
	 *
	 * @param entity
	 * @return
	 */
	long recover(Entity entity);

	/**
	 * 恢复被删除记录
	 *
	 * @param id
	 * @return
	 */
	long recover(ID id);

	/**
	 * 恢复被删除记录
	 *
	 * @param ids
	 * @return
	 */
	long[] recover(ID... ids);
}
