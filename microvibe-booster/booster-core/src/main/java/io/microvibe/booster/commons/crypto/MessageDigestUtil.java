package io.microvibe.booster.commons.crypto;


import io.microvibe.booster.commons.err.CryptoException;
import io.microvibe.booster.commons.string.Hex;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MessageDigestUtil {
	private MessageDigestUtil() {
	}

	public static String md5AsString(String s) {
		return s == null ? null : md5AsString(s.getBytes());
	}

	public static String sha1AsString(String s) {
		return s == null ? null : sha1AsString(s.getBytes());
	}

	public static String sha1AsString(byte[] btInput) {
		byte[] md = sha1(btInput);
		return Hex.toHex(md);
	}

	public static byte[] sha1(byte[] btInput) {
		try {
			MessageDigest mdInst = MessageDigest.getInstance("SHA1");
			mdInst.update(btInput);
			byte[] md = mdInst.digest();
			return md;
		} catch (NoSuchAlgorithmException e) {
			throw new CryptoException(e);
		}
	}

	public static byte[] md5(byte[] btInput) {
		try {
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			mdInst.update(btInput);
			byte[] md = mdInst.digest();
			return md;
		} catch (NoSuchAlgorithmException e) {
			throw new CryptoException(e);
		}
	}

	public static String md5AsString(byte[] btInput) {
		try {
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			mdInst.update(btInput);
			byte[] md = mdInst.digest();
			return Hex.toHex(md);
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}

	public static String md5AsString(Object object) throws IOException {
		if (object == null)
			return "";

		String ss = null;
		ObjectOutputStream s = null;
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		try {
			s = new ObjectOutputStream(bo);
			s.writeObject(object);
			s.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (s != null) {
				s.close();
				s = null;
			}
		}
		ss = md5AsString(bo.toByteArray());
		return ss;
	}

}
