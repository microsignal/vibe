package io.microvibe.booster.commons.string;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author Qt
 * @since Jul 13, 2018
 */
public class Xmls {
	private static final String[] CHARS_ORIGIN = {
		"&", "<", ">", "\"", "'"
	};
	private static final String[] CHARS_ENTITY = {
		"&amp;", "&lt;", "&gt;", "&quot;", "&apos;"
	};

	public static String unescape(final String fieldValue) {
		String fv = fieldValue;
		for (int i = 0; i < CHARS_ENTITY.length; ++i) {
			fv = fv.replaceAll(CHARS_ENTITY[i], CHARS_ORIGIN[i]);
		}
		return fv;
	}

	public static String escape(final String fieldValue) {
		String fv = fieldValue;
		for (int i = 0; i < CHARS_ORIGIN.length; ++i) {
			fv = fv.replaceAll(CHARS_ORIGIN[i], CHARS_ENTITY[i]);
		}
		return fv;
	}

	public static Object fromXml(final String str) {
		ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes());
		XMLDecoder d = new XMLDecoder(in);
		Object o = d.readObject();
		d.close();
		return o;
	}

	public static String toXml(final Object o) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		XMLEncoder e = new XMLEncoder(out);
		e.writeObject(o);
		e.flush();
		e.close();
		return new String(out.toByteArray());
	}
}
