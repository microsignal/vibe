package io.microvibe.booster.commons.string;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Qt
 * @since Jul 13, 2018
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Names {

	private static final String FOLDER_SEPARATOR = "/";
	private static final String WINDOWS_FOLDER_SEPARATOR = "\\";
	private static final char EXTENSION_SEPARATOR = '.';

	public static String formatInstanceName(final Class<?> clazz) {
		StringBuilder sb = new StringBuilder();
		String name = clazz.getSimpleName();
		char ch0 = name.charAt(0);
		if (name.length() == 1) {
			sb.append(Character.toLowerCase(ch0));
		} else {
			char ch1 = name.charAt(1);
			if (Character.isUpperCase(ch1)) {
				sb.append(name);
			} else {
				sb.append(Character.toLowerCase(ch0));
				sb.append(name.substring(1));
			}
		}
		return sb.toString();
	}

	public static String formatNameAsXmlStyle(final String name) {
		char[] chs = name.toCharArray();
		StringBuilder sb = new StringBuilder();
		int idx = 0;
		while (idx < chs.length) {
			if (chs[idx] != '_' && chs[idx] != '-') {
				sb.append(Character.toLowerCase(chs[idx]));
				idx++;
				break;
			} else {
				idx++;
			}
		}
		while (idx < chs.length) {
			if (Character.isUpperCase(chs[idx])) {
				sb.append('-').append(Character.toLowerCase(chs[idx]));
			} else {
				sb.append(chs[idx]);
			}
			idx++;
		}
		return sb.toString();
	}

	public static String formatNameAsJavaStyle(final String name) {
		char[] chs = name.toLowerCase().toCharArray();
		StringBuilder sb = new StringBuilder();
		int idx = 0;
		while (idx < chs.length) {
			if (chs[idx] != '_' && chs[idx] != '-') {
				sb.append(chs[idx]);
				idx++;
				break;
			} else {
				idx++;
			}
		}
		boolean upper = false;
		while (idx < chs.length) {
			if (chs[idx] != '_' && chs[idx] != '-') {
				if (upper) {
					sb.append(Character.toUpperCase(chs[idx]));
					upper = false;
				} else {
					sb.append(chs[idx]);
				}
			} else {
				upper = true;
			}
			idx++;
		}
		return sb.toString();
	}


	public static String getFilename(String path) {
		if (path == null) {
			return null;
		}
		path = path.replace(WINDOWS_FOLDER_SEPARATOR, FOLDER_SEPARATOR);
		int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR);
		return (separatorIndex != -1 ? path.substring(separatorIndex + 1) : path);
	}

	public static String getFilenameExtension(final String path) {
		if (path == null) {
			return null;
		}
		int extIndex = path.lastIndexOf(EXTENSION_SEPARATOR);
		if (extIndex == -1) {
			return null;
		}
		int folderIndex = path.lastIndexOf(FOLDER_SEPARATOR);
		if (folderIndex > extIndex) {
			return null;
		}
		return path.substring(extIndex + 1);
	}
}
