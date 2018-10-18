package io.microvibe.booster.commons.string;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.util.Arrays;

/**
 * @author Qt
 * @since Jul 13, 2018
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Hex {

	private static char[] hexDigits = {
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
	};

	public static byte[] fromHex(final String str) throws IllegalArgumentException {
//		Assert.hasText(str);
		if (str.length() % 2 != 0) {
			throw new IllegalArgumentException();
		}
		char[] chs = str.toCharArray();
		byte[] b = new byte[chs.length / 2];
		for (int i = 0; i < b.length; i++) {

			int h = Arrays.binarySearch(hexDigits, Character.toLowerCase(chs[i * 2]));
			if (h < 0) {
				throw new IllegalArgumentException();
			}
			int l = Arrays
				.binarySearch(hexDigits, Character.toLowerCase(chs[i * 2 + 1]));
			if (l < 0) {
				throw new IllegalArgumentException();
			}
			b[i] = (byte) ((h << 4) | l);
		}
		return b;
	}

	public static String toHex(final byte l) {
		return toHex(l, 2);
	}

	public static String toHex(final byte[] b) {
		Assert.notNull(b);
		char str[] = new char[b.length * 2];
		int k = 0;
		for (int i = 0; i < b.length; i++) {
			byte byte0 = b[i];
			str[k++] = hexDigits[byte0 >>> 4 & 0xf];
			str[k++] = hexDigits[byte0 & 0xf];
		}
		return new String(str);
	}

	public static String toHex(final int l) {
		return toHex(l, 8);
	}

	public static String toHex(final long l) {
		return toHex(l, 16);
	}

	public static String toHex(final long l, final int length) {
		char[] ch = new char[length];
		int chPos = ch.length - 1;
		long tmp = l;
		while (chPos >= 0) {
			ch[chPos--] = hexDigits[(int) (tmp & 0xF)];
			tmp >>>= 4L;
		}
		return new String(ch, 0, length);
	}

	public static String toHex(final short l) {
		return toHex(l, 4);
	}

}
