package io.microvibe.util.constant;

public class GlobalConstant {

	public static final String SYMBOL_SLASH = "/";
	public static final String SYMBOL_DOT = ".";

	public static final String CLASS_PATH;
	public static final String PATH_SEPARATOR;
	public static final String LINE_SEPARATOR;

	static {
		CLASS_PATH = System.getProperty("java.class.path");
		PATH_SEPARATOR = System.getProperty("path.separator");
		LINE_SEPARATOR = System.getProperty("line.separator");
	}

}
