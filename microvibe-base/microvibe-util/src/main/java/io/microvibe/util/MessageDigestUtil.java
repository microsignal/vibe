package io.microvibe.util;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang3.StringUtils;

public class MessageDigestUtil {
	private MessageDigestUtil() {
	}

	public static String MD5(String s) {
		if (StringUtils.isEmpty(s) || StringUtils.isBlank(s)) {
			return null;
		}
		return MD5(s.getBytes());
	}

	public static String SHA1(String s) {
		if (StringUtils.isEmpty(s) || StringUtils.isBlank(s)) {
			return null;
		}
		return SHA1(s.getBytes());
	}

	public static String SHA1(byte[] btInput) {
		try {
			MessageDigest mdInst = MessageDigest.getInstance("SHA1");
			mdInst.update(btInput);
			byte[] md = mdInst.digest();
			return StringUtil.toHex(md);
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}

	public static String MD5(byte[] btInput) {
		try {
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			mdInst.update(btInput);
			byte[] md = mdInst.digest();
			return StringUtil.toHex(md);
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}

	public static String getHashCode(Object object) throws IOException {
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
		ss = MD5(bo.toString());
		return ss;
	}

}
