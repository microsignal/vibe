package io.microvibe.booster.core.base.aspect;

import io.microvibe.booster.core.env.BootConstants;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import javax.annotation.PostConstruct;

/**
 * @author Qt
 * @since Aug 02, 2018
 */
@Aspect
@Order(BootConstants.ASPECT_ORDER_OF_TRANSACTION)
@Component
@Slf4j
public class TxAdviceAspect {

	public static final String POINTCUT_EXPRESSION_OF_TRANSACTION =
		"execution(* " + BootConstants.BASE_PACKAGE + "..service..*+.*(..))"
			+ " || @within(javax.transaction.Transactional)"
			+ " || @within(org.springframework.transaction.annotation.Transactional)"
			+ " || @annotation(javax.transaction.Transactional)"
			+ " || @annotation(org.springframework.transaction.annotation.Transactional)"
			+ "";
	public static final String[] PROPAGATION_REQUIRES_NEW_METHODS
		= {"log*", "doLog*",};
	public static final String[] PROPAGATION_REQUIRES_READONLY_METHODS
		= {"get*", "count*", "find*", "list*", "query*", "select*", "is*", "has*", "exist*"};

	@Autowired
	private PlatformTransactionManager transactionManager;
	private TransactionInterceptor transactionInterceptor;
	private TransactionInterceptor transactionInterceptorAnno;

	@PostConstruct
	public void init() {
		transactionInterceptor = txAdvice();
		transactionInterceptorAnno = new TransactionInterceptor(transactionManager, new AnnotationTransactionAttributeSource(true));
	}


	@Around(value = "execution(* " + BootConstants.BASE_PACKAGE + "..service..*+.*(..))")
	public Object wildcardService(ProceedingJoinPoint joinPoint) throws Throwable {
		return transactionInterceptor.invoke(new MethodInvocationAdapter(joinPoint));
	}

	@Around(value = "@within(javax.transaction.Transactional)"
		+ " || @within(org.springframework.transaction.annotation.Transactional)"
		+ " || @annotation(javax.transaction.Transactional)"
		+ " || @annotation(org.springframework.transaction.annotation.Transactional)")
	public Object annotationService(ProceedingJoinPoint joinPoint) throws Throwable {
		return transactionInterceptorAnno.invoke(new MethodInvocationAdapter(joinPoint));
	}

	/*
	@Pointcut(POINTCUT_EXPRESSION_OF_TRANSACTION)
	private void pointcut() {
	}
	@Around(value = "pointcut()")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		try {
			// before
			MethodSignature signature = (MethodSignature) joinPoint.getSignature();
			Method method = signature.getMethod();
			Class<?> declaringType = signature.getDeclaringType();
			Object target = joinPoint.getTarget();
			Class<?> targetType = target.getClass();

			Object rs;
			if (AnnotationUtils.findAnnotation(method, javax.transaction.Transactional.class) != null
				|| AnnotationUtils.findAnnotation(method, org.springframework.transaction.annotation.Transactional.class) != null
				|| AnnotationUtils.findAnnotation(targetType, javax.transaction.Transactional.class) != null
				|| AnnotationUtils.findAnnotation(targetType, org.springframework.transaction.annotation.Transactional.class) != null
				|| AnnotationUtils.findAnnotation(declaringType, javax.transaction.Transactional.class) != null
				|| AnnotationUtils.findAnnotation(declaringType, org.springframework.transaction.annotation.Transactional.class) != null) {
				rs = transactionInterceptorAnno.invoke(new MethodInvocationAdapter(joinPoint));
			} else {
				rs = transactionInterceptor.invoke(new MethodInvocationAdapter(joinPoint));
			}
			// after
			return rs;
		} catch (Throwable throwable) {
			// after throwing
			throw throwable;
		} finally {
			// close
		}
	}

	*/
	private TransactionInterceptor txAdvice() {
		NameMatchTransactionAttributeSource source = new NameMatchTransactionAttributeSource();

		RuleBasedTransactionAttribute tran = new RuleBasedTransactionAttribute();
		tran.setPropagationBehavior(TransactionAttribute.PROPAGATION_REQUIRED);
		tran.setIsolationLevel(TransactionAttribute.ISOLATION_READ_COMMITTED);

		RuleBasedTransactionAttribute tranNew = new RuleBasedTransactionAttribute();
		tranNew.setPropagationBehavior(TransactionAttribute.PROPAGATION_REQUIRES_NEW);
		tranNew.setIsolationLevel(TransactionAttribute.ISOLATION_READ_COMMITTED);

		RuleBasedTransactionAttribute readOnly = new RuleBasedTransactionAttribute();
		readOnly.setPropagationBehavior(TransactionAttribute.PROPAGATION_REQUIRED);
		readOnly.setReadOnly(true);
		// REQUIRES_NEW
		for (String method : PROPAGATION_REQUIRES_NEW_METHODS) {
			source.addTransactionalMethod(method, tranNew);
		}

		// readOnly
		for (String method : PROPAGATION_REQUIRES_READONLY_METHODS) {
			source.addTransactionalMethod(method, readOnly);
		}

		// REQUIRED
		source.addTransactionalMethod("*", tran);

		TransactionInterceptor txAdvice = new TransactionInterceptor(transactionManager, source);
		return txAdvice;
	}

}
