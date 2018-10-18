package io.microvibe.util.env;

import java.util.Properties;

public class ContextPropertiesUtil {
	private static ContextProperties contextProperties = new ContextProperties();

	public static Properties getProperties(String resource) {
		return contextProperties.getProperties(resource);
	}

	public static boolean containsKey(String resource, String key) {
		return contextProperties.containsKey(resource, key);
	}

	public static String getProperty(String resource, String key) {
		return contextProperties.getProperty(resource, key);
	}

}
