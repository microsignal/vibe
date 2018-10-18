package io.microvibe.booster.core.api.tools;

import io.microvibe.booster.commons.crypto.SHA1;
import io.microvibe.booster.commons.string.Hex;

import java.security.SecureRandom;
import java.util.UUID;

public class AppKeyToolkit {
	private static class Holder {
		static final SecureRandom numberGenerator = new SecureRandom();
	}

	// app xxxx xxxxxxxxxx xx
	public static String genAppId() {
		String prefix = "app";
		String head = Hex.toHex(System.currentTimeMillis() / 1000 / 3600 / 24, 4);
		String uuid = UUID.randomUUID().toString();
		String sha1 = SHA1.encrypt(uuid).substring(0, 10);
		byte[] bytes = new byte[2];
		Holder.numberGenerator.nextBytes(bytes);
		String tail = Hex.toHex(bytes);
		return prefix + head + sha1 + tail;
	}

	public static String genAppSecret(String appId) {
		String uuid = UUID.randomUUID().toString();
		String sha1 = SHA1.encrypt(uuid + appId);
		return sha1;
	}

}
