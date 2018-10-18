package io.microvibe.booster.commons.utils;

import org.apache.commons.lang3.StringEscapeUtils;

import javax.persistence.Column;
import javax.servlet.http.HttpServletRequest;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 字符串工具类, 继承org.apache.commons.lang3.StringUtils类
 *
 * @author Qt
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

	public static Date accessTime = new Date();
	public static String accessToken = "";
	public static String jsapiTicket = "";

	private static final ThreadLocal<Map<Object, Object>> dataCacheThreadLocal = new ThreadLocal<Map<Object, Object>>();
	private static final ThreadLocal<Integer> counterThreadLocal = new ThreadLocal<Integer>();

	@SuppressWarnings("unchecked")
	public static <T> T trimToNull(final T o) {
		if (o == null) {
			return null;
		}
		if (o.getClass() == String.class) {
			return (T) trimToNull((String) o);
		}

		Map<Object, Object> map = dataCacheThreadLocal.get();
		if (map == null) {
			map = new ConcurrentHashMap<Object, Object>();
			dataCacheThreadLocal.set(map);
		} else if (map.containsKey(o)) {
			return o;
		}
		Integer counter = counterThreadLocal.get();
		if (counter == null) {
			counterThreadLocal.set(Integer.valueOf(0));
		} else {
			counterThreadLocal.set(counter + 1);
		}
		map.put(o, o);
		try {
			if (o instanceof Collection) {
				Collection<Object> collections = (Collection<Object>) o;
				Object[] array = collections.toArray();
				collections.clear();
				for (int i = 0; i < array.length; i++) {
					collections.add(trimToNull(array[i]));
				}
			} else if (o.getClass().isArray()) {
				int length = Array.getLength(o);
				for (int i = 0; i < length; i++) {
					Object e = Array.get(o, i);
					Array.set(o, i, trimToNull(e));
				}
			} else {
				BeanInfo info = Introspector.getBeanInfo(o.getClass(), Object.class);
				PropertyDescriptor[] pds = info.getPropertyDescriptors();
				for (PropertyDescriptor pd : pds) {
					Method readMethod = pd.getReadMethod();
					Method writeMethod = pd.getWriteMethod();
					if (writeMethod != null && readMethod != null) {
						Object v = readMethod.invoke(o);
						if (v != null) {
							if (pd.getPropertyType() == String.class) {
								writeMethod.invoke(o, trimToNull((String) v));
							} else {
								writeMethod.invoke(o, trimToNull(v));
							}
						}
					}
				}
			}
		} catch (Exception e) {
		} finally {
			if (counter == null) {
				dataCacheThreadLocal.remove();
				counterThreadLocal.remove();
			}
		}
		return o;
	}


	public static String lowerFirst(String str) {
		if (StringUtils.isBlank(str)) {
			return "";
		} else {
			return str.substring(0, 1).toLowerCase() + str.substring(1);
		}
	}

	public static String upperFirst(String str) {
		if (StringUtils.isBlank(str)) {
			return "";
		} else {
			return str.substring(0, 1).toUpperCase() + str.substring(1);
		}
	}

	/**
	 * 替换掉HTML标签方法
	 */
	public static String replaceHtml(String html) {
		if (isBlank(html)) {
			return "";
		}
		String regEx = "<.+?>";
		String regExSpace = "\\s*|\t|\r|\n";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(html);
		String s = m.replaceAll("");

		Pattern pSpace = Pattern.compile(regExSpace);
		Matcher mSpace = pSpace.matcher(s);
		String result = mSpace.replaceAll("");
		return result;
	}

	/**
	 * 替换掉HTML(&lt;span)标签方法
	 */
	public static String replaceHtmls(String html) {
		if (isBlank(html)) {
			return "";
		}
		String regEx = "&lt;.+?&gt;";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(html);
		String s = m.replaceAll("");
		return s;
	}

	/**
	 * 缩略字符串（不区分中英文字符）
	 *
	 * @param str    目标字符串
	 * @param length 截取长度
	 * @return
	 */
	public static String abbr(String str, int length) {
		if (str == null) {
			return "";
		}
		try {
			StringBuilder sb = new StringBuilder();
			int currentLength = 0;
			for (char c : replaceHtml(StringEscapeUtils.unescapeHtml4(str))
				.toCharArray()) {
				currentLength += String.valueOf(c).getBytes("GBK").length;
				if (currentLength <= length - 3) {
					sb.append(c);
				} else {
					sb.append("...");
					break;
				}
			}
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 缩略字符串（替换html）
	 *
	 * @param str    目标字符串
	 * @param length 截取长度
	 * @return
	 */
	public static String rabbr(String str, int length) {
		return abbr(replaceHtml(str), length);
	}

	/**
	 * 转换为Double类型
	 */
	public static Double toDouble(Object val) {
		if (val == null) {
			return 0D;
		}
		try {
			return Double.valueOf(trim(val.toString()));
		} catch (Exception e) {
			return 0D;
		}
	}

	/**
	 * 转换为Float类型
	 */
	public static Float toFloat(Object val) {
		return toDouble(val).floatValue();
	}

	/**
	 * 转换为Long类型
	 */
	public static Long toLong(Object val) {
		return toDouble(val).longValue();
	}

	/**
	 * 转换为Integer类型
	 */
	public static Integer toInteger(Object val) {
		return toLong(val).intValue();
	}

	/**
	 * 获得用户远程地址
	 */
	public static String getRemoteAddr(HttpServletRequest request) {
		String remoteAddr = request.getHeader("X-Real-IP");
		if (isNotBlank(remoteAddr)) {
			remoteAddr = request.getHeader("X-Forwarded-For");
		} else if (isNotBlank(remoteAddr)) {
			remoteAddr = request.getHeader("Proxy-Client-IP");
		} else if (isNotBlank(remoteAddr)) {
			remoteAddr = request.getHeader("WL-Proxy-Client-IP");
		}
		return remoteAddr != null ? remoteAddr : request.getRemoteAddr();
	}

	/**
	 * float截取小数点后两位
	 *
	 * @param ft
	 * @return
	 */
	public static float floatCut(float ft) {
		int scale = 2;// 设置位数
		int roundingMode = 4;// 表示四舍五入，可以选择其他舍值方式，例如去尾，等等.
		BigDecimal bd = new BigDecimal((double) ft);
		bd = bd.setScale(scale, roundingMode);

		ft = bd.floatValue();
		return ft;
	}

	/**
	 * 生成随机字符串
	 *
	 * @param length
	 * @return
	 */
	public static String getRandomString(int length) {
		String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	/**
	 * 判断字符串是否仅为数字
	 *
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {

		Pattern pattern = Pattern.compile("[0-9]*");

		return pattern.matcher(str).matches();

	}

	/**
	 * 判断是否为汉字
	 *
	 * @param str
	 * @return
	 */
	public static boolean vd(String str) {

		char[] chars = str.toCharArray();
		boolean isGB2312 = false;
		for (int i = 0; i < chars.length; i++) {
			byte[] bytes = ("" + chars[i]).getBytes();
			if (bytes.length == 2) {
				int[] ints = new int[2];
				ints[0] = bytes[0] & 0xff;
				ints[1] = bytes[1] & 0xff;
				if (ints[0] >= 0x81 && ints[0] <= 0xFE && ints[1] >= 0x40
					&& ints[1] <= 0xFE) {
					isGB2312 = true;
					break;
				}
			}
		}
		return isGB2312;
	}

	/**
	 * 是否是GB2312中的字符
	 *
	 * @param s
	 * @return
	 */
	public final static boolean isGB2312(final String s) {
		return java.nio.charset.Charset.forName("GB2312").newEncoder().canEncode(s);
	}

	/**
	 * 将字符串编码格式转成UTF-8
	 *
	 * @param str
	 * @return
	 */
	public static String TranEncodeTOUTF8(String str) {
		try {
			String strEncode = getEncoding(str);
			String temp = new String(str.getBytes(strEncode), "UTF-8");
			return temp;
		} catch (java.io.IOException ex) {

			return null;
		}
	}

	public static String TranEncodeTOGB2312(String str) {
		try {
			String strEncode = getEncoding(str);
			String temp = new String(str.getBytes(strEncode), "GB2312");
			return temp;
		} catch (java.io.IOException ex) {

			return null;
		}
	}

	/**
	 * @param str
	 * @return
	 */
	public static String RemoveNotGB2312(String str) {
		if (isGB2312(str)) {
			return str;
		} else {
			String newStr = "";
			for (String s : str.split("")) {
				if (isGB2312(s)) {
					newStr += s;
				}
			}
			return newStr;
		}
	}

	/**
	 * 判断输入字符是否为UTF-8的编码格式
	 *
	 * @param c 输入字符
	 * @return 如果是UTF-8返回真，否则返回假
	 */
	public static boolean isUTF8(char c) {

		return java.nio.charset.Charset.forName("UTF-8").newEncoder()
			.canEncode(c);
	}

	public static boolean isGB2312(char c) {

		return java.nio.charset.Charset.forName("GB2312").newEncoder()
			.canEncode(c);
	}

	/**
	 * 判断字符串的编码
	 *
	 * @param str
	 * @return
	 */
	public static String getEncoding(String str) {
		String encodeValue = "";
		String[] encode = {"GB2312", "ISO-8859-1", "UTF-8", "GBK", "ASCII",
			"BIG5", "UCS"};
		for (int i = 0; i < encode.length; i++) {
			encodeValue = encode[i];
			boolean canPaser = java.nio.charset.Charset.forName(encodeValue)
				.newEncoder().canEncode(str);
			if (canPaser) {
				break;
			}
		}
		return encodeValue;
	}


	/**
	 * e list to String
	 * list.add("aa");
	 * list.add("bb");
	 * list.add("cc");
	 * 'aa','bb','cc'
	 *
	 * @param stringList
	 * @return
	 */
	public static String listToString(List<String> stringList) {
		if (stringList == null) {
			return null;
		}
		StringBuilder result = new StringBuilder("'");
		boolean flag = false;
		for (String string : stringList) {
			if (flag) {
				result.append("','");
			} else {
				flag = true;
			}
			result.append(string);
		}
		return result.append("'").toString();
	}

	public static final char UNDERLINE = '_';

	public static String camelToUnderline(String param) {
		if (param == null || "".equals(param.trim())) {
			return "";
		}
		int len = param.length();
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			char c = param.charAt(i);
			if (Character.isUpperCase(c)) {
				sb.append(UNDERLINE);
				sb.append(Character.toLowerCase(c));
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static String underlineToCamel(String param) {
		return underlineToCamel(param, false);
	}

	public static String underlineToCamel(String param, boolean firstUpper) {
		if (param == null || "".equals(param.trim())) {
			return "";
		}
		int len = param.length();
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			char c = param.charAt(i);

			if (c == UNDERLINE) {
				if (++i < len) {
					sb.append(Character.toUpperCase(param.charAt(i)));
				}
			} else {
				if (firstUpper && i == 0) {
					sb.append(Character.toUpperCase(param.charAt(i)));
					//sb.replace(0,1,sb.substring(0,1).toUpperCase());
				} else {
					sb.append(c);
				}
			}
		}

		return sb.toString();
	}

	/**
	 * 处理地区code进行搜索
	 *
	 * @param regionCode
	 * @return
	 */
	public static String subRegion(String regionCode) {
		if (regionCode.substring(regionCode.length() - 8, regionCode.length()).equals("00000000")) {
			return regionCode.substring(0, regionCode.length() - 8);
		}
		if (regionCode.substring(regionCode.length() - 6, regionCode.length()).equals("000000")) {
			return regionCode.substring(0, regionCode.length() - 6);
		}
		if (regionCode.substring(regionCode.length() - 3, regionCode.length()).equals("000")) {
			return regionCode.substring(0, regionCode.length() - 9);
		}
		return regionCode;
	}

	/**
	 * 下划线命名转为驼峰命名
	 *
	 * @param para
	 * @return
	 */
	public static String UnderlineToHump(String para) {
		if (StringUtils.isBlank(para)) {
			return null;
		}
		StringBuilder result = new StringBuilder();
		String a[] = para.split("_");
		for (String s : a) {
			if (result.length() == 0) {
				result.append(s.toLowerCase());
			} else {
				result.append(s.substring(0, 1).toUpperCase());
				result.append(s.substring(1).toLowerCase());
			}
		}
		return result.toString();
	}

}
