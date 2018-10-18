package io.microvibe.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ObjectUtil {
	public static class Key {
		private Object obj;

		public Key(Object obj) {
			this.obj = obj;
		}

		public Object getObject() {
			return obj;
		}
	}

	private ObjectUtil() {
	}

	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T clone(T t) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(t);
			oos.flush();
			ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bis);
			return (T) ois.readObject();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 返回参数中第一个不为null的对象，如不存在，返回null
	 *
	 * @param args
	 * @return
	 */
	public static Object coalesce(Object... args) {
		Object v = null;
		if (args.length > 0)
			for (Object arg : args) {
				if (arg == null) {
					continue;
				}
				if (arg instanceof String && ((String) arg).trim().equals("")) {
					continue;
				}
				v = arg;
				break;
			}
		return v;
	}

	/**
	 * 在Map参数列中，查找第一个存在指定Key的映射Value值且不为null的Map，并将该Value值返回，如不存在，返回null
	 *
	 * @param k
	 * @param map
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Object coalesce(Key key, Object... args) {
		if (key == null || key.obj == null) {
			return coalesce(args);
		}
		Object k = key.obj;
		Object v = null;
		if (args.length > 0)
			for (Object arg : args) {
				v = arg;
				if (v != null) {
					if (arg instanceof String && ((String) arg).trim().equals("")) {
						v = null;
						continue;
					} else if (arg instanceof Map) {
						v = ((Map) arg).get(k);
						if (v == null) {
							continue;
						}
						if (v instanceof String && ((String) v).trim().equals("")) {
							v = null;
							continue;
						}
					}
					break;
				}
			}
		return v;
	}

	/**
	 * 尝试计算对象占用的内存大小,不太准
	 *
	 * <pre>
	 * boolean 1 bytes
	 * byte 1 bytes
	 * char 2 bytes
	 * short 2 bytes
	 * int 4 bytes
	 * float 4 bytes
	 * long 8 bytes
	 * double 8 bytes
	 *
	 * ref 4 bytes
	 * Empty Object 8 bytes
	 * Array 12 bytes (Empty  Object + Array length)
	 * Object 8*n bytes
	 * </pre>
	 *
	 * @param obj
	 * @return 字节数
	 */
	public static int sizeOf(Object obj) {
		Set<Object> set = new HashSet<Object>();
		threadLocal.set(set);
		int size = innerSizeOf(obj);
		threadLocal.remove();
		return size;
	}

	private static int innerSizeOf(Object obj) {
		int size = 0;
		if (obj != null) {
			Set<Object> set = threadLocal.get();
			if (!set.contains(obj)) {
				set.add(obj);
				Class<?> clazz = obj.getClass();
				if (clazz == boolean.class) {
					size = 1;
				} else if (clazz == byte.class) {
					size = 1;
				} else if (clazz == char.class || clazz == short.class) {
					size = 2;
				} else if (clazz == int.class || clazz == float.class) {
					size = 4;
				} else if (clazz == long.class || clazz == double.class) {
					size = 8;
				} else if (clazz == String.class) {
					String s = (String) obj;
					size = 2 * s.length();
				} else if (clazz.isArray()) {
					size = 12;
					int length = Array.getLength(obj);
					for (int i = 0; i < length; i++) {
						size += innerSizeOf(Array.get(obj, i));
					}
				} else {
					size = 8;
					try {
						Field[] fields = clazz.getDeclaredFields();
						size += 4 * fields.length;
						for (Field field : fields) {
							if (!Modifier.isStatic(field.getModifiers())) {
								clazz = field.getType();
								if (clazz == boolean.class) {
									size += 1;
								} else if (clazz == byte.class) {
									size += 1;
								} else if (clazz == char.class || clazz == short.class) {
									size += 2;
								} else if (clazz == int.class || clazz == float.class) {
									size += 4;
								} else if (clazz == long.class || clazz == double.class) {
									size += 8;
								} else {
									try {
										size += innerSizeOf(field.get(obj));
									} catch (IllegalArgumentException e) {
										e.printStackTrace();
									} catch (IllegalAccessException e) {
										e.printStackTrace();
									}
								}
							}
						}
						int mask = 8 - size & 7;
						if (mask != 8) {
							size += mask;
						}
					} catch (SecurityException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return size;
	}

	private static final ThreadLocal<Set<Object>> threadLocal = new ThreadLocal<Set<Object>>();
}
