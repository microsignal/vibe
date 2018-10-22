package io.microvibe.booster.test.generic;

import io.microvibe.booster.commons.utils.ReflectionUtils;
import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import java.beans.Introspector;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * @author Qt
 * @since Aug 27, 2018
 */
public class FnTest {
	private static final Pattern GET_PATTERN = Pattern.compile("^get[A-Z].*");
	private static final Pattern IS_PATTERN  = Pattern.compile("^is[A-Z].*");


	public String rand(){
		return RandomStringUtils.randomAlphanumeric(4);
	}
	@Test
	public void test() throws ReflectiveOperationException {
		System.out.println(fnToFieldName(FnTest::rand));
		System.out.println(ReflectionUtils.getPropertyName(FnTest::rand));
		System.out.println(ReflectionUtils.getPropertyName(BusinessObject::getProperty1));
		System.out.println(ReflectionUtils.getPropertyName(BusinessObject::getProperty2));
	}
	@Data
	static class BusinessObject {

		private String property1;

		private int property2;
	}

	public static interface Fn<T, R> extends Function<T, R>, Serializable {
	}
	public static <T, R> String fnToFieldName(Fn<T, R> fn) throws ReflectiveOperationException {
		try {
			Method method = fn.getClass().getDeclaredMethod("writeReplace");
			method.setAccessible(Boolean.TRUE);
			SerializedLambda serializedLambda = (SerializedLambda) method.invoke(fn);
			String getter = serializedLambda.getImplMethodName();
			if (GET_PATTERN.matcher(getter).matches()) {
				getter = getter.substring(3);
			} else if (IS_PATTERN.matcher(getter).matches()) {
				getter = getter.substring(2);
			}
			return Introspector.decapitalize(getter);
		} catch (ReflectiveOperationException e) {
			throw e;
		}
	}
}
