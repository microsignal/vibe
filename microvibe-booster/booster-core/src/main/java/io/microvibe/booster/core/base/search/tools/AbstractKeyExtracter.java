package io.microvibe.booster.core.base.search.tools;

import com.google.common.base.CaseFormat;
import io.microvibe.booster.core.api.tools.impl.DataKeyExtracterSupport;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 接口传递的查询条件的属性提取器
 *
 * @author Qt
 * @version 1.0.1
 * @since Mar 28, 2018
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractKeyExtracter
	implements SearchKeyExtracter<AbstractKeyExtracter>, SortKeyExtracter<AbstractKeyExtracter> {

	private DataKeyExtracterSupport delegate = new DataKeyExtracterSupport();

	@Override
	public String getMappingKey(String key) {
		return delegate.getMappingKey(key);
	}

	@Override
	public boolean isExcluded(String key) {
		return delegate.isExcluded(key);
	}

	@Override
	public boolean isIncluded(String key) {
		return delegate.isIncluded(key);
	}

	@Override
	public AbstractKeyExtracter defaultIncluded(boolean defaultIncluded) {
		delegate.defaultIncluded(defaultIncluded);
		return this;
	}

	@Override
	public AbstractKeyExtracter include(String... keys) {
		delegate.include(keys);
		return this;
	}

	@Override
	public AbstractKeyExtracter includeAll(Class<?> entityClass) {
		delegate.includeAll(entityClass);
		return this;
	}

	@Override
	public AbstractKeyExtracter includeAllUnderlineFormat(Class<?> entityClass) {
		delegate.includeAllUnderlineFormat(entityClass);
		return this;
	}

	@Override
	public AbstractKeyExtracter exclude(String... keys) {
		delegate.exclude(keys);
		return this;
	}

	@Override
	public AbstractKeyExtracter excludeAll(Class<?> entityClass) {
		delegate.excludeAll(entityClass);
		return this;
	}

	@Override
	public AbstractKeyExtracter excludeAllUnderlineFormat(Class<?> entityClass) {
		delegate.excludeAllUnderlineFormat(entityClass);
		return this;
	}

	@Override
	public AbstractKeyExtracter mapping(String origKey, String destKey) {
		delegate.mapping(origKey, destKey);
		return this;
	}

	@Override
	public AbstractKeyExtracter mapping(Map<String, String> mapping) {
		delegate.mapping(mapping);
		return this;
	}

	@Override
	public AbstractKeyExtracter mappingAllCamelToUnderline(Class<?> entityClass) {
		delegate.mappingAllCamelToUnderline(entityClass);
		return this;
	}

	@Override
	public AbstractKeyExtracter mappingAllUnderlineToCamel(Class<?> entityClass) {
		delegate.mappingAllUnderlineToCamel(entityClass);
		return this;
	}

}
