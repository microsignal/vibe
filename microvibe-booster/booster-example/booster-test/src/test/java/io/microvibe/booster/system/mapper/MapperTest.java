package io.microvibe.booster.system.mapper;

import com.alibaba.fastjson.JSON;
import io.microvibe.booster.commons.spring.ApplicationContextHolder;
import io.microvibe.booster.config.ProfileNames;
import io.microvibe.booster.core.base.mybatis.annotation.AutoMapper;
import io.microvibe.booster.system.entity.A;
import io.microvibe.booster.system.entity.B;
import io.microvibe.booster.test.BaseTestCase;
import org.junit.Test;
import org.springframework.core.annotation.AliasFor;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.*;
import java.util.List;

/**
 * @author Qt
 * @since Aug 25, 2018
 */
@ActiveProfiles(inheritProfiles = false, value = ProfileNames.DEV)
public class MapperTest extends BaseTestCase {

	public static void main(String[] args) {
		System.out.println(AnnotationUtils.findAnnotation(I1.class, AutoMapper.class));
		System.out.println(AnnotationUtils.findAnnotation(I2.class, AutoMapper.class));
		System.out.println(AnnotationUtils.findAnnotation(I3.class, AutoMapper.class));
		System.out.println(AnnotationUtils.findAnnotation(I4.class, AutoMapper.class));
		System.out.println(AnnotatedElementUtils.getMergedAnnotation(I4.class, AutoMapper.class));
	}

	@Test
	public void test001() {
		EntityATestMapper mapper = ApplicationContextHolder.getBean(EntityATestMapper.class);

		A a = mapper.getById(1L);
		System.out.println(a);
		A param = new A();
		param.setId(1L);
		List<A> list = mapper.selectByEntity(param);
		System.out.println(JSON.toJSONString(list, true));
	}

	@Test
	@Transactional
	public void test002() {
		EntityATestMapper mapper = ApplicationContextHolder.getBean(EntityATestMapper.class);

		B b = new B();
		b.setUserId(2L);
//		b.setId(2L);
		System.out.println(b);
		long row = mapper.insertB(b);
		System.out.println("rows:" + row);
		System.out.println(b);
	}

	@Documented
	@Inherited
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.METHOD})
	static @interface AutoMapper {

		Class<?> value() default void.class;
		Class<?> value2() default void.class;
	}
	@AutoMapper(A.class)
	static interface I1 {
	}

	@AutoMapper(B.class)
	static interface I2 {
	}

	static interface I3 extends I1 {
	}

	@AutoMapper(value2 = B.class)
	static interface I4 extends I1 {
	}
}
