package io.microvibe.booster.core.api.tools;

import io.microvibe.booster.core.api.tools.impl.DataKeyExtracterSupport;

import java.util.Map;

/**
 * @author Qt
 * @since Jun 30, 2018
 */
public interface DataKeyExtracter<T extends DataKeyExtracter<T>> {

	public static DataKeyExtracter<?> config() {
		return new DataKeyExtracterSupport();
	}

	T mappingAllUnderlineToCamel(Class<?> entityClass);

	T mappingAllCamelToUnderline(Class<?> entityClass);

	T mapping(Map<String, String> mapping);

	T mapping(String origKey, String destKey);

	T excludeAllUnderlineFormat(Class<?> entityClass);

	T excludeAll(Class<?> entityClass);

	T exclude(String... keys);

	T includeAllUnderlineFormat(Class<?> entityClass);

	T includeAll(Class<?> entityClass);

	T include(String... keys);

	T defaultIncluded(boolean defaultIncluded);

	String getMappingKey(String key);

	boolean isExcluded(String key);

	boolean isIncluded(String key);
}
