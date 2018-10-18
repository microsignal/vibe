package io.microvibe.util;

import java.util.Collection;
import java.util.Map;

import io.microvibe.util.err.AssertException;

public class Asserts {
	private static String join(String... strings) {
		StringBuilder sb = new StringBuilder();
		for (String str : strings) {
			sb.append(str);
		}
		return sb.toString();
	}

	public static void raise() throws AssertException {
		throw new AssertException();
	}

	public static void raise(String message) throws AssertException {
		throw new AssertException(message);
	}

	public static void raise(String message, Object... args) throws AssertException {
		throw new AssertException(message, args);
	}

	public static void doesNotContain(String text, String substring) throws AssertException {
		doesNotContain(text, substring,
				join("the argument must not contain the substring [", substring, "]"));
	}

	public static void doesNotContain(String text, String substring, String message)
			throws AssertException {
		if (StringUtil.hasLength(text) && StringUtil.hasLength(substring)
				&& text.contains(substring)) {
			raise(message);
		}
	}

	public static void hasLength(String text) throws AssertException {
		hasLength(text, "the argument must have length; it must not be null or empty");
	}

	public static void hasLength(String text, String message) throws AssertException {
		if (!StringUtil.hasLength(text)) {
			raise(message);
		}
	}

	public static void hasText(String text) throws AssertException {
		hasText(text, "the argument must have text; it must not be null, empty, or blank");
	}

	public static void hasText(String text, String message) throws AssertException {
		if (!StringUtil.hasText(text)) {
			raise(message);
		}
	}

	public static void isAssignable(Class<?> superType, Class<?> subType) throws AssertException {
		isAssignable(superType, subType, "");
	}

	public static void isAssignable(Class<?> superType, Class<?> subType, String message)
			throws AssertException {
		notNull(superType, "Type to check against must not be null");
		if (subType == null || !superType.isAssignableFrom(subType)) {
			raise((StringUtil.hasLength(message) ? message + " " : "") + subType
					+ " is not assignable to " + superType);
		}
	}

	public static void isInstanceOf(Class<?> clazz, Object obj) throws AssertException {
		isInstanceOf(clazz, obj, "");
	}

	public static void isInstanceOf(Class<?> type, Object obj, String message)
			throws AssertException {
		notNull(type, "Type to check against must not be null");
		if (!type.isInstance(obj)) {
			raise((StringUtil.hasLength(message) ? message + " " : "") + "Object of class ["
					+ (obj != null ? obj.getClass().getName() : "null")
					+ "] must be an instance of " + type);
		}
	}

	public static void isNull(Object object) throws AssertException {
		isNull(object, "the object argument must be null");
	}

	public static void isNull(Object object, String message) throws AssertException {
		if (object != null) {
			raise(message);
		}
	}

	public static void isTrue(boolean expression) throws AssertException {
		isTrue(expression, "this expression must be true");
	}

	public static void isTrue(boolean expression, String message) throws AssertException {
		if (!expression) {
			raise(message);
		}
	}

	public static void noNullElements(Object[] array) throws AssertException {
		noNullElements(array, "this array must not contain any null elements");
	}

	public static void noNullElements(Object[] array, String message) throws AssertException {
		if (array != null) {
			for (Object element : array) {
				if (element == null) {
					raise(message);
				}
			}
		}
	}

	public static void notEmpty(Collection<?> collection) throws AssertException {
		notEmpty(collection,
				"this collection must not be empty: it must contain at least 1 element");
	}

	public static void notEmpty(Collection<?> collection, String message) throws AssertException {
		if (collection == null || collection.isEmpty()) {
			raise(message);
		}
	}

	public static void notEmpty(Map<?, ?> map) throws AssertException {
		notEmpty(map,
				"this map must not be empty; it must contain at least one entry");
	}

	public static void notEmpty(Map<?, ?> map, String message) throws AssertException {
		if (map == null || map.isEmpty()) {
			raise(message);
		}
	}

	public static void notEmpty(Object[] array) throws AssertException {
		notEmpty(array,
				"this array must not be empty: it must contain at least 1 element");
	}

	public static void notEmpty(Object[] array, String message) throws AssertException {
		if ((array == null || array.length == 0)) {
			raise(message);
		}
	}

	public static void notEmpty(String s) throws IllegalArgumentException {
		notNull(s);
		if (s.trim().length() == 0) raise();
	}

	public static void notEmpty(String s, String msg) throws AssertException {
		notNull(s, msg);
		if (s.trim().length() == 0) raise(msg);
	}

	public static void notNull(Object object) throws AssertException {
		notNull(object, "this argument is required; it must not be null");
	}

	public static void notNull(Object object, String message) throws AssertException {
		if (object == null) {
			raise(message);
		}
	}

	public static void state(boolean expression) throws AssertException {
		state(expression, "this state invariant must be true");
	}

	public static void state(boolean expression, String message) throws AssertException {
		if (!expression) {
			raise(message);
		}
	}

	private Asserts() {
	}

}
