package io.microvibe.booster.core.base.persistence;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class FieldReflectUtil {


	/**
	 * 设定目标对象指定的字段值
	 *
	 * @param target 对象
	 * @param field  字段
	 * @throws RuntimeException
	 */
	public static <T> void setFieldValue(T target, Field field, Object value) {
		ReflectionUtils.makeAccessible(field);
		ReflectionUtils.setField(field, target, value);
	}

	/**
	 * 获取目标对象指定的字段值
	 *
	 * @param target 对象
	 * @param field  字段
	 * @throws RuntimeException
	 */
	public static <T> Object getFieldValue(T target, Field field) throws Exception {
		ReflectionUtils.makeAccessible(field);
		return ReflectionUtils.getField(field, target);
	}

	/**
	 * 获取class类中指定注解类型的field对象
	 *
	 * @param clazz          pojo类-class对象
	 * @param annotationType 注解类-class对象
	 * @return Field or null
	 */
	public static Field getField(Class<?> clazz, Class<? extends Annotation> annotationType) {
		List<Field> fields = getFields(clazz, annotationType);
		return fields.size() == 0 ? null : fields.get(0);
	}

	public static Field getField(Class<?> clazz, String name) {
		List<Field> fields = getFields(clazz, name);
		return fields.size() == 0 ? null : fields.get(0);
	}

	public static List<Field> getFields(Class<?> clazz) {
		return getFields(clazz, field -> true);
	}

	public static List<Field> getFields(Class<?> clazz, Class<? extends Annotation> annotationType) {
		return getFields(clazz, field -> AnnotationUtils.findAnnotation(field, annotationType) != null);
	}

	public static List<Field> getFields(Class<?> clazz, String name) {
		return getFields(clazz, field -> field.getName().equalsIgnoreCase(name));
	}

	/**
	 * 获取所有字段
	 *
	 * @param clazz class对象
	 * @return
	 */
	public static List<Field> getFields(Class<?> clazz, Function<Field, Boolean> filter) {
		Map<String, Field> map = new LinkedHashMap<>();

		List<Field> list = new ArrayList<>();
		Class<?> searchType = clazz;
		while (!Object.class.equals(searchType) && searchType != null) {
			Field[] fields = searchType.getDeclaredFields();
			for (Field field : fields) {
				if (filter != null) {
					if (!filter.apply(field)) {
						continue;
					}
				}

				if (!map.containsKey(field.getName())) {
					map.put(field.getName(), field);
					list.add(field);
				}
			}
			searchType = searchType.getSuperclass();
		}
		return list;
	}
}
