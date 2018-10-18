package io.microvibe.booster.core.validation.validator;

import com.alibaba.fastjson.util.TypeUtils;
import com.baidu.unbiz.fluentvalidator.Validator;
import io.microvibe.booster.commons.err.ValidationException;
import io.microvibe.booster.core.base.controller.annotation.DataValid;
import io.microvibe.booster.core.base.controller.annotation.DataValids;
import lombok.Data;
import org.springframework.core.annotation.AliasFor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

/**
 * @author Qt
 * @since Jul 02, 2018
 */
@Component(Noop.BEAN_ID)
public class Noop extends AbstractValidator<Object> implements Validator<Object> {

	public static final String BEAN_ID = "NoopValidator";

	@Target({ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	@DataValid(Noop.class)
	public static @interface X {

		String[]  args() default { };
	}
	@Override
	public void doValidate(Object obj) {
	}

	public static class T  {

		public T(){}
		public T(String s){}


		@DataValids({
			@DataValid(NotNull.class),
		})
		@DataValid(Null.class)
		@X
		void fun(){

		}
	}
	public static enum E{
		a,b,c
	}
	public static void main(String[] args) throws  Exception{
		Method fun = T.class.getDeclaredMethod("fun");
		Set<DataValid> set = AnnotationUtils.getRepeatableAnnotations(fun, DataValid.class);
		for (DataValid dataValid : set) {
			System.out.println(dataValid);
		}

		Constructor<?>[] cs = T.class.getConstructors();
		for (Constructor<?> c : cs) {
			System.out.println(Arrays.toString(c.getParameterTypes()));
			for (Class<?> type : c.getParameterTypes()) {
//				TypeUtils.cast(, , )
			}
		}

		System.out.println(TypeUtils.castToJavaBean("a", E.class ));;
		System.out.println(TypeUtils.castToJavaBean("123", byte.class ));;
	}


}
