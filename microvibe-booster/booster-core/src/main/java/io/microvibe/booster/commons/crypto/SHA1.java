package io.microvibe.booster.commons.crypto;

public class SHA1 {

	/**
	 * MD5加密
	 *
	 * @param str 待加密的字符串
	 * @return
	 */
	public static String encrypt(String str) {
		return MessageDigestUtil.sha1AsString(str);
	}

	/**
	 * MD5加密
	 *
	 * @param str       待加密的字符串
	 * @param upperCase 大小写
	 * @return
	 */
	public static String encrypt(String str, boolean upperCase) {
		String result = MessageDigestUtil.sha1AsString(str);
		if (upperCase) {
			result = result.toUpperCase();
		}
		return result;
	}

}
