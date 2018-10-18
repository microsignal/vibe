package io.microvibe.booster.core.api.tools;

import io.microvibe.booster.commons.utils.property.PropertyUtil;
import io.microvibe.booster.core.api.ReplyCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

/**
 * @author Qt
 * @since Jul 05, 2018
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Assertion {

	private static final ReplyCode DEFAULT_REPLY_CODE = ReplyCode.RequestParamError;

	private static final String DEFAULT_MESSAGE = "参数对象校验失败";
	private static final String ARGS_MESSAGE = "参数%s校验失败";

	public static void fail(ReplyCode replyCode, String message) {
		replyCode.fail(message);
	}

	public static void fail(ReplyCode replyCode) {
		replyCode.fail();
	}

	public static void fail() {
		fail(DEFAULT_REPLY_CODE);
	}

	public static void fail(String message) {
		fail(DEFAULT_REPLY_CODE, message);
	}

	public static void isTrue(boolean expression) {
		isTrue(DEFAULT_MESSAGE, expression);
	}

	public static void isNull(Object object) {
		isNull(DEFAULT_MESSAGE, object);
	}

	public static void isEmpty(String str) {
		isEmpty(DEFAULT_MESSAGE, str);
	}

	public static void isBlank(String str) {
		isBlank(DEFAULT_MESSAGE, str);
	}

	public static void isEquals(Object object1, Object object2) {
		isEquals(DEFAULT_MESSAGE, object1, object2);
	}

	public static void isNotTrue(boolean expression) {
		isNotTrue(DEFAULT_MESSAGE, expression);
	}

	public static void isNotNull(Object object) {
		isNotNull(DEFAULT_MESSAGE, object);
	}

	public static void isNotEmpty(String str) {
		isNotEmpty(DEFAULT_MESSAGE, str);
	}

	public static void isNotBlank(String str) {
		isNotBlank(DEFAULT_MESSAGE, str);
	}

	public static void isNotEquals(Object object1, Object object2) {
		isNotEquals(DEFAULT_MESSAGE, object1, object2);
	}

	public static void isPattern(String message, Pattern pattern, String str) {
		isPattern(DEFAULT_REPLY_CODE, message, pattern, str);
	}

	public static void isPattern(String message, String pattern, String str) {
		isPattern(DEFAULT_REPLY_CODE, message, pattern, str);
	}

	public static void isTrue(String message, boolean expression) {
		isTrue(DEFAULT_REPLY_CODE, message, expression);
	}

	public static void isNull(String message, Object object) {
		isNull(DEFAULT_REPLY_CODE, message, object);
	}

	public static void isEmpty(String message, String str) {
		isEmpty(DEFAULT_REPLY_CODE, message, str);
	}

	public static void isBlank(String message, String str) {
		isBlank(DEFAULT_REPLY_CODE, message, str);
	}

	public static void isEquals(String message, Object object1, Object object2) {
		isEquals(DEFAULT_REPLY_CODE, message, object1, object2);
	}

	public static void isNotTrue(String message, boolean expression) {
		isNotTrue(DEFAULT_REPLY_CODE, message, expression);
	}

	public static void isNotNull(String message, Object object) {
		isNotNull(DEFAULT_REPLY_CODE, message, object);
	}

	public static void isNotEmpty(String message, String str) {
		isNotEmpty(DEFAULT_REPLY_CODE, message, str);
	}

	public static void isNotBlank(String message, String str) {
		isNotBlank(DEFAULT_REPLY_CODE, message, str);
	}

	public static void isNotEquals(String message, Object object1, Object object2) {
		isNotEquals(DEFAULT_REPLY_CODE, message, object1, object2);
	}

	public static void isPattern(ReplyCode replyCode, String message, Pattern pattern, String str) {
		replyCode.assertPattern(message, pattern, str);
	}

	public static void isPattern(ReplyCode replyCode, String message, String pattern, String str) {
		replyCode.assertPattern(message, pattern, str);
	}

	public static void isTrue(ReplyCode replyCode, String message, boolean expression) {
		replyCode.assertTrue(message, expression);
	}

	public static void isNull(ReplyCode replyCode, String message, Object object) {
		replyCode.assertNull(message, object);
	}

	public static void isEmpty(ReplyCode replyCode, String message, String str) {
		replyCode.assertEmpty(message, str);
	}

	public static void isBlank(ReplyCode replyCode, String message, String str) {
		replyCode.assertBlank(message, str);
	}

	public static void isEquals(ReplyCode replyCode, String message, Object object1, Object object2) {
		replyCode.assertEquals(message, object1, object2);
	}

	public static void isNotTrue(ReplyCode replyCode, String message, boolean expression) {
		replyCode.assertNotTrue(message, expression);
	}

	public static void isNotNull(ReplyCode replyCode, String message, Object object) {
		replyCode.assertNotNull(message, object);
	}

	public static void isNotEmpty(ReplyCode replyCode, String message, String str) {
		replyCode.assertNotEmpty(message, str);
	}

	public static void isNotBlank(ReplyCode replyCode, String message, String str) {
		replyCode.assertNotBlank(message, str);
	}

	public static void isNotEquals(ReplyCode replyCode, String message, Object object1, Object object2) {
		replyCode.assertNotEquals(message, object1, object2);
	}

	public static void isPattern(ReplyCode replyCode, String message, Pattern pattern, Object obj,
		String property) {
		replyCode.assertPattern(message, pattern, PropertyUtil.getPathPropertyAsString(obj, property));
	}

	public static void isPattern(ReplyCode replyCode, String message, String pattern, Object obj, String property) {
		replyCode.assertPattern(message, pattern, PropertyUtil.getPathPropertyAsString(obj, property));
	}

	public static void isTrue(ReplyCode replyCode, String message, Object obj, String property) {
		replyCode.assertTrue(message, PropertyUtil.getPathPropertyAsBoolean(obj, property));
	}

	public static void isNull(ReplyCode replyCode, String message, Object obj, String property) {
		replyCode.assertNull(message, PropertyUtil.getPathProperty(obj, property));
	}

	public static void isEmpty(ReplyCode replyCode, String message, Object obj, String property) {
		replyCode.assertEmpty(message, PropertyUtil.getPathPropertyAsString(obj, property));
	}

	public static void isBlank(ReplyCode replyCode, String message, Object obj, String property) {
		replyCode.assertBlank(message, PropertyUtil.getPathPropertyAsString(obj, property));
	}

	public static void isNotTrue(ReplyCode replyCode, String message, Object obj, String property) {
		replyCode.assertNotTrue(message, PropertyUtil.getPathPropertyAsBoolean(obj, property));
	}

	public static void isNotNull(ReplyCode replyCode, String message, Object obj, String property) {
		replyCode.assertNotNull(message, PropertyUtil.getPathProperty(obj, property));
	}

	public static void isNotEmpty(ReplyCode replyCode, String message, Object obj, String property) {
		replyCode.assertNotEmpty(message, PropertyUtil.getPathPropertyAsString(obj, property));
	}

	public static void isNotBlank(ReplyCode replyCode, String message, Object obj, String property) {
		replyCode.assertNotBlank(message, PropertyUtil.getPathPropertyAsString(obj, property));
	}

	public static void isPattern(String message, Pattern pattern, Object obj, String property) {
		isPattern(DEFAULT_REPLY_CODE, message, pattern, obj, property);
	}

	public static void isPattern(String message, String pattern, Object obj, String property) {
		isPattern(DEFAULT_REPLY_CODE, message, pattern, obj, property);
	}

	public static void isTrue(String message, Object obj, String property) {
		isTrue(DEFAULT_REPLY_CODE, message, obj, property);
	}

	public static void isNull(String message, Object obj, String property) {
		isNull(DEFAULT_REPLY_CODE, message, obj, property);
	}

	public static void isEmpty(String message, Object obj, String property) {
		isEmpty(DEFAULT_REPLY_CODE, message, obj, property);
	}

	public static void isBlank(String message, Object obj, String property) {
		isBlank(DEFAULT_REPLY_CODE, message, obj, property);
	}

	public static void isNotTrue(String message, Object obj, String property) {
		isNotTrue(DEFAULT_REPLY_CODE, message, obj, property);
	}

	public static void isNotNull(String message, Object obj, String property) {
		isNotNull(DEFAULT_REPLY_CODE, message, obj, property);
	}

	public static void isNotEmpty(String message, Object obj, String property) {
		isNotEmpty(DEFAULT_REPLY_CODE, message, obj, property);
	}

	public static void isNotBlank(String message, Object obj, String property) {
		isNotBlank(DEFAULT_REPLY_CODE, message, obj, property);
	}

	public static void isPattern(Pattern pattern, Object obj, String... property) {
		for (String s : property) {
			isPattern(DEFAULT_REPLY_CODE, String.format(ARGS_MESSAGE, s), pattern, obj, s);
		}
	}

	public static void isPattern(String pattern, Object obj, String... property) {
		for (String s : property) {
			isPattern(DEFAULT_REPLY_CODE, String.format(ARGS_MESSAGE, s), pattern, obj, s);
		}
	}

	public static void isTrue(Object obj, String... property) {
		for (String s : property) {
			isTrue(DEFAULT_REPLY_CODE, String.format(ARGS_MESSAGE, s), obj, s);
		}
	}

	public static void isNull(Object obj, String... property) {
		for (String s : property) {
			isNull(DEFAULT_REPLY_CODE, String.format(ARGS_MESSAGE, s), obj, s);
		}
	}

	public static void isEmpty(Object obj, String... property) {
		for (String s : property) {
			isEmpty(DEFAULT_REPLY_CODE, String.format(ARGS_MESSAGE, s), obj, s);
		}
	}

	public static void isBlank(Object obj, String... property) {
		for (String s : property) {
			isBlank(DEFAULT_REPLY_CODE, String.format(ARGS_MESSAGE, s), obj, s);
		}
	}

	public static void isNotTrue(Object obj, String... property) {
		for (String s : property) {
			isNotTrue(DEFAULT_REPLY_CODE, String.format(ARGS_MESSAGE, s), obj, s);
		}
	}

	public static void isNotNull(Object obj, String... property) {
		for (String s : property) {
			isNotNull(DEFAULT_REPLY_CODE, String.format(ARGS_MESSAGE, s), obj, s);
		}
	}

	public static void isNotEmpty(Object obj, String... property) {
		for (String s : property) {
			isNotEmpty(DEFAULT_REPLY_CODE, String.format(ARGS_MESSAGE, s), obj, s);
		}
	}

	public static void isNotBlank(Object obj, String... property) {
		for (String s : property) {
			isNotBlank(DEFAULT_REPLY_CODE, String.format(ARGS_MESSAGE, s), obj, s);
		}
	}
}
