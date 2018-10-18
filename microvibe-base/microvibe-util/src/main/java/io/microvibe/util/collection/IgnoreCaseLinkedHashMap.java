package io.microvibe.util.collection;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class IgnoreCaseLinkedHashMap<K, V> implements Map<K, V> {

	private Map<K, V> map;

	public IgnoreCaseLinkedHashMap() {
		map = new LinkedHashMap<K, V>();
	}

	public IgnoreCaseLinkedHashMap(int initialCapacity) {
		map = new LinkedHashMap<K, V>(initialCapacity);
	}

	public IgnoreCaseLinkedHashMap(int initialCapacity, float loadFactor) {
		map = new LinkedHashMap<K, V>(initialCapacity, loadFactor);
	}

	@SuppressWarnings("unchecked")
	static <T> T toUpperCase(T key) {
		if (key.getClass() == String.class) {
			key = (T) ((String) key).toUpperCase();
		}
		return key;
	}

	public int size() {
		return map.size();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public boolean containsKey(Object key) {
		return map.containsKey(toUpperCase(key));
	}

	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	public V get(Object key) {
		return map.get(toUpperCase(key));
	}

	public V put(K key, V value) {
		return map.put(toUpperCase(key), value);
	}

	public V remove(Object key) {
		return map.remove(toUpperCase(key));
	}

	public void putAll(Map<? extends K, ? extends V> m) {
		if (m.getClass() == IgnoreCaseLinkedHashMap.class) {
			map.putAll(m);
		} else {
			for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
				K key = entry.getKey();
				map.put(toUpperCase(key), entry.getValue());
			}
		}
	}

	public void clear() {
		map.clear();
	}

	public Set<K> keySet() {
		return map.keySet();
	}

	public Collection<V> values() {
		return map.values();
	}

	public Set<Map.Entry<K, V>> entrySet() {
		return map.entrySet();
	}

}
