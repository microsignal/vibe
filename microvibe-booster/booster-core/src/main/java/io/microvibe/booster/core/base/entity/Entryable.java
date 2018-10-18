package io.microvibe.booster.core.base.entity;

import com.google.common.base.CaseFormat;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于标识通用的key-value形式的数据字典.供前端接口使用
 *
 * @author Qt
 * @version 1.0.1
 * @since Feb 11, 2018
 */
public interface Entryable {

	EntryableHelper HELPER = new EntryableHelper();

	static String entryName(Class<?> clazz) {
		if (Entryable.class.isAssignableFrom(clazz)) {
			EntryType anno = clazz.getAnnotation(EntryType.class);
			String type;
			String typeName = clazz.getSimpleName();
			if (anno != null) {
				type = anno.value();
			} else {
				type = typeName;
			}
			return type;
		} else {
			return null;
		}
	}

	static Map<String, String> entries(String type) {
		return HELPER.entries(type);
	}

	default void register() {
		Class<? extends Entryable> clazz = this.getClass();
		String type = entryName(clazz);
		HELPER.entries(type,key(),value());
		if (!clazz.isAnnotationPresent(EntryType.class)) {
			String typeName = clazz.getSimpleName();
			HELPER.alias(type, typeName);
			HELPER.alias(type,CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, typeName));
			HELPER.alias(type,CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, typeName));
			HELPER.alias(type,CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, typeName));
		}
	}

	String key();

	String value();

}
