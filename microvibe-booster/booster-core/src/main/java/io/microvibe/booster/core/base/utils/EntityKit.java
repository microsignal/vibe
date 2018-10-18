package io.microvibe.booster.core.base.utils;

import io.microvibe.booster.commons.utils.StringUtils;
import io.microvibe.booster.core.base.entity.*;
import io.microvibe.booster.core.base.shiro.SessionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * @author Qt
 * @since Jun 29, 2018
 */
@Slf4j
public class EntityKit {

	/**
	 * 创建一个空的实体对象
	 *
	 * @param entityClass
	 * @param <Entity>
	 * @return
	 */
	public static <Entity extends Serializable> Entity createEmpty(Class<Entity> entityClass) {
		Entity entity = create(entityClass);
		PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(entityClass);
		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
			try {
				Class<?> propertyType = propertyDescriptor.getPropertyType();
				if (!propertyType.isPrimitive()) {
					Method writeMethod = propertyDescriptor.getWriteMethod();
					ReflectionUtils.invokeMethod(writeMethod, entity, new Object[]{null});
				}
			} catch (Exception e) {
				log.trace(e.getMessage(), e);
			}
		}
		return entity;
	}

	/**
	 * 将值为空字符串的字段修改为NULL值
	 *
	 * @param entity
	 * @param <Entity>
	 * @return
	 */
	public static <Entity extends Serializable> Entity clearEmptyFields(Entity entity) {
		Class<Entity> entityClass = (Class<Entity>) entity.getClass();
		PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(entityClass);
		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
			try {
				Class<?> propertyType = propertyDescriptor.getPropertyType();
				if (!propertyType.isPrimitive()) {
					Method writeMethod = propertyDescriptor.getWriteMethod();
					Method readMethod = propertyDescriptor.getReadMethod();
					Object val = ReflectionUtils.invokeMethod(readMethod, entity);
					if (val != null && val instanceof CharSequence && StringUtils.isBlank((CharSequence) val)) {
						ReflectionUtils.invokeMethod(writeMethod, entity, new Object[]{null});
					}
				}
			} catch (Exception e) {
				log.trace(e.getMessage(), e);
			}
		}
		return entity;
	}

	/**
	 * 将值为空字符串中的字段移至实体的空值更新列表中
	 *
	 * @param entity
	 * @param <Entity>
	 * @return
	 */
	public static <Entity extends Serializable> Entity moveEmptyFieldsToNulls(Entity entity) {
		if (entity instanceof NullUpdateable) {
			Class<Entity> entityClass = (Class<Entity>) entity.getClass();
			PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(entityClass);
			for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
				try {
					Class<?> propertyType = propertyDescriptor.getPropertyType();
					if (!propertyType.isPrimitive()) {
						Method readMethod = propertyDescriptor.getReadMethod();
						Object val = ReflectionUtils.invokeMethod(readMethod, entity);
						if (val != null && val instanceof CharSequence && StringUtils.isBlank((CharSequence) val)) {
							if (ReflectionUtils.findField(entityClass, propertyDescriptor.getName()) != null) {
								((NullUpdateable) entity).addUpdatingNullField(propertyDescriptor.getName());
							}
						}
					}
				} catch (Exception e) {
					log.trace(e.getMessage(), e);
				}
			}
			return entity;
		} else {
			return clearEmptyFields(entity);
		}
	}

	/**
	 * 创建一个默认实体对象
	 *
	 * @param entityClass
	 * @param <Entity>
	 * @return
	 */
	public static <Entity extends Serializable> Entity create(Class<Entity> entityClass) {
		Entity entity = BeanUtils.instantiateClass(entityClass);
		return entity;
	}

	/**
	 * 尝试填充实体对象的主键字段
	 *
	 * @param entity
	 * @param <Entity>
	 */
	public static <Entity extends Serializable> void fillKeyFields(Entity entity) {
		if (entity instanceof UuidHexSettable) {
			UuidHexSettable.set((UuidHexSettable) entity);
		} else if (entity instanceof UuidSettable) {
			UuidSettable.set((UuidSettable) entity);
		}
	}

	/**
	 * 填充实体对象的创建时间/创建人等信息
	 *
	 * @param entity
	 * @param <Entity>
	 */
	public static <Entity extends Serializable> void fillCreateDate(Entity entity) {
		if (entity instanceof CreateDateRecordable) {
			CreateDateRecordable bean = (CreateDateRecordable) entity;
			bean.setCreateDate(new Date());
		}
		if (entity instanceof CreateUserRecordable && SessionUtils.isAuthenticated()) {
			try {
				CreateUserRecordable bean = (CreateUserRecordable) entity;
				Serializable currentUserId = SessionUtils.getCurrentUserId();
				bean.setCreateUser(currentUserId);
			} catch (Exception e) {// 忽略会话异常
				log.trace(e.getMessage(), e);
			}
		}
	}

	/**
	 * 填充实体对象的修改时间/修改人等信息
	 *
	 * @param entity
	 * @param <Entity>
	 */
	public static <Entity extends Serializable> void fillUpdateDate(Entity entity) {
		if (entity instanceof UpdateDateRecordable) {
			UpdateDateRecordable bean = (UpdateDateRecordable) entity;
			bean.setUpdateDate(new Date());
		}
		if (entity instanceof UpdateUserRecordable && SessionUtils.isAuthenticated()) {
			try {
				UpdateUserRecordable bean = (UpdateUserRecordable) entity;
				Serializable currentUserId = SessionUtils.getCurrentUserId();
				bean.setUpdateUser(currentUserId);
			} catch (Exception e) {// 忽略会话异常
				log.trace(e.getMessage(), e);
			}
		}
	}
}
