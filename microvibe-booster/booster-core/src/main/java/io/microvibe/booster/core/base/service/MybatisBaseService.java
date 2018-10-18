package io.microvibe.booster.core.base.service;

import io.microvibe.booster.commons.err.ValidationException;
import io.microvibe.booster.commons.utils.ReflectionUtils;
import io.microvibe.booster.commons.utils.StringUtils;
import io.microvibe.booster.core.api.model.Data;
import io.microvibe.booster.core.api.model.SearchModel;
import io.microvibe.booster.core.api.tools.DataKit;
import io.microvibe.booster.core.base.controller.annotation.DataParam;
import io.microvibe.booster.core.base.entity.*;
import io.microvibe.booster.core.base.mybatis.example.Example;
import io.microvibe.booster.core.base.mybatis.mapping.BaseMapper;
import io.microvibe.booster.core.base.mybatis.mapping.EntityMapper;
import io.microvibe.booster.core.base.persistence.PersistentRecognizer;
import io.microvibe.booster.core.base.repository.support.WrappedPageImpl;
import io.microvibe.booster.core.base.utils.EntityKit;
import io.microvibe.booster.core.base.utils.InjectBaseDependencyHelper;
import io.microvibe.booster.core.search.SearchOper;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
public abstract class MybatisBaseService<Entity extends BaseEntity<ID>, ID extends Serializable> implements InitializingBean, BaseService<Entity, ID> {

	protected BaseMapper<Entity, ID> baseMapper;
	protected EntityMapper<Entity, ID> entityMapper;
	protected Class<Entity> entityClass;

	public BaseMapper<Entity, ID> getBaseMapper() {
		return baseMapper;
	}

	protected void setBaseMapper(BaseMapper<Entity, ID> baseMapper) {
		this.baseMapper = baseMapper;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.baseMapper = InjectBaseDependencyHelper.findBaseComponent(this, BaseMapper.class);
		this.entityMapper = InjectBaseDependencyHelper.findBaseComponent(this, EntityMapper.class);
		try {
			Class entityClass = ReflectionUtils.firstParameterizedType(this.getClass());
			this.entityClass = entityClass;
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
		}
	}

	protected boolean requiredTreeableChecking() {
		return true;
	}

	protected boolean requiredInsertChecking() {
		return false;
	}

	protected void doInsertChecking(Entity entity) {
		throw new ValidationException("ERR-02001");
	}

	protected boolean requiredDeleteChecking() {
		return false;
	}

	protected void doDeleteChecking(Entity entity) {
		throw new ValidationException("ERR-02002");
	}

	protected boolean requiredUpdateChecking() {
		return false;
	}

	protected void doUpdateChecking(Entity entity) {
		throw new ValidationException("ERR-02003");
	}

	private void checkTreeableWhenDelete(Treeable tree) {
		/*
		String treePath = StringUtils.trimToNull(tree.getParentPath());
		treePath = appendTreePath(tree, treePath);
		Example<Entity> example = Example.of(entityClass)
			.like(tree.parentPathField(), SearchOper.prefixLike.repair(treePath))
			.build();
		*/
		Entity param = EntityKit.createEmpty(entityClass);
		((Treeable) param).setParentId(tree.getId());
		if (param instanceof DeletedRecordable) {
			((DeletedRecordable) param).setDeleted(false);
		}
		if (baseMapper.countByEntity(param) > 0) {
			throw new IllegalStateException("存在子节点,不能删除");
		}
	}

	private void checkWhenDelete(ID id) {
		Entity entity = null;
		if (requiredDeleteChecking()) {
			entity = getById(id);
			if (entity != null) {
				doDeleteChecking(entity);
			}
		}
		if (requiredTreeableChecking() && Treeable.class.isAssignableFrom(entityClass)) {
			if (entity == null) {
				entity = getById(id);
			}
			if (entity != null) {
				Treeable tree = (Treeable) entity;
				checkTreeableWhenDelete(tree);
			}
		}
	}

	private void checkWhenDelete(Entity entity) {
		if (requiredDeleteChecking()) {
			doDeleteChecking(entity);
		}
		if (requiredTreeableChecking() && Treeable.class.isAssignableFrom(entityClass)) {
			Treeable tree = (Treeable) getById(entity);
			if (tree != null) {
				checkTreeableWhenDelete(tree);
			}
		}
	}

