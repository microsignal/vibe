package io.microvibe.util.id;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.UUID;

public class IDUtil {
	private IDUtil() {
	}

	private static final char[] BIT32_UPPER = {
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
			'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
			// 'W', 'X', 'Y', 'Z',
	};
	private static final char[] BIT32_LOWER = {
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
			'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
			// 'w', 'x', 'y', 'z',
	};

	private static SecureRandom secureRandom = new SecureRandom();// SecureRandom.getInstance("SHA1PRNG");
	private static final char[] baseCharsLower = new char[12];
	private static final char[] baseCharsUpper = new char[12];
	static {
		Calendar cal = Calendar.getInstance();
		long time = cal.getTimeInMillis();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		time = (time - cal.getTimeInMillis());
		int dayMills = (int) (time & 0xfffffff);// 28

		byte[] bytes = new byte[4];
		try {
			bytes = InetAddress.getLocalHost().getAddress();
		} catch (UnknownHostException e) {
			bytes[0] = (byte) secureRandom.nextInt(0x100);
			bytes[1] = (byte) secureRandom.nextInt(0x100);
			bytes[2] = (byte) secureRandom.nextInt(0x100);
			bytes[3] = (byte) secureRandom.nextInt(0x100);
		}

		int[] bits = new int[12];
		int idx = 0;
		bits[idx++] = bytes[0] >>> 3 & 0x1F;
		bits[idx++] = (bytes[0] & 0x7) << 2 | (bytes[1] & 0xFF >>> 6);
		bits[idx++] = (bytes[1] & 0x3F) >>> 1;
		bits[idx++] = (bytes[1] & 0x1) << 4 | (bytes[2] & 0xFF >>> 4);
		bits[idx++] = (bytes[2] & 0xF) << 1 | (bytes[3] & 0xFF >>> 7);
		bits[idx++] = (bytes[3] & 0x7F) >>> 2;
		bits[idx++] = (bytes[3] & 0x3) << 3 | (dayMills >>> 25);
		bits[idx++] = dayMills >>> 20 & 0x1F;
		bits[idx++] = dayMills >>> 15 & 0x1F;
		bits[idx++] = dayMills >>> 10 & 0x1F;
		bits[idx++] = dayMills >>> 5 & 0x1F;
		bits[idx++] = dayMills & 0x1F;
		for (int i = 0; i < bits.length; i++) {
			baseCharsUpper[i] = BIT32_UPPER[bits[i]];
			baseCharsLower[i] = BIT32_LOWER[bits[i]];
		}
	}

	public static String getKey(boolean lowercase) {
		if (lowercase) {
			return getKey(IDUtil.BIT32_LOWER, baseCharsLower);
		}
		return getKey(IDUtil.BIT32_UPPER, baseCharsUpper);
	}

	public static String getKey() {
		return getKey(IDUtil.BIT32_UPPER, baseCharsUpper);
	}

	private static String getKey(char[] bit32, char[] baseChars) {
		char[] chs = new char[32];
		Calendar cal = Calendar.getInstance();
		int idx = 0;

		System.arraycopy(baseChars, 0, chs, idx, 12);
		idx += 12;

		chs[idx++] = bit32[secureRandom.nextInt(0x20)];
		chs[idx++] = bit32[secureRandom.nextInt(0x20)];
		chs[idx++] = bit32[secureRandom.nextInt(0x20)];
		chs[idx++] = bit32[secureRandom.nextInt(0x20)];

		int year = cal.get(Calendar.YEAR);
		chs[idx++] = bit32[year / 1000];
		chs[idx++] = bit32[year % 1000 / 100];
		chs[idx++] = bit32[year % 100 / 10];
		chs[idx++] = bit32[year % 10];
		int month = cal.get(Calendar.MONTH);
		chs[idx++] = bit32[month / 10];
		chs[idx++] = bit32[month % 10];
		int day = cal.get(Calendar.DAY_OF_MONTH);
		chs[idx++] = bit32[day / 10];
		chs[idx++] = bit32[day % 10];
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		chs[idx++] = bit32[hour / 10];
		chs[idx++] = bit32[hour % 10];
		int minute = cal.get(Calendar.MINUTE);
		chs[idx++] = bit32[minute / 10];
		chs[idx++] = bit32[minute % 10];
		int second = cal.get(Calendar.SECOND);
		chs[idx++] = bit32[second / 10];
		chs[idx++] = bit32[second % 10];

		int time = (int) cal.getTimeInMillis();
		chs[idx++] = bit32[time >>> 5 & 0x1F];
		chs[idx++] = bit32[time & 0x1F];

		return new String(chs);
	}

	public static String uuid(boolean compact, boolean uppercase) {
		String uuid = UUID.randomUUID().toString();
		if (compact) {
			uuid = uuid.replace("-", "");
		}
		if (uppercase) {
			uuid = uuid.toUpperCase();
		}
		return uuid;
	}

	public static String uuid() {
		return uuid(false, false);
	}

	public static String uuidUpper() {
		return uuid(false, true);
	}

	public static String uuidCompact() {
		return uuid(true, false);
	}

	public static String uuidCompactUpper() {
		return uuid(true, true);
	}

	public static void main(String[] args) throws Exception {
		System.out.println(uuidCompactUpper());
		System.out.println(uuidCompact());
		System.out.println(uuidUpper());
		System.out.println(uuid());
		System.out.println(getKey());
		System.out.println(getKey());
		System.out.println(getKey());
		System.out.println(System.nanoTime() / 1000);
		System.out.println(System.nanoTime() / 1000);
		System.out.println(System.nanoTime());
		System.out.println(System.nanoTime());
		System.out.println(Long.toHexString(System.nanoTime()));
		System.out.println(Long.toString(System.nanoTime(), 36));
		System.out.println(Long.toString(Long.MAX_VALUE, 36));
	}
}
