package io.microvibe.booster.core.base.search.builder.support;

import com.google.common.collect.Lists;
import io.microvibe.booster.core.accessor.ConversionServiceAccessor;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.ObjectUtils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class Conversions {

	@SuppressWarnings({"rawtypes"})
	public static Optional<Object> convert(Class<?> entityClass, String key, Optional<Object> opt) {
		Object val = opt.get();
		boolean isCollection = val instanceof Collection;
		boolean isArray = val != null && val.getClass().isArray();
		if (isCollection || isArray) {
			List<Object> list = Lists.newArrayList();
			if (isCollection) {// collection
				for (Object o : (Collection) val) {
					list.add(toConvertedValue(entityClass, key, o));
				}
				// list.addAll((Collection) val);
			} else {// array
				Object[] arr = ObjectUtils.toObjectArray(val);
				for (Object o : arr) {
					list.add(toConvertedValue(entityClass, key, o));
				}
				// list = Lists.newArrayList(CollectionUtils.arrayToList(val));
			}
			// int length = list.size();
			// for (int i = 0; i < length; i++) {
			// list.set(i, toConvertedValue(entityClass, key, list.get(i)));
			// }
			return Optional.of(list);
		} else {
			return Optional.of(toConvertedValue(entityClass, key, val));
		}
	}

	public static Object toConvertedValue(Class<?> entityClass, String key, Object val) {
		if (val == null) {
			return null;
		}
		try {
			BeanWrapper beanWrapper = new BeanWrapperImpl(entityClass);
			return ConversionServiceAccessor.getConversionService().convert(val, beanWrapper.getPropertyType(key));
		} catch (Exception e) {
			throw new IllegalArgumentException("value object cannot be converted to property type: "
				+ entityClass + "." + key, e);
		}
	}
}
