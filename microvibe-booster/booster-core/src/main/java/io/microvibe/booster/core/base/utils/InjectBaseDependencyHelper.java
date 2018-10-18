package io.microvibe.booster.core.base.utils;

import io.microvibe.booster.core.base.annotation.BaseComponent;
import com.google.common.collect.Sets;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * 注入 @BaseComponent 的工具类
 *
 * @author Qt
 */
public class InjectBaseDependencyHelper {

	public static <T> T findBaseComponent(Object bean, Class<T> type) {
		final Set<Object> candidates = InjectBaseDependencyHelper.findDependencies(bean, BaseComponent.class);
		for (Object o : candidates) {
			if (type.isAssignableFrom(o.getClass())) {
				return (T) o;
			}
		}
		return null;
	}

	/**
	 * 根据注解在目标对象上的字段上查找依赖
	 *
	 * @param target
	 * @param annotation
	 */
	public static Set<Object> findDependencies(final Object target, final Class<? extends Annotation> annotation) {

		final Set<Object> candidates = Sets.newHashSet();

		ReflectionUtils.doWithFields(
			target.getClass(),
			new ReflectionUtils.FieldCallback() {
				@Override
				public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
					ReflectionUtils.makeAccessible(field);
					Object obj = ReflectionUtils.getField(field, target);
					candidates.add(obj);
				}
			},
			new ReflectionUtils.FieldFilter() {
				@Override
				public boolean matches(Field field) {
					return field.isAnnotationPresent(annotation);
				}
			}
		);

		ReflectionUtils.doWithMethods(
			target.getClass(),
			new ReflectionUtils.MethodCallback() {
				@Override
				public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
					ReflectionUtils.makeAccessible(method);
					PropertyDescriptor descriptor = BeanUtils.findPropertyForMethod(method);
					candidates.add(ReflectionUtils.invokeMethod(descriptor.getReadMethod(), target));
				}
			},
			new ReflectionUtils.MethodFilter() {
				@Override
				public boolean matches(Method method) {
					boolean hasAnnotation = false;
					hasAnnotation = method.isAnnotationPresent(annotation);
					if (!hasAnnotation) {
						return false;
					}

					boolean hasReadMethod = false;
					PropertyDescriptor descriptor = BeanUtils.findPropertyForMethod(method);
					hasReadMethod = descriptor != null && descriptor.getReadMethod() != null;

					if (!hasReadMethod) {
						return false;
					}

					return true;
				}
			}
		);

		return candidates;
	}

}