	private void checkWhenDelete(Map<String, Object> param) {
		Entity entity = null;
		if (requiredDeleteChecking()) {
			entity = getById(param);
			if (entity != null) {
				doDeleteChecking(entity);
			}
		}
		if (requiredTreeableChecking() && Treeable.class.isAssignableFrom(entityClass)) {
			if (entity == null) {
				entity = getById(param);
			}
			if (entity != null) {
				Treeable tree = (Treeable) entity;
				checkTreeableWhenDelete(tree);
			}
		}
	}

	@Override
	public long recover(Entity entity) {
		if (entity instanceof DeletedRecordable) {
			long rows = 0;
			((DeletedRecordable) entity).setDeleted(false);
			rows = baseMapper.updateSelectiveByEntity(entity);
			checkVersion(rows, entity);
			return rows;
		} else {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public long recover(ID id) {
		if (entityClass != null && DeletedRecordable.class.isAssignableFrom(entityClass)) {
			Entity entity = EntityKit.createEmpty(entityClass);
			entity.setId(id);
			if (Versionable.class.isAssignableFrom(entityClass)) {
				entity = baseMapper.getById(id);
			}
			((DeletedRecordable) entity).setDeleted(false);
			long rows = baseMapper.updateSelectiveByEntity(entity);
			checkVersion(rows, entity);
			return rows;
		} else {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public long[] recover(ID... ids) {
		long[] rs = new long[ids.length];
		for (int i = 0; i < ids.length; i++) {
			rs[i] = recover(ids[i]);
		}
		return rs;
	}

	public long delete(ID id) {
		checkWhenDelete(id);
		if (entityClass != null && DeletedRecordable.class.isAssignableFrom(entityClass)) {
			// Entity entity = baseMapper.getById(id);
			Entity entity = EntityKit.createEmpty(entityClass);
			entity.setId(id);
			((DeletedRecordable) entity).delete();
			return baseMapper.updateSelectiveByEntity(entity);
		} else {
			return baseMapper.deleteById(id);
		}
	}

	private long delete(Supplier<Long> logicDeleting, Supplier<Long> deleting){
		// checkBeforeDelete()

		// checkAfterDelete

		return 0;//fixme
	}

	public long[] delete(ID... ids) {
		long[] rs = new long[ids.length];
		for (int i = 0; i < ids.length; i++) {
			rs[i] = delete(ids[i]);
		}
		return rs;
	}

	private void checkVersion(long rows, Entity entity) {
		if (entity instanceof Versionable) {
			if (rows == 0) {
				throw new IllegalStateException("bad version: " + ((Versionable) entity).getVersion());
			}
		}
	}

	private void checkVersion(long rows, Map<String, Object> param) {
		if (entityClass != null && Versionable.class.isAssignableFrom(entityClass)) {
			if (rows == 0) {
				throw new IllegalStateException("bad version: " + param.get(Versionable.FIELD_NAME));
			}
		}
	}

	public long delete(Entity entity) {
		checkWhenDelete(entity);
		long rows = 0;
		if (entity instanceof DeletedRecordable) {
			((DeletedRecordable) entity).delete();
			rows = baseMapper.updateSelectiveByEntity(entity);
		} else {
			rows = baseMapper.deleteByEntity(entity);
		}
		checkVersion(rows, entity);
		return rows;
	}

	public long delete(Map<String, Object> param) {
		checkWhenDelete(param);
		long rows = 0;
		if (entityClass != null && DeletedRecordable.class.isAssignableFrom(entityClass)) {
			param.put("deleted", true);
			rows = baseMapper.updateSelectiveByMap(param);
		} else {
			rows = baseMapper.deleteByMap(param);
		}
		checkVersion(rows, param);
		return rows;
	}

	public long deleteForcely(ID id) {
		checkWhenDelete(id);
		return baseMapper.deleteById(id);
	}

	public long[] deleteForcely(ID... ids) {
		long[] rs = new long[ids.length];
		for (int i = 0; i < ids.length; i++) {
			rs[i] = deleteForcely(ids[i]);
		}
		return rs;
	}

	public long deleteForcely(Entity entity) {
		checkWhenDelete(entity);
		long rows = baseMapper.deleteByEntity(entity);
		checkVersion(rows, entity);
		return rows;
	}

	public long deleteForcely(Map<String, Object> param) {
		checkWhenDelete(param);
		long rows = baseMapper.deleteByMap(param);
		checkVersion(rows, param);
		return rows;
	}

	public long saveOrUpdate(Entity entity) {
		if (entity.isNew()) {
			EntityKit.fillKeyFields(entity);
			return insert(entity);
		} else {
			long rows = update(entity);
			checkVersion(rows, entity);
			if (rows == 0) {// 未更新到数据
				return insert(entity);
			} else {
				return rows;
			}
		}
	}

	public long saveOrUpdateSelective(Entity entity) {
		if (entity.isNew()) {
			EntityKit.fillKeyFields(entity);
			return insertSelective(entity);
		} else {
			long rows = updateSelective(entity);
			checkVersion(rows, entity);
			if (rows == 0) {// 未更新到数据
				return insertSelective(entity);
			} else {
				return rows;
			}
		}
	}

	public long insert(Entity entity) {
		EntityKit.clearEmptyFields(entity);
		if (entity.isNew()) {
			EntityKit.fillKeyFields(entity);
		}
		EntityKit.fillCreateDate(entity);
		if (entity instanceof DeletedRecordable) {
			if (((DeletedRecordable) entity).getDeleted() == null) {
				((DeletedRecordable) entity).setDeleted(Boolean.FALSE);
			}
		}
		if (requiredTreeableChecking()) {
			fixTreeableParent(entity);
		}
		if (requiredInsertChecking()) {
			doInsertChecking(entity);
		}
		return baseMapper.insertByEntity(entity);
	}

	public long insertSelective(Entity entity) {
		EntityKit.clearEmptyFields(entity);
		if (entity.isNew()) {
			EntityKit.fillKeyFields(entity);
		}
		EntityKit.fillCreateDate(entity);
		if (entity instanceof DeletedRecordable) {
			if (((DeletedRecordable) entity).getDeleted() == null) {
				((DeletedRecordable) entity).setDeleted(Boolean.FALSE);
			}
		}
		if (requiredTreeableChecking()) {
			fixTreeableParent(entity);
		}
		if (requiredInsertChecking()) {
			doInsertChecking(entity);
		}
		return baseMapper.insertSelectiveByEntity(entity);
	}

	private void updateTreeableCascade(Entity entity) {
		if (requiredTreeableChecking() && Treeable.class.isAssignableFrom(entityClass)) {
			Treeable tree = (Treeable) getById(entity);
			String oldTreePath = StringUtils.trimToNull(tree.getParentPath());
			String newTreePath = ((Treeable) entity).getParentPath();
			if (/*添加上级*/(oldTreePath == null && newTreePath != null) ||
				/*移除上级*/ (oldTreePath != null && newTreePath == null && entity.getUpdatingNullFields() != null
				&& entity.getUpdatingNullFields().contains(tree.parentIdField())) ||
				/*修改上级*/ (oldTreePath != null && newTreePath != null && !oldTreePath.equalsIgnoreCase(newTreePath))
				) {
				// update children
				oldTreePath = ServiceHelper.appendTreePath(oldTreePath, tree);
				newTreePath = ServiceHelper.appendTreePath(newTreePath, tree);
				Example<Entity> example = Example.of(entityClass)
					.like(tree.parentPathField(), SearchOper.prefixLike.repair(oldTreePath))
					.build();
				List<Entity> children = baseMapper.selectByExample(example);
				if (children.size() > 0) {
					for (Entity child : children) {
						Treeable childTree = (Treeable) child;
						String childTreePath = childTree.getParentPath().replace(oldTreePath, newTreePath);
						childTree.setParentPath(childTreePath);
						EntityKit.fillUpdateDate(child);
						baseMapper.updateSelectiveByEntity(child);
					}
				}
			}
		}
	}

	/**
	 * 树结构实体填充 parentPath 值
	 * <ol>
	 * <li>parentId 的值不能是自身id, 否则报错</li>
	 * <li>parentId 为空或指向的父节点不存在时, 认为是清空上级节点操作,即上移至根节点,同步清空 parentPath</li>
	 * <li>通过 parentId 的值,同步更新 parentPath 的值 </li>
	 * </ol>
	 *
	 * @param entity
	 */
	private void fixTreeableParent(Entity entity) {
		if (requiredTreeableChecking() && Treeable.class.isAssignableFrom(entityClass)) {
			Serializable parentId = ((Treeable) entity).getParentId();
			if (parentId instanceof String) {
				parentId = StringUtils.trimToNull(parentId);
			}
			if (parentId != null) {
				if (parentId.equals(entity.getId())) {
					throw new ValidationException("ERR-02000", "自身不能作为自身的上级");
				}
				Treeable parent = (Treeable) getById((ID) parentId);
				if (parent == null) {
					ServiceHelper.makeParentToNull(entity);
					log.warn("parent not exists : {}", parentId);
				} else {
					String newTreePath = ServiceHelper.appendTreePath(parent.getParentPath(), parent);
					((Treeable) entity).setParentPath(newTreePath);
				}
			} else {
				ServiceHelper.makeParentToNull(entity);
			}
		}
	}

	public long[] doEnable(ID... ids) {
		long[] rs = new long[ids.length];
		for (int i = 0; i < ids.length; i++) {
			rs[i] = doEnable(ids[i]);
		}
		return rs;
	}

	public long[] doDisable(ID... ids) {
		long[] rs = new long[ids.length];
		for (int i = 0; i < ids.length; i++) {
			rs[i] = doDisable(ids[i]);
		}
		return rs;
	}

	public long doEnable(ID id) {
		return doEnableOrDisable(id, true);
	}

	public long doDisable(ID id) {
		return doEnableOrDisable(id, false);
	}

	private long doEnableOrDisable(ID id, boolean enabled) {
		if (entityClass != null && EnabledRecordable.class.isAssignableFrom(entityClass)) {
			Entity entity = EntityKit.createEmpty(entityClass);
			if (Versionable.class.isAssignableFrom(entityClass)) {
				Entity persisted = baseMapper.getById(id);
				((Versionable) entity).setVersion(((Versionable) persisted).getVersion());
			}
			entity.setId(id);
			((EnabledRecordable) entity).setEnabled(enabled);
			EntityKit.fillUpdateDate(entity);
			long rows = baseMapper.updateSelectiveByEntity(entity);
			checkVersion(rows, entity);
			return rows;
		}
		return 0;
	}

	public long update(Entity entity) {
		EntityKit.moveEmptyFieldsToNulls(entity);
		EntityKit.fillUpdateDate(entity);
		if (requiredUpdateChecking()) {
			doUpdateChecking(entity);
		}
		fixTreeableParent(entity);
		updateTreeableCascade(entity);
		long rows = baseMapper.updateByEntity(entity);
		checkVersion(rows, entity);
		return rows;
	}

	public long updateSelective(Entity entity) {
		EntityKit.moveEmptyFieldsToNulls(entity);
		EntityKit.fillUpdateDate(entity);
		if (requiredUpdateChecking()) {
			doUpdateChecking(entity);
		}
		fixTreeableParent(entity);
		updateTreeableCascade(entity);
		long rows = baseMapper.updateSelectiveByEntity(entity);
		checkVersion(rows, entity);
		return rows;
	}

	public Entity getById(ID id) {
		return baseMapper.getById(id);
	}

	public Entity getById(Entity entity) {
		return baseMapper.getByIdOfEntity(entity);
	}

	public Entity getById(Map<String, Object> param) {
		return baseMapper.getByIdOfMap(param);
	}

	public Entity get(Entity entity) {
		return baseMapper.getByEntity(entity);
	}

	public Entity get(Example<Entity> example) {
		return baseMapper.getByExample(example);
	}

	public Entity get(Map<String, Object> param) {
		return baseMapper.getByMap(param);
	}

	public boolean existsById(ID id) {
		return baseMapper.existsById(id);
	}

	public boolean existsById(Entity entity) {
		return baseMapper.existsByIdOfEntity(entity);
	}

	public boolean existsById(Map<String, Object> param) {
		return baseMapper.existsByIdOfMap(param);
	}

	public boolean exists(Entity entity) {
		return baseMapper.existsByEntity(entity);
	}

	public boolean exists(Example<Entity> example) {
		return baseMapper.existsByExample(example);
	}

	public boolean exists(Map<String, Object> param) {
		return baseMapper.existsByMap(param);
	}

	public long count() {
		return count(new HashMap<String, Object>());
	}

	public long count(Entity param) {
		return baseMapper.countByEntity(param);
	}

	public long count(Example<Entity> example) {
		return baseMapper.countByExample(example);
	}

	public long count(Map<String, Object> param) {
		return baseMapper.countByMap(param);
	}

	public List<Entity> findAll() {
		return findAll(Collections.emptyMap());
	}

	public List<Entity> findAll(Example<Entity> example) {
		return baseMapper.selectByExample(example);
	}

	public List<Entity> findAll(Entity param) {
		return baseMapper.selectByEntity(param);
	}

	public List<Entity> findAll(Map<String, Object> param) {
		return baseMapper.selectByMap(param);
	}


	public List<Entity> findAll(Sort sort) {
		orderBy(sort);
		return findAll();
	}

	public List<Entity> findAll(Example<Entity> example, Sort sort) {
		orderBy(sort);
		return findAll(example);
	}

	public List<Entity> findAll(Entity param, Sort sort) {
		orderBy(sort);
		return findAll(param);
	}

	public List<Entity> findAll(Map<String, Object> param, Sort sort) {
		orderBy(sort);
		return findAll(param);
	}

	protected void orderBy(Sort sort) {
		if (sort != null) {
			Iterator<Sort.Order> iter = sort.iterator();
			StringBuilder orderby = new StringBuilder();
			while (iter.hasNext()) {
				Sort.Order order = iter.next();
				if (orderby.length() > 0) {
					orderby.append(", ");
				}
				String property = order.getProperty();
				property = convertPropertyToColumn(entityClass, property);
				orderby.append(property).append(" ")
					.append(order.getDirection().name().toLowerCase());
			}
			if (orderby.length() > 0) {
				PageHelper.orderBy(orderby.toString());
			}
		}
	}

	protected Page<Entity> findByPage(Pageable pageable, Function<Pageable, Page<Entity>> function) {
		if (pageable == null) {
			pageable = new PageRequest(0, DataParam.DEFAULT_PAGE_SIZE);
		}
		// PageHelper start from 1
		PageHelper.startPage(pageable.getPageNumber() + 1, pageable.getPageSize());
		Sort sort = pageable.getSort();
		orderBy(sort);
		return function.apply(pageable);
	}

	protected String convertPropertyToColumn(Class<?> entityClass, String property) {
		return PersistentRecognizer.propertyToColumn(entityClass, property);
	}

	protected String convertPropertyToColumn(String property) {
		return PersistentRecognizer.propertyToColumn(entityClass, property);
	}

	public Page<Entity> findByPage(Pageable pageable) {
		return findByPage(Collections.emptyMap(), pageable);
	}

	public Page<Entity> findTop(int total) {
		return findByPage(Collections.emptyMap(), new PageRequest(0, total));
	}

	public Page<Entity> findByPage(Entity param, Pageable pageableParam) {
		return findByPage(pageableParam, pageable -> {
			List<Entity> list = baseMapper.selectByEntity(param);
			if (list instanceof com.github.pagehelper.Page) {
				long total = ((com.github.pagehelper.Page<Entity>) list).getTotal();
				return new WrappedPageImpl<>(list, pageable, total);
			} else {
				long total = baseMapper.countByEntity(param);
				return new WrappedPageImpl<>(list, pageable, total);
			}
		});
	}

	public Page<Entity> findTop(Entity param, int total) {
		return findByPage(param, new PageRequest(0, total));
	}

	public Page<Entity> findByPage(Example<Entity> example, Pageable pageableParam) {
		return findByPage(pageableParam, pageable -> {
			List<Entity> list = baseMapper.selectByExample(example);
			if (list instanceof com.github.pagehelper.Page) {
				long total = ((com.github.pagehelper.Page<Entity>) list).getTotal();
				return new WrappedPageImpl<>(list, pageable, total);
			} else {
				long total = baseMapper.countByExample(example);
				return new WrappedPageImpl<>(list, pageable, total);
			}
		});
	}

	public Page<Entity> findTop(Example<Entity> example, int total) {
		return findByPage(example, new PageRequest(0, total));
	}


	public Page<Entity> findByPage(Map<String, Object> param, Pageable pageableParam) {
		return findByPage(pageableParam, pageable -> {
			List<Entity> list = baseMapper.selectByMap(param);
			if (list instanceof com.github.pagehelper.Page) {
				long total = ((com.github.pagehelper.Page<Entity>) list).getTotal();
				return new WrappedPageImpl<>(list, pageable, total);
			} else {
				long total = baseMapper.countByMap(param);
				return new WrappedPageImpl<>(list, pageable, total);
			}
		});
	}

	public Page<Entity> findTop(Map<String, Object> param, int total) {
		return findByPage(param, new PageRequest(0, total));
	}

	public Page<Entity> findByPage(Data data) {
		boolean hasSubSearches = false;
		{
			List<SearchModel> searches = data.getBody().getSearches();
			for (SearchModel search : searches) {
				if (search.getKey() == null && search.getSearches() != null && search.getSearches().size() > 0) {
					hasSubSearches = true;
					break;
				}
			}
		}
		if (hasSubSearches) {
			Example<Entity> param = DataKit.toSimpleSearchExample(data, entityClass);
			Pageable pageable = data.getBody().getPageable();
			return findByPage(param, pageable);
		} else {
			Map<String, Object> param = DataKit.toSearchParam(data);
			Pageable pageable = data.getBody().getPageable();
			return findByPage(param, pageable);
		}
	}

	public List<Entity> findAll(Data data) {
		PageHelper.clearPage();
		return findAll(DataKit.toSearchParam(data));
	}

	public List<Entity> findAllBySort(Data data) {
		PageHelper.clearPage();
		orderBy(data.getBody().getSort());
		return findAll(DataKit.toSearchParam(data));
	}
}
