package io.microvibe.booster.core.base.utils;

import com.google.common.base.CaseFormat;

/**
 * @author Qt
 * @see CaseFormat
 */
public class NameCastor {

	/**
	 * 将驼峰标识转换为下划线
	 *
	 * @param text
	 * @return camel
	 */
	public static String camelToUnderline(String text) {
		return camelToUnderline(text);
	}

	public static String camelToLowerUnderline(String text) {
		if (text == null) {
			return null;
		}
		return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, text);
	}

	public static String camelToUpperUnderline(String text) {
		if (text == null) {
			return null;
		}
		return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, text);
	}

	/**
	 * 将下划线标识转换为驼峰
	 *
	 * @param text
	 * @return underline
	 */
	public static String underlineToCamel(String text) {
		return underlineToLowerCamel(text);
	}

	public static String underlineToLowerCamel(String text) {
		if (text == null) {
			return null;
		}
		return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, text);
	}

	public static String underlineToUpperCamel(String text) {
		if (text == null) {
			return null;
		}
		return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, text);
	}

	public static String lowerCamelToUpperCamel(String text) {
		if (text == null) {
			return null;
		}
		return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, text);
	}

	public static String upperCamelToLowerCamel(String text) {
		if (text == null) {
			return null;
		}
		return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, text);
	}
}
