package io.microvibe.booster.commons.utils;

import java.math.BigDecimal;
import java.math.BigInteger;

public class BooleanUtils {

	private BooleanUtils() {
	}

	public static boolean isTrue(Object o) {
		if (o == null) return false;
		if (o instanceof Boolean) return isTrue((Boolean) o);
		if (o instanceof Number) return isTrue((Number) o);
		if (o instanceof Character) return isTrue((Character) o);
		if (o instanceof String) return isTrue((String) o);
		if (o instanceof String) return isTrue((String) o);
		if (o instanceof CharSequence) return isTrue((CharSequence) o);
		if (o instanceof BigDecimal) return isTrue((BigDecimal) o);
		if (o instanceof BigInteger) return isTrue((BigInteger) o);
		return true;
	}

	public static boolean isTrue(String s) {
		return s != null && s.trim().length() > 0;
	}

	public static boolean isTrue(CharSequence s) {
		return s != null && s.length() > 0;
	}

	public static boolean isTrue(Number n) {
		return n != null && n.intValue() != 0;
	}

	public static boolean isTrue(BigDecimal n) {
		return n != null && n.compareTo(BigDecimal.ZERO) != 0;
	}

	public static boolean isTrue(BigInteger n) {
		return n != null && n.compareTo(BigInteger.ZERO) != 0;
	}

	public static boolean isTrue(boolean b) {
		return b;
	}

	public static boolean isTrue(Boolean b) {
		return b != null && b.booleanValue();
	}

	public static boolean isTrue(int i) {
		return i != 0;
	}

	public static boolean isTrue(Integer i) {
		return i != null && i.intValue() != 0;
	}

	public static boolean isTrue(short n) {
		return n != 0;
	}

	public static boolean isTrue(Short n) {
		return n != null && n.intValue() != 0;
	}

	public static boolean isTrue(byte n) {
		return n != 0;
	}

	public static boolean isTrue(Byte n) {
		return n != null && n.intValue() != 0;
	}

	public static boolean isTrue(long n) {
		return n != 0;
	}

	public static boolean isTrue(Long n) {
		return n != null && n.intValue() != 0;
	}

	public static boolean isTrue(char c) {
		return c != 0;
	}

	public static boolean isTrue(Character c) {
		return c != null && c.charValue() != 0;
	}

	public static boolean isTrue(double n) {
		return n != 0;
	}

	public static boolean isTrue(Double n) {
		return n != null && n.intValue() != 0;
	}

	public static boolean isTrue(float n) {
		return n != 0;
	}

	public static boolean isTrue(Float n) {
		return n != null && n.intValue() != 0;
	}
}
