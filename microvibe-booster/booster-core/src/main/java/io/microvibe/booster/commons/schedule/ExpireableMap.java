package io.microvibe.booster.commons.schedule;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Qt
 * @since Jun 15, 2018
 */
public class ExpireableMap<K, V> implements Map<K, V>, Serializable, Cloneable {

	private Map<K, ExpireableElement<V>> data;
	private long expireSeconds = 0;

	public ExpireableMap(long expireSeconds) {
		this.expireSeconds = expireSeconds;
		this.data = Collections.synchronizedMap(new LinkedHashMap<>());
	}

	public long getExpireSeconds() {
		return expireSeconds;
	}

	@Override
	public String toString() {
		return data.toString();
	}

	@Override
	public int size() {
		return data.size();
	}

	@Override
	public boolean isEmpty() {
		return data.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return data.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return data.containsValue(ExpireableElement.of(value));
	}

	@Override
	public V get(Object key) {
		ExpireableElement<V> e = data.get(key);
		return e == null ? null : e.getValue();
	}

	@Override
	public V put(K key, V value) {
		ExpireableElement<V> wrapper = ExpireableElement.of(value);
		ExpireableElement<V> e = data.put(key, wrapper);
		long delay = wrapper.getBirthday() - System.currentTimeMillis() + expireSeconds * 1000;
		Schedules.executor().schedule(() -> {
			ExpireableElement<V> element = data.get(key);
			if (element == wrapper) {
				data.remove(key);
			}
		}, delay, TimeUnit.MILLISECONDS);
		return e == null ? null : e.getValue();
	}

	@Override
	public V remove(Object key) {
		ExpireableElement<V> e = data.remove(key);
		return e == null ? null : e.getValue();
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		Set<? extends Entry<? extends K, ? extends V>> set = m.entrySet();
		for (Entry<? extends K, ? extends V> entry : set) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void clear() {
		data.clear();
	}

	@Override
	public Set<K> keySet() {
		Set<K> set = new LinkedHashSet<>(data.keySet());
		return set;
	}

	@Override
	public Collection<V> values() {
		List<V> values = new ArrayList<>();
		Collection<ExpireableElement<V>> es = data.values();
		for (ExpireableElement<V> e : es) {
			values.add(e.getValue());
		}
		return values;
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		Set<Entry<K, ExpireableElement<V>>> entrySet = data.entrySet();
		Set<Entry<K, V>> set = new LinkedHashSet();
		for (Entry<K, ExpireableElement<V>> e : entrySet) {
			Entry<K, V> entry = new Entry<K, V>() {
				@Override
				public K getKey() {
					return e.getKey();
				}

				@Override
				public V getValue() {
					return e.getValue().getValue();
				}

				@Override
				public V setValue(V value) {
					V old = e.getValue().getValue();
					e.getValue().setValue(value);
					return old;
				}
			};
		}

		return set;
	}


	@Getter
	@EqualsAndHashCode
	static class ExpireableElement<V> implements Serializable, Cloneable {
		private long birthday;
		@Setter
		private V value;

		private ExpireableElement(V value) {
			this.value = value;
			this.birthday = System.currentTimeMillis();
		}

		static <V> ExpireableElement<V> of(V value) {
			return new ExpireableElement(value);
		}

		@Override
		public String toString() {
			return String.valueOf(value);
		}
        /*
        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj.getClass() != ExpireableElement.class) return false;
            return value.equals(((ExpireableElement) obj).value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }*/
	}
}
