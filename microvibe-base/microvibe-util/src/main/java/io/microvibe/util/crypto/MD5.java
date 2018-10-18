package io.microvibe.util.crypto;

/**
 * MD5加密
 * @since 2017/09/25
 * @author Qt
 */
public class MD5 {

	/**
	 * MD5加密
	 * @param str 待加密的字符串
	 * @return
	 */
	public static String encrypt(String str) {
		return MessageDigestUtil.MD5(str);
	}

	/**
	 * MD5加密
	 * @param str 待加密的字符串
	 * @param upperCase 大小写
	 * @return
	 */
	public static String encrypt(String str, boolean upperCase) {
		String result = MessageDigestUtil.MD5(str);
		if (upperCase) {
			result = result.toUpperCase();
		}
		return result;
	}

}
