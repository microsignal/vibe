package io.microvibe.util.crypto;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;

import io.microvibe.util.StringUtil;
import io.microvibe.util.err.CryptoException;

/**
 * AES加解密
 * @since 2017/09/25
 * @author Qt
 */
public class AES {
	private static final String ALGORITHM = "AES";
	private static final String TOKEN = "629fd0d1a9504202862f27e970afcd2f";//

	/**
	 * 加密
	 * @param s	需要加密的字符串
	 * @param token	加密token，16进制的hex格式，必须是32长度的字符串。如果token不合法，返回null
	 * @return 加密后的字符串
	 * @throws Exception
	 */
	public static String encryptBase64(String s, String token) {
		byte[] key = StringUtil.fromHex(StringUtils.isNotBlank(token) ? token : TOKEN);
		return encryptBase64URLSafe(s, key);
	}

	/**
	 * 解密
	 * @param encrypted	已加密的字符串
	 * @param token	加密token，16进制的hex格式，必须是32长度的字符串。如果token不合法，返回空串
	 * @return	解密后的字符串
	 * @throws CryptoException
	 */
	public static String decryptBase64(String encrypted, String token) {
		byte[] key = StringUtil.fromHex(StringUtils.isNotBlank(token) ? token : TOKEN);
		return decryptBase64URLSafe(encrypted, key);
	}

	/**
	 * 加密
	 * @param s	需要加密的字符串
	 * @param token	加密token，16进制的hex格式，必须是32长度的字符串。如果token不合法，返回null
	 * @return 加密后的字符串
	 * @throws CryptoException
	 */
	public static String encrypt(String s, String token) throws CryptoException {
		byte[] key = StringUtil.fromHex(StringUtils.isNotBlank(token) ? token : TOKEN);
		byte[] encryptedBytes = encryptBytes(s, key);
		return StringUtil.toHex(encryptedBytes);
	}

	/**
	 * 解密
	 * @param encrypted	已加密的字符串
	 * @param token	加密token，16进制的hex格式，必须是32长度的字符串。如果token不合法，返回空串
	 * @return	解密后的字符串
	 * @throws CryptoException
	 */
	public static String decrypt(String encrypted, String token) throws CryptoException {
		byte[] key = StringUtil.fromHex(StringUtils.isNotBlank(token) ? token : TOKEN);
		byte[] encryptedBytes = StringUtil.fromHex(encrypted);
		String decrypted = decryptBytes(encryptedBytes, key);
		return decrypted;
	}

	private static byte[] encryptBytes(String s, byte[] key) {
		try {
			if (key == null || key.length != 16) {
				throw new CryptoException("invalid key");
			}
			SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			byte[] encrypted = cipher.doFinal(s.getBytes("utf-8"));
			return encrypted;
		} catch (CryptoException e) {
			throw e;
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
				| BadPaddingException | UnsupportedEncodingException e) {
			throw new CryptoException(e);
		}
	}

	private static String decryptBytes(byte[] encrypted, byte[] key) {
		try {
			if (key == null || key.length != 16) {
				throw new CryptoException("invalid key");
			}
			SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);

			byte[] original = cipher.doFinal(encrypted);
			String originalString = new String(original, "utf-8");
			return originalString;
		} catch (CryptoException e) {
			throw e;
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
				| BadPaddingException | UnsupportedEncodingException e) {
			throw new CryptoException(e);
		}
	}

	private static String encryptBase64URLSafe(String s, byte[] key) {
		byte[] encryptedBytes = encryptBytes(s, key);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(encryptedBytes);
	}

	private static String decryptBase64URLSafe(String encrypted, byte[] key) {
		byte[] encryptedBytes = Base64.getUrlDecoder().decode(encrypted);
		String decrypted = decryptBytes(encryptedBytes, key);
		return decrypted;
	}

}
