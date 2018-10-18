package io.microvibe.booster.core.base.mybatis.lang;

public class ScriptWrapper {
	static final String PREFIX = "<script>";
	static final String SUFFIX = "</script>";

	public static String quoteScript(String script) {
		boolean hasPrefix = script.startsWith(PREFIX);
		boolean hasSuffix = script.endsWith(SUFFIX);
		if (hasPrefix && hasSuffix) {
			return script;
		}
		StringBuilder sb = new StringBuilder();
		if (!hasPrefix) {
			sb.append(PREFIX);
		}
		sb.append(script);
		if (!hasSuffix) {
			sb.append(SUFFIX);
		}
		return sb.toString();
	}
}
