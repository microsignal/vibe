package io.microvibe.booster.core.base.service;

import io.microvibe.booster.core.api.model.BodyModel;
import io.microvibe.booster.core.api.model.Data;
import io.microvibe.booster.core.base.entity.BaseEntity;
import io.microvibe.booster.core.base.entity.CreateDateRecordable;
import io.microvibe.booster.core.base.entity.UpdateDateRecordable;
import io.microvibe.booster.core.base.repository.BaseRepository;
import io.microvibe.booster.core.base.utils.InjectBaseDependencyHelper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>抽象service层基类 提供一些简便方法
 * <p>泛型 ： M 表示实体类型；ID表示主键类型
 */
public abstract class JpaBaseService<M extends BaseEntity<ID>, ID extends Serializable> implements InitializingBean {

	protected BaseRepository<M, ID> baseRepository;

	protected void setBaseRepository(BaseRepository<M, ID> baseRepository) {
		this.baseRepository = baseRepository;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		baseRepository = InjectBaseDependencyHelper.findBaseComponent(this, BaseRepository.class);
	}

	/**
	 * 保存单个实体
	 *
	 * @param entity 实体
	 * @return 返回保存的实体
	 */
	public M save(M entity) {
		if (entity.isNew()) {
			if (entity instanceof CreateDateRecordable) {
				CreateDateRecordable o = (CreateDateRecordable) entity;
				o.setCreateDate(new Date());
			}
		} else {
			if (entity instanceof UpdateDateRecordable) {
				UpdateDateRecordable o = (UpdateDateRecordable) entity;
				o.setUpdateDate(new Date());
			}
		}
		return baseRepository.save(entity);
	}

	public M saveAndFlush(M entity) {
		entity = save(entity);
		baseRepository.flush();
		return entity;
	}

	/**
	 * 更新单个实体
	 *
	 * @param entity 实体
	 * @return 返回更新的实体
	 */
	public M update(M entity) {
		return save(entity);
       /* if (entity instanceof UpdateDateRecordable) {
            UpdateDateRecordable o = (UpdateDateRecordable) entity;
            o.setUpdateDate(new Date());
        }
        return baseRepository.save(entity);*/
	}

	/**
	 * 根据主键删除相应实体
	 *
	 * @param id 主键
	 */
	public void delete(ID id) {
		baseRepository.delete(id);
	}

	/**
	 * 删除实体
	 *
	 * @param m 实体
	 */
	public void delete(M m) {
		baseRepository.delete(m);
	}

	/**
	 * 根据主键删除相应实体
	 *
	 * @param ids 实体
	 */
	public void delete(ID[] ids) {
		baseRepository.delete(ids);
	}

	/**
	 * 按照主键查询
	 *
	 * @param id 主键
	 * @return 返回id对应的实体
	 */
	public M findOne(ID id) {
		return baseRepository.findOne(id);
	}

	/**
	 * 实体是否存在
	 *
	 * @param id 主键
	 * @return 存在 返回true，否则false
	 */
	public boolean exists(ID id) {
		return baseRepository.exists(id);
	}

	/**
	 * 统计实体总数
	 *
	 * @return 实体总数
	 */
	public long count() {
		return baseRepository.count();
	}

	/**
	 * 查询所有实体
	 *
	 * @return
	 */
	public List<M> findAll() {
		return baseRepository.findAll();
	}

	/**
	 * 按照顺序查询所有实体
	 *
	 * @param sort 排序
	 * @return
	 */
	public List<M> findAll(Sort sort) {
		return baseRepository.findAll(sort);
	}

	/**
	 * 按照顺序查询所有实体
	 *
	 * @param apiData 排序
	 * @return
	 */
	public List<M> findAllBySort(Data apiData) {
		BodyModel dataModel = apiData.getBody();
		Sort sort = dataModel.getSort();
		return findAll(sort);
	}

	/**
	 * 分页及排序查询实体
	 *
	 * @param pageable 分页及排序数据
	 * @return
	 */
	public Page<M> findAll(Pageable pageable) {
		return baseRepository.findAll(pageable);
	}

	/**
	 * 分页及排序查询实体
	 *
	 * @param apiData 分页及排序数据
	 * @return
	 */
	public Page<M> findAllByPage(Data apiData) {
		BodyModel dataModel = apiData.getBody();
		Pageable pageable = dataModel.getPageable();
		return findAll(pageable);
	}

}
