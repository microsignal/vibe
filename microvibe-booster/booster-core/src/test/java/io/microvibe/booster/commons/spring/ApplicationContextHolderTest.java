package io.microvibe.booster.commons.spring;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.Date;

/**
 * @author Qt
 * @since Aug 12, 2018
 */
@Ignore
public class ApplicationContextHolderTest {

	@Test
	public void test() {
		ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
		System.out.println(applicationContext);
		System.out.println(applicationContext.getDisplayName());
		System.out.println(applicationContext.getApplicationName());
		System.out.println(new Date(applicationContext.getStartupDate()));
		System.out.println(Arrays.toString(applicationContext.getBeanDefinitionNames()));
	}
}
