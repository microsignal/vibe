package io.microvibe.booster.commons.cache.impl;

import redis.clients.jedis.Protocol;
import redis.clients.util.SafeEncoder;

import java.io.*;

public class Serializers {
	public static final byte[] STRING_PREFIX;
	public static final byte[] OBJECT_PREFIX;

	static {
		try {
			STRING_PREFIX = "s~>".getBytes(Protocol.CHARSET);
			OBJECT_PREFIX = "o~>".getBytes(Protocol.CHARSET);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static boolean startsWith(byte[] bytes, byte[] prefix) {
		if (bytes.length < prefix.length) {
			return false;
		}
		for (int i = 0; i < prefix.length; i++) {
			if (bytes[i] != prefix[i]) {
				return false;
			}
		}
		return true;
	}

	public static byte[] trimPrefix(byte[] bytes, int trimLen) {
		if (trimLen >= bytes.length) {
			return null;
		}
		byte[] keyBytes = new byte[bytes.length - trimLen];
		System.arraycopy(bytes, trimLen, keyBytes, 0, keyBytes.length);
		return keyBytes;
	}

	public static byte[] join(byte[]... ibytes) {
		int length = 0;
		for (byte[] b : ibytes) {
			length += b.length;
		}
		byte[] joined = new byte[length];
		int pos = 0;
		for (byte[] b : ibytes) {
			System.arraycopy(b, 0, joined, pos, b.length);
			pos += b.length;
		}
		return joined;
	}

	public static byte[] join(byte[] a, byte[] b) {
		byte[] ab = new byte[a.length + b.length];
		System.arraycopy(a, 0, ab, 0, a.length);
		System.arraycopy(b, 0, ab, a.length, b.length);
		return ab;
	}

	public static byte[] serialize(Object object) {
        /*if (object == null) {
            return null;
        }*/
		if (object != null && object instanceof String) {
			byte[] payload = SafeEncoder.encode((String) object);
			byte[] bytes = join(STRING_PREFIX, payload);
			return bytes;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
		try {
			baos.write(OBJECT_PREFIX);
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			oos.flush();
		} catch (Exception ex) {
			throw new IllegalArgumentException("Failed to serialize object of type: " + object.getClass(), ex);
		}
		return baos.toByteArray();
	}

	public static Object deserialize(byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		if (startsWith(bytes, STRING_PREFIX)) {
			try {
				String s = new String(bytes, STRING_PREFIX.length, bytes.length - STRING_PREFIX.length,
					Protocol.CHARSET);
				return s;
			} catch (UnsupportedEncodingException e) {
				throw new IllegalArgumentException(e);
			}
		}
		try {
			ObjectInputStream ois = new ObjectInputStream(
				new ByteArrayInputStream(bytes, OBJECT_PREFIX.length, bytes.length - OBJECT_PREFIX.length));
			return ois.readObject();
		} catch (Exception ex) {
			throw new IllegalArgumentException("Failed to deserialize object", ex);
		}
	}

}
