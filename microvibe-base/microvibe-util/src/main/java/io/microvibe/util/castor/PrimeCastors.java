package io.microvibe.util.castor;

public class PrimeCastors {

	public static boolean castToBoolean(final Object orig) {
		assert orig != null;
		boolean rs = false;
		if (orig instanceof Number) {
			rs = ((Number) orig).intValue() != 0;
		} else if (orig instanceof Character) {
			rs = ((Character) orig).charValue() != 0;
		} else {
			rs = Boolean.valueOf(orig.toString());
		}
		return rs;
	}

	public static int castToInt(final Object orig) {
		assert orig != null;
		int rs = 0;
		if (orig instanceof Number) {
			rs = ((Number) orig).intValue();
		} else if (orig instanceof Character) {
			rs = ((Character) orig).charValue();
		} else {
			try {
				rs = Integer.valueOf(orig.toString());
			} catch (final NumberFormatException e) {
			}
		}
		return rs;
	}

	public static long castToLong(final Object orig) {
		assert orig != null;
		long rs = 0;
		if (orig instanceof Number) {
			rs = ((Number) orig).longValue();
		} else if (orig instanceof Character) {
			rs = ((Character) orig).charValue();
		} else {
			try {
				rs = Long.valueOf(orig.toString());
			} catch (final NumberFormatException e) {
			}
		}
		return rs;
	}

	public static byte castToByte(final Object orig) {
		assert orig != null;
		byte rs = 0;
		if (orig instanceof Number) {
			rs = ((Number) orig).byteValue();
		} else if (orig instanceof Character) {
			rs = (byte) ((Character) orig).charValue();
		} else {
			try {
				rs = Byte.valueOf(orig.toString());
			} catch (final NumberFormatException e) {
			}
		}
		return rs;
	}

	public static short castToShort(final Object orig) {
		assert orig != null;
		short rs = 0;
		if (orig instanceof Number) {
			rs = ((Number) orig).shortValue();
		} else if (orig instanceof Character) {
			rs = (short) ((Character) orig).charValue();
		} else {
			try {
				rs = Short.valueOf(orig.toString());
			} catch (final NumberFormatException e) {
			}
		}
		return rs;
	}

	public static char castToChar(final Object orig) {
		assert orig != null;
		char rs = '\0';
		if (orig instanceof Number) {
			rs = (char) ((Number) orig).shortValue();
		} else if (orig instanceof Character) {
			rs = ((Character) orig).charValue();
		} else {
			final String s = orig.toString();
			if (s.length() > 0) {
				rs = s.charAt(0);
			}
		}
		return rs;
	}

	public static double castToDouble(final Object orig) {
		assert orig != null;
		double rs = 0;
		if (orig instanceof Number) {
			rs = ((Number) orig).doubleValue();
		} else if (orig instanceof Character) {
			rs = ((Character) orig).charValue();
		} else {
			try {
				rs = Double.valueOf(orig.toString());
			} catch (final NumberFormatException e) {
			}
		}
		return rs;
	}

	public static float castToFloat(final Object orig) {
		assert orig != null;
		float rs = 0;
		if (orig instanceof Number) {
			rs = ((Number) orig).floatValue();
		} else if (orig instanceof Character) {
			rs = ((Character) orig).charValue();
		} else {
			try {
				rs = Float.valueOf(orig.toString());
			} catch (final NumberFormatException e) {
			}
		}
		return rs;
	}
}
