package io.microvibe.booster.pay.service;

import io.microvibe.booster.commons.utils.ReflectionUtils;
import io.microvibe.booster.core.base.mybatis.mapping.BaseMapper;
import io.microvibe.booster.core.base.utils.InjectBaseDependencyHelper;
import io.microvibe.booster.pay.entity.BasePayEntity;
import io.microvibe.booster.system.toolkit.Users;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.data.domain.Persistable;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Qt
 * @since Aug 30, 2018
 */
@Slf4j
public abstract class BasePayService<T extends Persistable<K>, K extends Serializable> {

	protected Class entityClass;
	protected BaseMapper<T, K> mapper;

	@PostConstruct
	private void init() {
		try {
			mapper = InjectBaseDependencyHelper.findBaseComponent(this, BaseMapper.class);
			entityClass = ReflectionUtils.findParameterizedType(BasePayService.class, getClass(), 0);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	protected String getEntityName() {
		return entityClass == null ? "unknown" : entityClass.getSimpleName();
	}

	protected BaseMapper<T, K> getMapper() {
		return mapper;
	}

	protected void fillInsertRecords(T entity) {
		try {
			if (entity instanceof BasePayEntity) {
				BasePayEntity basePayEntity = (BasePayEntity) entity;
				basePayEntity.setCreateDate(new Date());
				basePayEntity.setCreateUser(Users.getCurrentUserId());
			}
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
	}

	protected void fillUpdateRecords(T entity) {
		try {
			if (entity instanceof BasePayEntity) {
				BasePayEntity basePayEntity = (BasePayEntity) entity;
				basePayEntity.setUpdateDate(new Date());
				basePayEntity.setUpdateUser(Users.getCurrentUserId());
			}
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
	}

	public long getCount(T entity) {
		if (log.isInfoEnabled()) {
			log.info("查询表记录数: {}", getEntityName());
		}
		long count = getMapper().countByEntity(entity);
		return count;
	}

	public Page<T> getPage(T entity, int pageNum, int pageSize) {
		if (log.isInfoEnabled()) {
			log.info("查询表结果集: {}", getEntityName());
		}
		Page<T> page = PageHelper.startPage(pageNum, pageSize, false);
		List<T> list = getMapper().selectByEntity(entity);
		if (list instanceof Page) {
			page = (Page<T>) list;
		} else {
			page.addAll(list);
		}
		if (page.getTotal() <= 0) {
			long total = getMapper().countByEntity(entity);
			page.setTotal(total);
		}
		return page;
	}

	public List<T> getLimitedList(T entity) {
		return getLimitedList(entity, 0xFFFF);
	}

	public List<T> getLimitedList(T entity, int maxCount) {
		if (log.isInfoEnabled()) {
			log.info("查询表结果集: {}", getEntityName());
		}
		List<T> list = getMapper().selectByEntity(entity, new RowBounds(0, maxCount));
		return list;
	}

	public List<T> getList(T entity) {
		if (log.isInfoEnabled()) {
			log.info("查询表结果集: {}", getEntityName());
		}
		List<T> list = getMapper().selectByEntity(entity);
		return list;
	}

	public List<T> getList(T entity, int pageIndex, int pageSize) {
		if (log.isInfoEnabled()) {
			log.info("查询表结果集: {}", getEntityName());
		}
		List<T> list = getMapper().selectByEntity(entity, new RowBounds(pageIndex * pageSize, pageSize));
		return list;
	}

	public long insert(T entity) {
		if (log.isInfoEnabled()) {
			log.info("新增表记录: {}", getEntityName());
		}
		fillInsertRecords(entity);
		return getMapper().insertByEntity(entity);
	}

	public long insertSelective(T entity) {
		if (log.isInfoEnabled()) {
			log.info("新增表记录: {}", getEntityName());
		}
		fillInsertRecords(entity);
		return getMapper().insertSelectiveByEntity(entity);
	}

	public long delete(T entity) {
		if (log.isInfoEnabled()) {
			log.info("根据主键删除表记录: {}", getEntityName());
		}
		return getMapper().deleteByEntity(entity);
	}

	public long update(T entity) {
		if (log.isInfoEnabled()) {
			log.info("根据主键更新表记录: {}", getEntityName());
		}
		fillUpdateRecords(entity);
		return getMapper().updateByEntity(entity);
	}

	public long updateSelective(T entity) {
		if (log.isInfoEnabled()) {
			log.info("根据主键更新表记录: {}", getEntityName());
		}
		fillUpdateRecords(entity);
		return getMapper().updateSelectiveByEntity(entity);
	}

	public T getById(T entity) {
		if (log.isInfoEnabled()) {
			log.info("根据主键查询表记录: {}", getEntityName());
		}
		return getMapper().getByIdOfEntity(entity);
	}

	public T getUnique(T entity) {
		if (log.isInfoEnabled()) {
			log.info("查询表的唯一结果集: {}", getEntityName());
		}
		return getMapper().getByEntity(entity);
	}

}
