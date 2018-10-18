package io.microvibe.booster.commons.string;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author Qt
 * @since Jul 13, 2018
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Strings extends org.apache.commons.lang3.StringUtils {


	/**
	 * 空字符串
	 */
	public static final String EMPTY = "";

	/**
	 * null字符串
	 */
	public static final String NULL = "null";
	/**
	 * 逗号
	 */
	public static final String COMMA = ",";
	/**
	 * 分号分
	 */
	public static final String SEMICOLON = ";";

	/**
	 * 冒号
	 */
	public static final String COLON = ":";

	/**
	 * 小于号
	 */
	public static final String LT = "<";

	/**
	 * 大于号
	 */
	public static final String GT = ">";

	/**
	 * 点号
	 */
	public static final String DOT = ".";

	/**
	 * 下划线
	 */
	public static final String UNDERLINE = "_";

	/**
	 * 分号分隔符(不区分全半角)
	 */
	public static final String SPLIT_SEMICOLON = ";|；";

	/**
	 * 逗号分隔符(不区分全半角)
	 */
	public static final String SPLIT_COMMA = ",|，";

	/**
	 * 索引位置找不到
	 */
	public static final int INDEX_NOT_FOUND = -1;

	/**
	 * 整数特殊值
	 */
	public static final int NUM_FLAG = -1;

	/**
	 * 长整数特殊值
	 */
	public static final long LONG_NUM_FLAG = -1;

	/**
	 * 换行
	 */
	public static final String NEW_LINE = "\n";

	public static String uuid() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString().replace("-", "");
	}


	public static String lpad(final String str, final int length) {
		return lpad(str, length, ' ');
	}

	public static String lpad(final String str, final int length, final char pad) {
		if (str.length() >= length) {
			return str;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length - str.length(); i++) {
			sb.append(pad);
		}
		sb.append(str);
		return sb.toString();
	}

	public static String rpad(final String str, final int length) {
		return rpad(str, length, ' ');
	}

	public static String rpad(final String str, final int length, final char pad) {
		if (str.length() >= length) {
			return str;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(str);
		for (int i = 0; i < length - str.length(); i++) {
			sb.append(pad);
		}
		return sb.toString();
	}

	/**
	 * 返回参数中第一个不为null或空字符串的对象，如不存在，返回null
	 *
	 * @param args
	 * @return
	 */
	public static String coalesce(final String... args) {
		String v = null;
		if (args.length > 0) {
			for (String arg : args) {
				if (arg != null && !arg.trim().equals("")) {
					v = arg;
					break;
				}
			}
		}
		return v;
	}

	public static String trimLeft(final String str) {
		if (!StringUtils.hasLength(str)) {
			return str;
		}
		StringBuilder sb = new StringBuilder(str);
		while (sb.length() > 0 && Character.isWhitespace(sb.charAt(0))) {
			sb.deleteCharAt(0);
		}
		return sb.toString();
	}

	public static String trimLeftCharacter(final String str, final char leadingCharacter) {
		if (!StringUtils.hasLength(str)) {
			return str;
		}
		StringBuilder sb = new StringBuilder(str);
		while (sb.length() > 0 && sb.charAt(0) == leadingCharacter) {
			sb.deleteCharAt(0);
		}
		return sb.toString();
	}

	public static String trimRight(final String str) {
		if (!StringUtils.hasLength(str)) {
			return str;
		}
		StringBuilder sb = new StringBuilder(str);
		while (sb.length() > 0 && Character.isWhitespace(sb.charAt(sb.length() - 1))) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	public static String trimRightCharacter(final String str, final char leadingCharacter) {
		if (!StringUtils.hasLength(str)) {
			return str;
		}
		StringBuilder sb = new StringBuilder(str);
		while (sb.length() > 0 && sb.charAt(0) == leadingCharacter) {
			sb.deleteCharAt(0);
		}
		return sb.toString();
	}

	public static boolean containsWhitespace(final CharSequence str) {
		if (!StringUtils.hasLength(str)) {
			return false;
		}
		int strLen = str.length();
		for (int i = 0; i < strLen; i++) {
			if (Character.isWhitespace(str.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 将字符串按分隔符分隔，转成字符串trim后的集合
	 *
	 * @param str
	 * @param split
	 * @return
	 */
	public static List<String> toTrimList(String str, String split) {
		return toTrimList(str, split, false);
	}

	/**
	 * 将字符串按分隔符分隔，转成字符串trim后的集合，支持去除空白字符
	 *
	 * @param str
	 * @param split
	 * @param excludeBlank 是否除排空白字符
	 * @return
	 */
	public static List<String> toTrimList(String str, String split, boolean excludeBlank) {
		if (isEmpty(str)) {
			return new ArrayList<String>();
		}
		List<String> strList = new LinkedList<String>();
		String[] strArr = str.split(split);
		for (int i = 0; i < strArr.length; i++) {
			String tempStr = strArr[i];
			if (excludeBlank) {
				if (isEmpty(tempStr)) {
					continue;
				}
			}
			strList.add(strArr[i].trim());
		}
		return strList;
	}


	/**
	 * 将字符串按逗号分隔，转成字符串集合<br>
	 *
	 * @param str
	 * @return 当字符串为空时，返回空集合
	 */
	public static List<String> toStrList(String str) {
		return toStrList(str, COMMA);
	}

	/**
	 * 将字符串按分隔符分隔，转成字符串集合
	 *
	 * @param str
	 * @param split
	 * @return 当字符串为空时，返回空集合
	 */
	public static List<String> toStrList(String str, String split) {
		if (isEmpty(str)) {
			return new ArrayList<String>();
		}
		return new ArrayList<String>(Arrays.asList(str.split(split)));
	}


	/**
	 * 将字符串按逗号分隔，转成Long集合
	 *
	 * @param str
	 * @return 当字符串为空时，返回空集合
	 */
	public static List<Long> toLongList(String str) {
		return toLongList(str, COMMA);
	}

	/**
	 * 将字符串按逗号分隔，转成整数集合
	 *
	 * @param str
	 * @return 当字符串为空时，返回空集合
	 */
	public static List<Integer> toIntList(String str) {
		return toIntList(str, COMMA);
	}

	/**
	 * 将字符串按逗号分隔，转成整数集合（注意：不能转换为整数的会被过滤掉）
	 *
	 * @param str
	 * @param split
	 * @return
	 */
	public static List<Long> toLongList(String str, String split) {
		if (isEmpty(str)) {
			return new ArrayList<Long>();
		}
		List<Long> longList = new LinkedList<Long>();
		String[] strArr = str.split(split);
		for (int i = 0; i < strArr.length; i++) {
			Long num = toLong(strArr[i]);
			if (num != null) {
				longList.add(num);
			}
		}
		return longList;
	}

	/**
	 * 将字符串按逗号分隔，转成整数集合（注意：不能转换为整数的会被过滤掉）
	 *
	 * @param str
	 * @param split
	 * @return
	 */
	public static List<Integer> toIntList(String str, String split) {
		if (isEmpty(str)) {
			return new ArrayList<Integer>();
		}
		List<Integer> intList = new LinkedList<Integer>();
		String[] strArr = str.split(split);
		for (int i = 0; i < strArr.length; i++) {
			Integer num = toInteger(strArr[i]);
			if (num != null) {
				intList.add(num);
			}
		}
		return intList;
	}

	/**
	 * 将字符串转换为整形形式，无法转换则返回null
	 *
	 * @param str
	 * @return
	 */
	public static Integer toInteger(String str) {
		if (isEmpty(str)) {
			return null;
		}
		try {
			return Integer.decode(str);
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * 将字符串转换为Long形式，无法转换则返回null
	 *
	 * @param str
	 * @return
	 */
	public static Long toLong(String str) {
		if (isEmpty(str)) {
			return null;
		}
		try {
			return Long.decode(str);
		} catch (Exception ex) {
			return null;
		}
	}

	public static String formatMaxLength(final String text, final int maxLength) {
		if (text.length() <= maxLength / 2) {
			return text;
		}
		char[] chs = text.toCharArray();
		StringBuilder sb = new StringBuilder();
		int lengthb = 0;
		int length = 0;
		for (char ch : chs) {
			lengthb++;
			if (ch > 255 || ch < 1) {
				lengthb++;
			}
			if (lengthb > maxLength) {
				break;
			}
			sb.append(ch);
			length++;
		}
		if (length < chs.length) {
			sb.append("...");
		}
		return sb.toString();
	}

	public static String formatMinLength(final String text, final int minLength) {
		if (text.length() >= minLength) {
			return text;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(text);
		for (int i = 0; i < minLength - text.length(); i++) {
			sb.append(" ");
		}
		return sb.toString();
	}

}
