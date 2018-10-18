package io.microvibe.util.io;

import java.io.File;

import io.microvibe.util.StringUtil;

/**
 * 文件工具类
 * @since 2017/09/25
 * @author AntEngine
 */
public class FileUtil {

	/**
	 * 文件后缀分隔符
	 */
	public static final String DOT = ".";

	/**
	 * 换行符
	 */
	public static final String NEW_LINE = System.getProperty("line.separator");

	/**
	 * 获取文件的扩展名
	 *
	 * @param fileName 文件名
	 * @return 当文件名中不存在.号时，则返回""
	 */
	public static String getFileExtName(String fileName) {
		if (StringUtil.isEmpty(fileName) || fileName.lastIndexOf(DOT) == StringUtil.INDEX_NOT_FOUND) {
			return StringUtil.EMPTY;
		}
		return fileName.substring(fileName.lastIndexOf(DOT) + 1);
	}

	/**
	 * 获取文件的扩展名
	 *
	 * @param file
	 * @return 当文件名中不存在.号时，则返回""
	 */
	public static String getFileExtName(File file) {
		if (file != null) {
			return getFileExtName(file.getName());
		}
		return null;
	}

	/**
	 * 去除文件扩展名，例如：abc.txt -> abc
	 *
	 * @param fileName
	 * @return 如果fileName为空或不存在后缀名则返回自身
	 */
	public static String removeFileExtName(String fileName) {
		String suffix = getFileExtName(fileName);
		if (StringUtil.isEmpty(fileName) || StringUtil.isEmpty(suffix)) {
			return fileName;
		}
		return fileName.substring(0, fileName.lastIndexOf(suffix) - 1);
	}

	/**
	 * 去除文件扩展名，例如：abc.txt -> abc
	 *
	 * @param file
	 * @return 如果fileName为空或不存在后缀名则返回自身
	 */
	public static String removeFileExtName(File file) {
		if (file != null) {
			return removeFileExtName(file.getName());
		}
		return null;
	}

	/**
	 * 返回文件是否存在
	 */
	public static boolean isExists(String filepath) {
		if (StringUtil.isEmpty(StringUtil.trim(filepath)))
			return false;
		File f = new File(filepath);
		return f.exists();
	}
}
