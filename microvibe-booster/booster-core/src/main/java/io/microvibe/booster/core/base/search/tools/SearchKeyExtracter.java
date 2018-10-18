package io.microvibe.booster.core.base.search.tools;

import io.microvibe.booster.core.api.tools.DataKeyExtracter;

import java.util.Map;

/**
 * 接口传递的查询条件的属性提取器
 *
 * @author Qt
 * @version 1.0.1
 * @since Mar 28, 2018
 */
public interface SearchKeyExtracter<T extends SearchKeyExtracter<T>> extends DataKeyExtracter<T> {

	public static SearchKeyExtracter<?> config() {
		return new AbstractKeyExtracter() {
		};
	}

}
