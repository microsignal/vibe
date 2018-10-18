package io.microvibe.booster.core.api.tools.impl;

import com.google.common.base.CaseFormat;
import io.microvibe.booster.core.api.tools.DataKeyExtracter;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Qt
 * @since Jun 30, 2018
 */
public class DataKeyExtracterSupport
	implements DataKeyExtracter<DataKeyExtracterSupport> {

	private boolean defaultIncluded = true;
	private Set<String> includeKeys = new HashSet<>();
	private Set<String> excludeKeys = new HashSet<>();
	private Map<String, String> keysMapping = new HashMap<>();

	private void addInclude(String k) {
		includeKeys.add(k);
		excludeKeys.remove(k);
	}

	private void addExclude(String k) {
		excludeKeys.add(k);
		includeKeys.remove(k);
	}

	@Override
	public boolean isIncluded(String key) {
		return includeKeys.isEmpty() && defaultIncluded || includeKeys.contains(key);
	}

	@Override
	public boolean isExcluded(String key) {
		return excludeKeys.contains(key);
	}

	@Override
	public String getMappingKey(String key) {
		if (isExcluded(key) || !isIncluded(key)) {
			return null;
		}
		return keysMapping.getOrDefault(key, key);
	}

	@Override
	public DataKeyExtracterSupport defaultIncluded(boolean defaultIncluded) {
		this.defaultIncluded = defaultIncluded;
		return this;
	}

	@Override
	public DataKeyExtracterSupport include(String... keys) {
		for (String key : keys) {
			String[] arr = key.split("[,;\\s]+");
			for (String k : arr) {
				addInclude(k);
			}
		}
		return this;
	}

	@Override
	public DataKeyExtracterSupport includeAll(Class<?> entityClass) {
		PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(entityClass);
		for (int i = 0; i < pds.length; i++) {
			String name = pds[i].getName();
			addInclude(name);
		}
		return this;
	}

	@Override
	public DataKeyExtracterSupport includeAllUnderlineFormat(Class<?> entityClass) {
		PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(entityClass);
		for (int i = 0; i < pds.length; i++) {
			String name = pds[i].getName();
			String underline = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
			addInclude(underline);
		}
		return this;
	}

	@Override
	public DataKeyExtracterSupport exclude(String... keys) {
		for (String key : keys) {
			String[] arr = key.split("[,;\\s]+");
			for (String k : arr) {
				addExclude(k);
			}
		}
		return this;
	}

	@Override
	public DataKeyExtracterSupport excludeAll(Class<?> entityClass) {
		PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(entityClass);
		for (int i = 0; i < pds.length; i++) {
			String name = pds[i].getName();
			addExclude(name);
		}
		return this;
	}

	@Override
	public DataKeyExtracterSupport excludeAllUnderlineFormat(Class<?> entityClass) {
		PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(entityClass);
		for (int i = 0; i < pds.length; i++) {
			String name = pds[i].getName();
			String underline = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
			addExclude(underline);
		}
		return this;
	}

	@Override
	public DataKeyExtracterSupport mapping(String origKey, String destKey) {
		keysMapping.put(origKey, destKey);
		return this;
	}

	@Override
	public DataKeyExtracterSupport mapping(Map<String, String> mapping) {
		keysMapping.putAll(mapping);
		return this;
	}

	@Override
	public DataKeyExtracterSupport mappingAllCamelToUnderline(Class<?> entityClass) {
		PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(entityClass);
		for (int i = 0; i < pds.length; i++) {
			String name = pds[i].getName();
			String underline = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
			keysMapping.put(name, underline);
		}
		return this;
	}

	@Override
	public DataKeyExtracterSupport mappingAllUnderlineToCamel(Class<?> entityClass) {
		PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(entityClass);
		for (int i = 0; i < pds.length; i++) {
			String name = pds[i].getName();
			String underline = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
			keysMapping.put(underline, name);
		}
		return this;
	}


}
