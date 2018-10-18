package io.microvibe.util;

/**
 * @since 1.0 , Java 1.8 , Sep 13, 2016
 * @version 1.0
 * @author Qt
 */
public class StringBuilders {
	private static final ThreadLocal<StringBuilder> builderLocal = new ThreadLocal<StringBuilder>();

	public static void begin() {
		builderLocal.set(new StringBuilder());
	}

	public static void append(String s) {
		builderLocal.get().append(s);
	}

	public static void prepend(String s) {
		builderLocal.get().insert(0, s);
	}

	public static String asString() {
		return builderLocal.get().toString();
	}

	public static String end() {
		String string = builderLocal.get().toString();
		builderLocal.remove();
		return string;
	}
}
