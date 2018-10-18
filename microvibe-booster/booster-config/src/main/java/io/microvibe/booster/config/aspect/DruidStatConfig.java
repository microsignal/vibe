//package io.microvibe.booster.config.aspect;
//
//import com.alibaba.druid.support.spring.stat.DruidStatInterceptor;
//import io.microvibe.booster.core.env.BootConstants;
//import org.aspectj.lang.annotation.Aspect;
//import org.springframework.aop.Advisor;
//import org.springframework.aop.Pointcut;
//import org.springframework.aop.aspectj.AspectJExpressionPointcut;
//import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
//import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
//import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
//import org.springframework.aop.support.DefaultPointcutAdvisor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.stereotype.Component;
//
//import java.util.Arrays;
//
//@Configuration
//@Component
//@Aspect
//public class DruidStatConfig {
//
//	public static final String ASPECT_POINTCUT_EXPRESSION_OF_TRANSACTION = "execution(* com.antengine..service..*+.*(..))"
//		+ " or @within(javax.transaction.Transactional)"
//		+ " or @annotation(javax.transaction.Transactional)"
//		+ " or @within(org.springframework.transaction.annotation.Transactional)"
//		+ " or @annotation(org.springframework.transaction.annotation.Transactional)"
//		+ "";
//
//
//	@Bean
//	public DruidStatInterceptor druidStatInterceptor() {
//		return new DruidStatInterceptor();
//	}
//
////	@Bean
////	public BeanNameAutoProxyCreator druidStatInterceptorAutoProxy() {
////		BeanNameAutoProxyCreator bean = new BeanNameAutoProxyCreator();
////		bean.setProxyTargetClass(true);
////		bean.setBeanNames("*Service", "*ServiceImpl");
////		bean.setInterceptorNames("druidStatInterceptor");
////		bean.setOrder(BootConstants.ASPECT_ORDER_OF_DRUID_MONITOR);
////		return bean;
////	}
//
//
//	@Bean
//	public AnnotationAwareAspectJAutoProxyCreator druidStatInterceptorAutoProxy() {
//		AnnotationAwareAspectJAutoProxyCreator bean = new AnnotationAwareAspectJAutoProxyCreator();
//		bean.setProxyTargetClass(true);
//		bean.setIncludePatterns(Arrays.asList(".*Service", ".*ServiceImpl"));
//		bean.setInterceptorNames("druidStatInterceptor");
//		bean.setOrder(BootConstants.ASPECT_ORDER_OF_DRUID_MONITOR);
//		return bean;
//	}
//
//	/*@Bean
//	public Advisor druidAdvisor() {
//		AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
//		advisor.setExpression(ASPECT_POINTCUT_EXPRESSION_OF_TRANSACTION);
//		advisor.setAdvice(new DruidStatInterceptor());
//		advisor.setOrder(BootConstants.ASPECT_ORDER_OF_DRUID_MONITOR);
//		return advisor;
//	}*/
//
//	/*@Bean
//	public Pointcut druidStatPointcut() {
//		JdkRegexpMethodPointcut druidStatPointcut = new JdkRegexpMethodPointcut();
//		String patterns = "com\\.transnal\\..*\\.service\\..*";
//		druidStatPointcut.setPatterns(patterns);
//		return druidStatPointcut;
//	}*/
//	/*
//	<bean id="druidStatInterceptor"
//		class="com.alibaba.druid.support.spring.stat.DruidStatInterceptor"/>
//	<aop:config>
//		<aop:advisor id="druidAdvisor" advice-ref="druidStatInterceptor"
//				pointcut="execution(* com.antengine..service..*.*(..))" />
//	</aop:config>
//	 */
//}
