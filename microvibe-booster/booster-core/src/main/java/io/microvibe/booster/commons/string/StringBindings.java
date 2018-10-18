package io.microvibe.booster.commons.string;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Qt
 * @since Jul 13, 2018
 */
public class StringBindings {
	private static final Pattern P1 = Pattern.compile("\\$\\{([^${}]+)\\}");
	private static final Pattern P2 = Pattern.compile("\\$([^${]+)\\$");

	/**
	 * 将字符串中的变量替换为System.getProperties()中的值<br>
	 * 变量的书写格式：${key}或$key$
	 *
	 * @param orig 原字符串
	 * @return 替换后的字符串
	 */
	public static String bindVariable(String orig) {
		orig = bindVariable(orig, System.getProperties());
		return orig;
	}

	/**
	 * 将字符串中的变量替换为Properties中的值<br>
	 * 变量的书写格式：${key}或$key$
	 *
	 * @param orig
	 * @param param
	 * @return
	 */
	public static String bindVariable(String orig, final Properties param) {
		orig = bindVariable(orig, param, P1);
		orig = bindVariable(orig, param, P2);
		return orig;
	}

	private static String bindVariable(final String orig, final Properties param,
		final Pattern pattern) {
		StringBuffer sb = new StringBuffer();
		Matcher m = pattern.matcher(orig);
		while(m.find()){
			String attr = m.group(1);
			m.appendReplacement(sb, "");
			String val = param.getProperty(attr);
			if (val != null) {
				sb.append(val);
			}
		}
		m.appendTail(sb);
		/*int start = 0;
		int end = 0;
		int idx = 0;
		while (m.find(idx)) {
			String attr = m.group(1);
			String val = param.getProperty(attr);
			if (val == null) {
				val = "";
			}
			end = m.start();
			sb.append(orig.substring(start, end)).append(val);

			start = m.end();
			idx = m.end();
		}
		sb.append(orig.substring(idx));*/
		String dest = sb.toString();
		return dest;
	}

	public static void main(String[] args) {
		System.out.println(bindVariable("java.io.tmpdir: ${java.io.tmpdir}\n" +
			"user.dir: ${user.dir}\n" +
			"user.name: ${user.name}\n" +
			"user.home: ${user.home}\n" +
			""
		));
	}
}
