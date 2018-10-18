package io.microvibe.booster.commons.utils.property;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.util.TypeUtils;
import com.google.common.collect.Sets;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.NotThreadSafe;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;

@NotThreadSafe
public class PropertyUtil extends PropertyUtils {
	private static final Logger logger = LoggerFactory.getLogger(PropertyUtil.class);

	public static Object mergeWithout(Object dest, Object orig, String... excludes) {
		return merge(dest, orig, null, excludes);
	}

	public static Object mergeWith(Object dest, Object orig, String... includes) {
		return merge(dest, orig, includes, null);
	}

	public static Object merge(Object dest, Object orig) {
		return merge(dest, orig, null, null);
	}

	public static Object merge(Object dest, Object orig, String[] includes, String[] excludes) {
		if (orig == null || dest == null) {
			return dest;
		}
		Set<String> includesSet = includes == null ? new HashSet<>() : Sets.newHashSet(includes);
		Set<String> excludesSet = excludes == null ? new HashSet<>() : Sets.newHashSet(excludes);

		if (orig instanceof Map) {
			final Iterator<?> entries = ((Map<?, ?>) orig).entrySet().iterator();
			while (entries.hasNext()) {
				final Entry<?, ?> entry = (Entry<?, ?>) entries.next();
				final String name = (String) entry.getKey();
				if ((includesSet.isEmpty() || includesSet.contains(name))
					&& (excludesSet.isEmpty() || !excludesSet.contains(name))) {
					try {
						Object value = entry.getValue();
						if (value != null) {
							setProperty(dest, name, value);
						}
					} catch (Exception e) {
					}
				}
			}
		} else {
			final PropertyDescriptor[] origDescriptors = getPropertyDescriptors(orig);
			for (PropertyDescriptor origDescriptor : origDescriptors) {
				try {
					final String name = origDescriptor.getName();
					if ((includesSet.isEmpty() || includesSet.contains(name))
						&& (excludesSet.isEmpty() || !excludesSet.contains(name))) {
						Method readMethod = origDescriptor.getReadMethod();
						readMethod.setAccessible(true);
						Object value = readMethod.invoke(orig);
						if (value != null) {
							setProperty(dest, name, value);
						}
					}
				} catch (Exception e) {
				}
			}
		}
		return dest;
	}

	public static <T> PropertyBuilder<T> build(T dest) {
		return new StdBuilder<T>(dest);
	}

	public static <T> PropertyBuilder<T> build(Class<T> clazz) {
		return new StdBuilder<T>(clazz);
	}

	public static <T> PropertyBuilder<T> build(Object orig, T dest) {
		return new StdBuilder<T>(orig, dest);
	}

	public static PropertyBuilder<List<Object>> buildList(List<Object> list, Class<?> clazz) {
		return new ListBuilder(list, clazz);
	}

	public static PropertyBuilder<List<Object>> buildList(List<Object> list, Class<?> clazz, int size) {
		return new ListBuilder(list, clazz, size);
	}

	public static PropertyBuilder<List<Object>> buildList(Class<?> clazz) {
		return new ListBuilder(null, clazz);
	}

	public static PropertyBuilder<List<Object>> buildList(Class<?> clazz, int size) {
		return new ListBuilder(null, clazz, size);
	}

	public static PropertyBuilder<List<Object>> buildList(List<Object> list) {
		return new ListBuilder(list, null);
	}

	public static PropertyBuilder<List<Object>> buildList(List<Object> list, int size) {
		return new ListBuilder(list, null, size);
	}

	public static Object getPathProperty(Object o, String property) {
		return getProperty(o, parseProperty(property));
	}

	public static String getPathPropertyAsString(Object o, String property) {
		return (String) getPathProperty(o, property);
	}

	public static Boolean getPathPropertyAsBoolean(Object o, String property) {
		return ((Boolean) getPathProperty(o, property));
	}

	public static void setPathProperty(Object o, String property, Object val) {
		setProperty(o, parseProperty(property), val);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	static void setProperty(Object o, Deque<String> properties, Object val) {
		String property = properties.pollLast();
		Object matrix = o;
		if (!properties.isEmpty()) {
			matrix = getProperty(o, properties);
		}
		try {
			if (matrix != null) {
				if (matrix instanceof List) {
					((List) matrix).set(Integer.parseInt(property), val);
				} else if (matrix.getClass().isArray()) {
					Array.set(matrix, Integer.parseInt(property), val);
				} else if (matrix instanceof Collection) {
					List list = new ArrayList<>(((Collection) matrix));
					list.set(Integer.parseInt(property), val);
					((Collection) matrix).clear();
					((Collection) matrix).addAll(list);
					list.clear();
				} else {
					PropertyDescriptor pd = PropertyUtils.getPropertyDescriptor(matrix, property);
					if (pd != null && !pd.getPropertyType().isAssignableFrom(val.getClass())) {
						val = TypeUtils.cast(val, pd.getPropertyType(), ParserConfig.getGlobalInstance());
					}
					PropertyUtils.setProperty(matrix, property, val);
				}
			}
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}
	}

	@SuppressWarnings("rawtypes")
	static Object getProperty(Object o, Deque<String> properties) {
		Object val = o;
		for (String property : properties) {
			try {
				Object matrix = val;
				if (matrix instanceof List) {
					val = ((List) matrix).get(Integer.parseInt(property));
				} else if (matrix.getClass().isArray()) {
					val = Array.get(matrix, Integer.parseInt(property));
				} else if (matrix instanceof Collection) {
					Iterator iter = ((Collection) matrix).iterator();
					int idx = Integer.parseInt(property);
					for (int i = 0; i < idx; i++) {
						iter.next();
					}
					val = iter.next();
				} else {
					val = PropertyUtils.getProperty(val, property);
				}
				if (val == null) {
					break;
				}
			} catch (Exception e) {
				logger.debug(e.getMessage(), e);
				return null;
			}
		}
		return val;
	}

	static Deque<String> parseProperty(String property) {
		char[] charArray = property.toCharArray();
		StringBuilder sb = new StringBuilder();
		boolean escape = false;
		boolean bracket = false;
		Deque<String> queue = new ArrayDeque<>();
		for (char c : charArray) {
			if (escape) {
				sb.append(c);
				escape = false;
				continue;
			} else if (c == '\\') {
				escape = true;
				continue;
			}
			if (bracket) {
				if (c == ']') {
					bracket = false;
					if (sb.length() > 0) {
						queue.add(sb.toString());
						sb.delete(0, sb.length());
					}
					continue;
				}
			} else {
				if (c == '.') {
					if (sb.length() > 0) {
						queue.add(sb.toString());
						sb.delete(0, sb.length());
					}
					continue;
				} else if (c == '[') {
					bracket = true;
					if (sb.length() > 0) {
						queue.add(sb.toString());
						sb.delete(0, sb.length());
					}
					continue;
				} else if (c == ']') {
					throw new IllegalArgumentException(property);
				}
			}
			sb.append(c);
		}
		if (escape || bracket) {
			throw new IllegalArgumentException(property);
		}
		if (sb.length() > 0) {
			queue.add(sb.toString());
		}
		return queue;
	}

}
