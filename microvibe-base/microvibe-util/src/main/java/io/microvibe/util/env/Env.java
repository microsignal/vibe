package io.microvibe.util.env;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Env {
	private static Map<String, String> env = new ConcurrentHashMap<String, String>();

	public static String get(String key) {
		return get(key, null);
	}

	public static String get(String key, String defaultVal) {
		String value = env.get(key);
		if (value == null) {
			value = System.getProperty(key);
		}
		if (value == null) {
			value = System.getenv(key);
		}
		if (value == null) {
			value = defaultVal;
		}
		return value;
	}

	public static void set(String key, String value) {
		env.put(key, value);
	}
}
