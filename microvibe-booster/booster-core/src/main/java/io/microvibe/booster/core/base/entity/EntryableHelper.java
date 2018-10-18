package io.microvibe.booster.core.base.entity;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Qt
 * @since Jul 25, 2018
 */
class EntryableHelper {

	Map<String, Map<String, String>> entries = new ConcurrentHashMap<>();

	Map<String, String> entries(String type) {
		Map<String, String> map = entries.get(type);
		return map == null ? null : Collections.unmodifiableMap(map);
	}

	Map<String, String> entries(String type, Map<String, String> value) {
		return entries.put(type, value);
	}

	void entries(String type, String key, String value) {
		Map<String, String> map = entries.get(type);
		if (map == null) {
			map = Collections.synchronizedMap(new LinkedHashMap<>());
			entries.put(type, map);
		}
		map.put(key, value);
	}

	void alias(String type, String alias) {
		Map<String, String> map = entries.get(type);
		if (map != null && !entries.containsKey(alias)) {
			entries.put(alias, map);
		}
	}

	int size() {
		return entries.size();
	}

	boolean contains(Object type) {
		return entries.containsKey(type);
	}
}
