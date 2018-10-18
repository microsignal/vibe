package io.microvibe.booster.commons.crypto;

import io.microvibe.booster.commons.string.Strings;

import java.util.*;

public class CollectionUtil {

	/**
	 * 判断集合是否为空
	 *
	 * @param collection
	 * @return 如果为null或size=0则表示返回true
	 */
	public static <E> boolean isEmpty(Collection<E> collection) {
		return collection == null || collection.size() == 0;
	}

	/**
	 * 判断集合是否不为空
	 *
	 * @param collection
	 * @return
	 */
	public static <E> boolean isNotEmpty(Collection<E> collection) {
		return !isEmpty(collection);
	}

	/**
	 * 判断Map是否为空
	 *
	 * @param map 如果为null或size=0则表示返回true
	 * @return
	 */
	public static <K, V> boolean isEmpty(Map<K, V> map) {
		return map == null || map.size() == 0;
	}

	/**
	 * 判断Map是否不为空
	 *
	 * @param map
	 * @return
	 */
	public static <K, V> boolean isNotEmpty(Map<K, V> map) {
		return !isEmpty(map);
	}

	/**
	 * 判断对象是否为数组
	 *
	 * @param obj
	 * @return
	 */
	public static boolean isArray(Object obj) {
		if (obj == null) {
			return false;
		}
		return obj.getClass().isArray();
	}

	/**
	 * 判断对象是否为集合(Collection子类)
	 *
	 * @param obj
	 */
	public static boolean isCollection(Object obj) {
		if (obj == null) {
			return false;
		}
		return obj instanceof Collection;
	}

	/**
	 * 判断对象是否为Map(Map子类)
	 *
	 * @param obj
	 * @return
	 */
	public static boolean isMap(Object obj) {
		if (obj == null) {
			return false;
		}
		return obj instanceof Map;
	}

	/**
	 * 将集合类转为List&lt;Object&gt;适用于参数泛型要求
	 *
	 * @param collection
	 * @return
	 */
	public static List<Object> toObjList(Collection<?> collection) {
		List<Object> objList = new LinkedList<Object>();
		if (isNotEmpty(collection)) {
			for (Object obj : collection) {
				objList.add(obj);
			}
		}
		return objList;
	}

	/**
	 * 将集合类转为List&lt;String&gt;适用于参数泛型要求
	 *
	 * @param collection
	 * @return 注意：null值为会处理为字符串类型的"null"
	 */
	public static List<String> toStrList(Collection<?> collection) {
		return toStrList(collection, true, false);
	}

	/**
	 * 将集合类转为List&lt;String&gt;适用于参数泛型要求
	 *
	 * @param collection
	 * @param castNull   是否将null值转为字符串类型的"null"，优先级高于filterNull
	 * @param filterNull 是否过滤null值
	 * @return
	 */
	public static List<String> toStrList(Collection<?> collection, boolean castNull, boolean filterNull) {
		List<String> strList = new LinkedList<String>();
		if (isNotEmpty(collection)) {
			for (Object obj : collection) {
				if (obj != null) {
					strList.add(obj.toString());
				} else {
					if (castNull) {
						strList.add(Strings.NULL);
					} else {
						if (!filterNull) {
							strList.add(null);
						}
					}
				}
			}
		}
		return strList;
	}

	/**
	 * 将集合类转为List&lt;Integer&gt;适用于参数泛型要求
	 *
	 * @param collection
	 * @return 注意：null值为会被过滤
	 */
	public static List<Integer> toIntList(Collection<?> collection) {
		return toIntList(collection, true);
	}

	/**
	 * 将集合类转为List&lt;Integer&gt;适用于参数泛型要求
	 *
	 * @param collection
	 * @param filterNull 是否过滤null值
	 * @return
	 */
	public static List<Integer> toIntList(Collection<?> collection, boolean filterNull) {
		List<Integer> intList = new LinkedList<Integer>();
		if (isNotEmpty(collection)) {
			for (Object obj : collection) {
				if (obj != null) {
					Integer intVal = Strings.toInteger(obj.toString());
					if (intVal != null) {
						intList.add(intVal);
					} else {
						if (!filterNull) {
							intList.add(null);
						}
					}
				}
			}
		}
		return intList;
	}

	/**
	 * 集合去除重复元素
	 *
	 * @param collection
	 * @return 为null时返回空集合
	 */
	public static <T> Set<T> unique(Collection<T> collection) {
		if (isNotEmpty(collection)) {
			return new HashSet<T>(collection);
		}
		return new HashSet<T>();
	}

	/**
	 * 两个集合求并集
	 *
	 * @param collOne
	 * @param collTwo
	 * @return
	 */
	public static <T> List<T> union(final Collection<T> collOne, final Collection<T> collTwo) {
		List<T> result = new ArrayList<T>();
		if (isNotEmpty(collOne)) {
			result.addAll(collOne);
		}
		if (isNotEmpty(collTwo)) {
			result.addAll(collTwo);
		}
		return result;
	}

