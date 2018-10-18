package io.microvibe.util.castor.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <pre>
 * 在使用map缓存一些数据时可能由于待添加元素因包含自身引用而调用默认hash函数导致堆栈异常,
 * 这时可考虑使用此类,在损失一些性能的情况下实现类似缓存
 * </pre>
 *
 * @author TQ
 * @since Oct 12, 2016
 * @param <K>
 * @param <V>
 */
class ObjectCache<K, V> {
	class Entry {
		K key;
		V value;

		public Entry(K k, V v) {
			this.key = k;
			this.value = v;
		}
	}

	private Map<K, V> cache0 = new ConcurrentHashMap<>();
	private Map<Class<?>, List<Entry>> cache = new ConcurrentHashMap<>();

	public void add(K key, V value) {
		if (key instanceof Collection || key instanceof Map) {
			Class<?> kc = key.getClass();
			List<Entry> list = cache.get(kc);
			if (list == null) {
				list = Collections.synchronizedList(new ArrayList<Entry>());
				list.add(new Entry(key, value));
				cache.put(kc, list);
			} else {
				boolean exists = false;
				for (ListIterator<Entry> iter = list.listIterator(); iter
						.hasNext();) {
					Entry entry = iter.next();
					if (entry.key == key) {
						iter.set(new Entry(key, value));
						exists = true;
						break;
					}
				}
				if (!exists) {
					list.add(new Entry(key, value));
				}
			}
		} else {
			cache0.put(key, value);
		}
	}

	public V get(K key) {
		if (key instanceof Collection || key instanceof Map) {
			Class<?> kc = key.getClass();
			List<Entry> list = cache.get(kc);
			if (list != null) {
				for (Entry entry : list) {
					if (entry.key == key) {
						return entry.value;
					}
				}
			}
		} else {
			return cache0.get(key);
		}
		return null;
	}

	public void clear() {
		cache.clear();
		cache0.clear();
	}
}
