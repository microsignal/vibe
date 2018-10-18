package io.microvibe.booster.core.lang;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class LocalDataBinding {

	private static final ThreadLocal<Map<String, Object>> local = new ThreadLocal<Map<String, Object>>() {
		protected Map<String, Object> initialValue() {
			return new LinkedHashMap<>();
		}
	};

	public static Map<String, Object> getBindings() {
		return local.get();
	}

	public static void clear() {
		local.remove();
	}

	public static Object put(String key, Object value) {
		return local.get().put(key, value);
	}

	public static Object get(String key) {
		return local.get().get(key);
	}

	public static boolean containsKey(Object key) {
		return local.get().containsKey(key);
	}

	public static Object remove(Object key) {
		return local.get().remove(key);
	}
}
