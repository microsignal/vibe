package io.microvibe.booster.core.base.shiro.authc;

import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.crypto.hash.Sha1Hash;
import org.apache.shiro.crypto.hash.Sha256Hash;

public class Hashes {

	public static int DEFAULT_ITERATIONS = 2;

	public static String md5(String text, String salt) {
		return new Md5Hash(text, salt, DEFAULT_ITERATIONS).toBase64();
	}

	public static String sha1(String text, String salt) {
		return new Sha1Hash(text, salt, DEFAULT_ITERATIONS).toBase64();
	}

	public static String sha256(String text, String salt) {
		return new Sha256Hash(text, salt, DEFAULT_ITERATIONS).toBase64();
	}

	public static void main(String[] args) {
		String s = md5("123456", "admin"+"937184cc47ffe6e749a3b93f85ccced8");
		System.out.println(s);
		System.out.println(s.equalsIgnoreCase("i4VG0/Lrx5cAM+EmcLucmA=="));

		String enc = AuthcKit.AuthcType.bcrypt.getEncryptor().encrypt("123456", "admin"+"937184cc47ffe6e749a3b93f85ccced8");
		System.out.println(enc);
		System.out.println(AuthcKit.AuthcType.bcrypt.getEncryptor().matches("123456", "admin"+"937184cc47ffe6e749a3b93f85ccced8", enc));

	}
}
