package io.microvibe.booster.test.anno;

import io.microvibe.booster.test.generic.GenericTest;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @author Qt
 * @since Aug 26, 2018
 */
public class AnnoTest {


	@Test
	public void test() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.scan(AnnoTest.class.getPackage().getName());
		context.refresh();

	}

	@Component
	static class A implements InitializingBean,ApplicationContextAware {
		@Autowired
		B b;

		@Override
		public void afterPropertiesSet() throws Exception {
			System.out.println("xxx");
			System.out.println(b);
		}
		@PostConstruct
		private void init1(){
			System.out.println("111");
			System.out.println(b);
		}
		@PostConstruct
		private void init2(){
			System.out.println("222");
			System.out.println(b);
		}

		@Override
		public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
			System.out.println("context aware");
			System.out.println(b);
		}
	}
	@Component
	static class B extends  A{

	}

}