	/**
	 * 两个集合求交集
	 *
	 * @param collOne
	 * @param collTwo
	 * @return
	 */
	public static <T> List<T> intersection(final Collection<T> collOne, final Collection<T> collTwo) {
		List<T> result = new ArrayList<T>();
		if (isEmpty(collOne) || isEmpty(collTwo)) {
			return result;
		}
		Map<T, Integer> mapOne = getCountMap(collOne);
		Map<T, Integer> mapTwo = getCountMap(collTwo);
		Set<T> entrys = new HashSet<T>(collOne);
		entrys.addAll(collTwo);
		Iterator<T> itr = entrys.iterator();
		while (itr.hasNext()) {
			T obj = itr.next();
			for (int i = 0, m = Math.min(getFreq(obj, mapOne), getFreq(obj, mapTwo)); i < m; i++) {
				result.add(obj);
			}
		}
		return result;
	}

	// 从map中得到计数，当map不存在时返回0
	private static final <T> int getFreq(final T obj, final Map<T, Integer> freqMap) {
		Integer count = freqMap.get(obj);
		if (count != null) {
			return count.intValue();
		}
		return 0;
	}

	/**
	 * 将集合转换为计数Map
	 *
	 * @param collection
	 * @return
	 */
	public static <T> Map<T, Integer> getCountMap(final Collection<T> collection) {
		Map<T, Integer> count = new HashMap<T, Integer>();
		if (isNotEmpty(collection)) {
			for (Iterator<T> itr = collection.iterator(); itr.hasNext(); ) {
				T obj = itr.next();
				Integer c = (Integer) (count.get(obj));
				if (c == null) {
					count.put(obj, 1);
				} else {
					count.put(obj, new Integer(c.intValue() + 1));
				}
			}
		}
		return count;
	}

	/**
	 * 两个集合求补集（交集取反）
	 *
	 * @param collOne
	 * @param collTwo
	 * @return
	 */
	public static <T> List<T> disjunction(final Collection<T> collOne, final Collection<T> collTwo) {
		List<T> result = new ArrayList<T>();
		Map<T, Integer> mapOne = getCountMap(collOne);
		Map<T, Integer> mapTwo = getCountMap(collTwo);
		Set<T> entrys = new HashSet<T>(collOne);
		entrys.addAll(collTwo);
		Iterator<T> it = entrys.iterator();
		while (it.hasNext()) {
			T obj = it.next();
			for (int i = 0, m = ((Math.max(getFreq(obj, mapOne), getFreq(obj, mapTwo))) - (Math
				.min(getFreq(obj, mapOne), getFreq(obj, mapTwo)))); i < m; i++) {
				result.add(obj);
			}
		}
		return result;
	}

	/**
	 * 两个集合相减(差集)
	 *
	 * @param collFull
	 * @param collSub  被减去的集合
	 * @return
	 */
	public static <T> List<T> subtract(final Collection<T> collFull, final Collection<T> collSub) {
		ArrayList<T> result = new ArrayList<T>(collFull);
		Iterator<T> itr = collSub.iterator();
		while (itr.hasNext()) {
			result.remove(itr.next());
		}
		return result;
	}

	/**
	 * 判断两个集合是否有交集
	 *
	 * @param collOne
	 * @param collTwo
	 * @return 两个集合只要有一个相同元素则返回true
	 */
	public static <T> boolean containsAny(final Collection<T> collOne, final Collection<T> collTwo) {
		if (isEmpty(collOne) || isEmpty(collTwo)) {
			return false;
		}
		for (Iterator<T> itr = collOne.iterator(); itr.hasNext(); ) {
			if (collTwo.contains(itr.next())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断集合是否是另一个集合的子集
	 *
	 * @param collSub  子集合
	 * @param collFull 全集
	 * @return
	 */
	public static <T> boolean isSubCollection(final Collection<T> collSub, final Collection<T> collFull) {
		Map<T, Integer> mapOne = getCountMap(collSub);
		Map<T, Integer> mapTwo = getCountMap(collFull);
		Iterator<T> itr = collSub.iterator();
		while (itr.hasNext()) {
			T obj = itr.next();
			if (getFreq(obj, mapOne) > getFreq(obj, mapTwo)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 获取集合中指定位置的元素，如果元素的长度小于获取位置则返回null
	 *
	 * @param coll
	 * @param index
	 * @return
	 */
	public static <T> T getElement(Collection<T> coll, int index) {
		return getElement(coll, index, null);
	}

	/**
	 * 获取集合中指定位置的元素，如果元素的长度小于获取位置则返回默认值
	 *
	 * @param coll
	 * @param index
	 * @param defVal 指定返回的值
	 * @return
	 */
	public static <T> T getElement(Collection<T> coll, int index, T defVal) {
		if (coll == null || coll.size() == 0 || coll.size() < index + 1) {
			return defVal;
		}
		Iterator<T> itr = coll.iterator();
		int count = 0;
		while (itr.hasNext()) {
			T obj = itr.next();
			if (count == index) {
				return obj;
			}
			count++;
		}
		return defVal;
	}
}
