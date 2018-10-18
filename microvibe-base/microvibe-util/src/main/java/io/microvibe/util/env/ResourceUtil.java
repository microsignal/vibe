package io.microvibe.util.env;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class ResourceUtil {
	private ResourceUtil() {
	}

	private static final Map<Locale, Map<String, ResourceBundle>> RESOURCE_BUNDLE_CACHE = new HashMap<Locale, Map<String, ResourceBundle>>();

	public static ResourceBundle getResourceBundle(String baseName) {
		return getResourceBundle(baseName, LocaleContext.getCurrentLocale());
	}

	public static ResourceBundle getResourceBundle(String baseName, Locale locale) {
		if (!RESOURCE_BUNDLE_CACHE.containsKey(locale)) {
			RESOURCE_BUNDLE_CACHE.put(locale, new HashMap<String, ResourceBundle>());
		}
		Map<String, ResourceBundle> map = RESOURCE_BUNDLE_CACHE.get(locale);
		ResourceBundle bundle = null;
		if (!map.containsKey(baseName)) {
			synchronized (ResourceUtil.class) {
				if (!map.containsKey(baseName)) {
					try {
						bundle = ResourceBundle.getBundle(baseName);
					} catch (Exception e) {
					}
					map.put(baseName, bundle);
				}
			}
		}
		return map.get(baseName);
	}

	public static String getString(String baseName, String key) {
		return getString(baseName, key, LocaleContext.getCurrentLocale());
	}

	public static String getString(String baseName, String key, Locale locale) {
		ResourceBundle bundle = getResourceBundle(baseName, locale);
		if (bundle == null) return null;
		try {
			return bundle.getString(key);
		} catch (Exception e) {
			return null;
		}
	}
}
