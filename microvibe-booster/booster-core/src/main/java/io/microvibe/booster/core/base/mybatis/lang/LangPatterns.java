package io.microvibe.booster.core.base.mybatis.lang;

import io.microvibe.booster.core.base.mybatis.MybatisConstants;
import io.microvibe.booster.core.lang.velocity.VelocityTemplate;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LangPatterns {
	private static final Pattern X_LANG_PATTERN = Pattern.compile(MybatisConstants.LANG_PATTERN);


	public static String quote(String script) {
		boolean hasPrefix = script.startsWith(MybatisConstants.LANG_PREFIX);
		boolean hasSuffix = script.endsWith(MybatisConstants.LANG_SUFFIX);
		if (hasPrefix && hasSuffix) {
			return script;
		}
		StringBuilder sb = new StringBuilder();
		if (!hasPrefix) {
			sb.append(MybatisConstants.LANG_PREFIX);
		}
		sb.append(script);
		if (!hasSuffix) {
			sb.append(MybatisConstants.LANG_SUFFIX);
		}
		return sb.toString();
	}

	public static String parse(String script, Function<String, String> resolver) {
		Matcher matcher = X_LANG_PATTERN.matcher(script);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			String expression = matcher.group(1);
			matcher.appendReplacement(sb, resolver.apply(expression).replace("$", "\\$"));
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	public static String parse(String script) {
		return parse(script, expression -> VelocityTemplate.eval(expression));
	}

}
