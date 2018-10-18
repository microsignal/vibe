package io.microvibe.booster.commons.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Qt
 * @since Jul 13, 2018
 */
public class ObjectUtils extends org.apache.commons.lang3.ObjectUtils {
	private static final ThreadLocal<Map<Object, Object>> dataCacheThreadLocal = new ThreadLocal<Map<Object, Object>>();
	private static final ThreadLocal<Integer> counterThreadLocal = new ThreadLocal<Integer>();


	public static String toString(final Object obj) {
		String str = "null";
		if (obj != null) {
			Class<?> clazz = obj.getClass();
			if (clazz.isArray()) {
				int length = Array.getLength(obj);
				if (length == 0) {
					str = "[]";
				} else {
					StringBuilder sb = new StringBuilder();
					sb.append("[").append(toString(Array.get(obj, 0)));
					for (int i = 1; i < length; i++) {
						sb.append(", ").append(toString(Array.get(obj, i)));
					}
					sb.append("]");
					str = sb.toString();
				}
			} else {
				return obj.toString();
			}
		}
		return str;
	}

	@SuppressWarnings("unchecked")
	public static Object trim(final Object o) {
		if (o == null) {
			return null;
		}
		if (o.getClass() == String.class) {
			return StringUtils.trim((String) o);
		}

		Map<Object, Object> map = dataCacheThreadLocal.get();
		if (map == null) {
			map = new ConcurrentHashMap<Object, Object>();
			dataCacheThreadLocal.set(map);
		} else if (map.containsKey(o)) {
			return o;
		}
		Integer counter = counterThreadLocal.get();
		if (counter == null) {
			counterThreadLocal.set(Integer.valueOf(0));
		} else {
			counterThreadLocal.set(counter + 1);
		}
		map.put(o, o);
		try {
			if (o instanceof Collection) {
				Collection<Object> collections = (Collection<Object>) o;
				Object[] array = collections.toArray();
				collections.clear();
				for (int i = 0; i < array.length; i++) {
					collections.add(trim(array[i]));
				}
			} else if (o.getClass().isArray()) {
				int length = Array.getLength(o);
				for (int i = 0; i < length; i++) {
					Object e = Array.get(o, i);
					Array.set(o, i, trim(e));
				}
			} else {
				BeanInfo info = Introspector.getBeanInfo(o.getClass(), Object.class);
				PropertyDescriptor[] pds = info.getPropertyDescriptors();
				for (PropertyDescriptor pd : pds) {
					Method readMethod = pd.getReadMethod();
					Method writeMethod = pd.getWriteMethod();
					if (writeMethod != null && readMethod != null) {
						Object v = readMethod.invoke(o);
						if (v != null) {
							if (pd.getPropertyType() == String.class) {
								writeMethod.invoke(o, StringUtils.trim((String) v));
							} else {
								writeMethod.invoke(o, trim(v));
							}
						}
					}
				}
			}
		} catch (Exception e) {
		} finally {
			if (counter == null) {
				dataCacheThreadLocal.remove();
				counterThreadLocal.remove();
			}
		}
		return o;
	}
}
