package io.microvibe.booster.core.base.persistence;

import io.microvibe.booster.commons.utils.StringUtils;
import io.microvibe.booster.core.base.utils.NameCastor;
import org.springframework.beans.BeanUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PersistentRecognizer {
	/**
	 * 驼峰_下划线转换,默认开启
	 */
	private static final boolean CAMEL_TO_UNDERLINE = true;
	private static final Map<Class<?>, EntityMetaData> classMeta = new ConcurrentHashMap<>();
	private static final Map<String, EntityMetaData> nameMeta = new ConcurrentHashMap<>();

	public static EntityMetaData entityMetaData(String entityName) {
		return nameMeta.get(entityName);
	}

	public static EntityMetaData entityMetaData(Class<?> entityClass) {
		EntityMetaData entityMetaData = classMeta.get(entityClass);
		if (entityMetaData != null) {
			return entityMetaData;
		}
		synchronized (PersistentRecognizer.class) {
			entityMetaData = classMeta.get(entityClass);
			if (entityMetaData != null) {
				return entityMetaData;
			}
			entityMetaData = new EntityMetaData(entityClass);
			classMeta.put(entityClass, entityMetaData);
			nameMeta.put(entityMetaData.getEntityName(), entityMetaData);
			return entityMetaData;
		}
	}

	public static FieldMetaData fieldMetaData(String entityName, String javaProperty) {
		return entityMetaData(entityName).getFieldMetaData(javaProperty);
	}

	public static FieldMetaData fieldMetaData(Class<?> entityClass, String javaProperty) {
		return entityMetaData(entityClass).getFieldMetaData(javaProperty);
	}

	public static String getEntityName(Class<?> type) {
		if (type.isAnnotationPresent(Entity.class)) {
			Entity entity = type.getAnnotation(Entity.class);
			if (!entity.name().trim().equals("")) {
				return entity.name();
			}
		}
		return type.getSimpleName();
	}

	/**
	 * 获取Java对象对应的表名</br>
	 * 默认下划线风格
	 *
	 * @param clazz pojo类class对象
	 * @return tableName
	 */
	public static String getTableName(Class<?> clazz) {
		// 判断是否有Table注解
		if (clazz.isAnnotationPresent(Table.class)) {
			// 获取注解对象
			Table table = clazz.getAnnotation(Table.class);
			// 设置了name属性
			if (!table.name().trim().equals("")) {
				return table.name();
			}
		}
		// 类名
		String className = clazz.getSimpleName();

		if (!CAMEL_TO_UNDERLINE) {
			return className;
		} else {
			// 驼峰转下划线
			return NameCastor.camelToLowerUnderline(className);
		}
	}

	/**
	 * 获取列名</br>
	 * 注解优先，javax.persistence.Column name属性值。</br>
	 * 无注解,将字段名转为字符串,默认下划线风格.</br>
	 *
	 * @param field pojo字段对象
	 * @return
	 */
	public static String getColumnName(Field field) {
		if (field.isAnnotationPresent(javax.persistence.Column.class)) {
			// 获取注解对象
			Column column = field.getAnnotation(Column.class);
			// 设置了name属性
			if (!column.name().trim().equals("")) {
				return column.name();
			}
		}
		if (!CAMEL_TO_UNDERLINE) {
			return field.getName();
		} else {
			return NameCastor.camelToLowerUnderline(field.getName());
		}
	}

	/**
	 * 属性转列名
	 *
	 * @param entityClass
	 * @param property
	 * @return
	 */
	public static String propertyToColumn(Class<?> entityClass, String property) {
		if (entityClass != null) {
			try {
				int i = property.indexOf('.');
				if (i > 0) {
					String sub = property.substring(0, i);
					String subKey = property.substring(i + 1);
					EntityMetaData entityMetaData = PersistentRecognizer.entityMetaData(entityClass);
					JoinMetaData joinMetaData = entityMetaData.getJoinMetaData(sub);
					if (joinMetaData != null) {
						EntityMetaData tableMetaData = joinMetaData.getTableMetaData();
						FieldMetaData fieldMetaData = tableMetaData.getFieldMetaData(subKey);
						return sub + "." + fieldMetaData.getColumnName();
					} else {
						PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(entityMetaData.getEntityClass(), sub);
						if (propertyDescriptor != null) {
							Class<?> propertyType = propertyDescriptor.getPropertyType();
							EntityMetaData tableMetaData = PersistentRecognizer.entityMetaData(propertyType);
							FieldMetaData fieldMetaData = tableMetaData.getFieldMetaData(subKey);
							return sub + "." + fieldMetaData.getColumnName();
						}
					}
				}else {
					EntityMetaData entityMetaData = PersistentRecognizer.entityMetaData(entityClass);
					FieldMetaData fieldMetaData = entityMetaData.getFieldMetaData(property);
					return fieldMetaData.getColumnName();
				}
			} catch (Exception e) {
			}
		}
		return property;
	}

	/**
	 * 列名转属性名
	 *
	 * @param fieldName
	 * @param entityClass
	 * @return
	 */
	public static String columnToProperty(String fieldName, Class<?> entityClass) {
		if (StringUtils.isBlank(fieldName) || entityClass == null) {
			return null;
		}
		Field[] fields = entityClass.getDeclaredFields();
		for (Field field : fields) {
			Column column = field.getAnnotation(Column.class);
			if (column != null && fieldName.equals(column.name())) {
				return field.getName();
			}
		}
		return null;
	}

}
