//package io.microvibe.booster.config.aspect;
//
//import io.microvibe.booster.core.env.BootConstants;
//import org.aspectj.lang.annotation.Aspect;
//import org.springframework.aop.Advisor;
//import org.springframework.aop.aspectj.AspectJExpressionPointcut;
//import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
//import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
//import org.springframework.aop.support.DefaultPointcutAdvisor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.interceptor.*;
//
//import java.util.Arrays;
//
//@Configuration
//@Aspect
//@Order(BootConstants.ASPECT_ORDER_OF_TRANSACTION)
//@Component
//public class TxAdviceConfig {
//
//
//	public static final String POINTCUT_EXPRESSION_OF_TRANSACTION = "execution(* com.antengine..service..*+.*(..))"
////		+ " or @within(javax.transaction.Transactional)"
////		+ " or @annotation(javax.transaction.Transactional)"
////		+ " or @within(org.springframework.transaction.annotation.Transactional)"
////		+ " or @annotation(org.springframework.transaction.annotation.Transactional)"
//		+ "";
//	public static final String[] PROPAGATION_REQUIRES_NEW_METHODS
//		= {"log*", "doLog*",};
//	public static final String[] PROPAGATION_REQUIRES_READONLY_METHODS
//		= {"get*", "count*", "find*", "list*", "query*", "select*", "is*", "has*", "exist*"};
//
//	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
//	@Autowired
//	private PlatformTransactionManager transactionManager;
//
//	@Bean
//	public TransactionInterceptor txAdvice() {
//		NameMatchTransactionAttributeSource source = new NameMatchTransactionAttributeSource();
//
//		RuleBasedTransactionAttribute tran = new RuleBasedTransactionAttribute();
//		tran.setPropagationBehavior(TransactionAttribute.PROPAGATION_REQUIRED);
//		tran.setIsolationLevel(TransactionAttribute.ISOLATION_READ_COMMITTED);
//
//		RuleBasedTransactionAttribute tranNew = new RuleBasedTransactionAttribute();
//		tranNew.setPropagationBehavior(TransactionAttribute.PROPAGATION_REQUIRES_NEW);
//		tranNew.setIsolationLevel(TransactionAttribute.ISOLATION_READ_COMMITTED);
//
//		RuleBasedTransactionAttribute readOnly = new RuleBasedTransactionAttribute();
//		readOnly.setPropagationBehavior(TransactionAttribute.PROPAGATION_REQUIRED);
//		readOnly.setReadOnly(true);
//		// REQUIRES_NEW
//		for (String method : PROPAGATION_REQUIRES_NEW_METHODS) {
//			source.addTransactionalMethod(method, tranNew);
//		}
//
//		// readOnly
//		for (String method : PROPAGATION_REQUIRES_READONLY_METHODS) {
//			source.addTransactionalMethod(method, readOnly);
//		}
//
//		// REQUIRED
//		source.addTransactionalMethod("*", tran);
//
//		TransactionInterceptor txAdvice = new TransactionInterceptor(transactionManager, source);
//		return txAdvice;
//	}
//
////	@Bean
////	public BeanNameAutoProxyCreator txAdviceInterceptorAutoProxy() {
////		BeanNameAutoProxyCreator bean = new BeanNameAutoProxyCreator();
////		bean.setProxyTargetClass(true);
////		bean.setBeanNames("*Service", "*ServiceImpl");
////		bean.setInterceptorNames("txAdvice");
////		bean.setOrder(BootConstants.ASPECT_ORDER_OF_TRANSACTION);
////		return bean;
////	}
//
////	@Bean
////	public AnnotationAwareAspectJAutoProxyCreator txAdviceInterceptorAutoProxy() {
////		AnnotationAwareAspectJAutoProxyCreator bean = new AnnotationAwareAspectJAutoProxyCreator();
////		bean.setProxyTargetClass(true);
////		bean.setOrder(BootConstants.ASPECT_ORDER_OF_TRANSACTION);
////		bean.setIncludePatterns(Arrays.asList(".*Service", ".*ServiceImpl"));
////		bean.setInterceptorNames("txAdvice");
////		return bean;
////	}
//	@Bean
//	public Advisor txAdviceAdvisor() {
//		AspectJExpressionPointcut txPointcut = new AspectJExpressionPointcut();
//		txPointcut.setExpression(POINTCUT_EXPRESSION_OF_TRANSACTION);
//		DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
//		advisor.setPointcut(txPointcut);
//		advisor.setAdvice(txAdvice());
//		advisor.setOrder(BootConstants.ASPECT_ORDER_OF_TRANSACTION);
//		return advisor;
//	}
//	/*
//	@Bean
//	public Advisor txAdviceAdvisor() {
//		AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
//		advisor.setExpression(POINTCUT_EXPRESSION_OF_TRANSACTION);
//		advisor.setAdvice(txAdvice());
//		advisor.setOrder(BootConstants.ASPECT_ORDER_OF_TRANSACTION);
//		return advisor;
//	}
//	@Bean
//	public Advisor txAdviceAdvisor() {
//		AspectJExpressionPointcut txPointcut = new AspectJExpressionPointcut();
//		txPointcut.setExpression(POINTCUT_EXPRESSION_OF_TRANSACTION);
//		DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(txPointcut, txAdvice());
//		advisor.setAdvice(txAdvice());
//		advisor.setOrder(BootConstants.ASPECT_ORDER_OF_TRANSACTION);
//		return advisor;
//	}*/
//}
