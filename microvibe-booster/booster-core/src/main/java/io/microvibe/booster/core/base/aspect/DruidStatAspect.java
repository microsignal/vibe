package io.microvibe.booster.core.base.aspect;

import com.alibaba.druid.support.spring.stat.DruidStatInterceptor;
import io.microvibe.booster.core.env.BootConstants;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author Qt
 * @since Aug 02, 2018
 */
@Aspect
@Order(BootConstants.ASPECT_ORDER_OF_DRUID_MONITOR)
@Component
@Slf4j
public class DruidStatAspect {

	public static final String POINTCUT_EXPRESSION_OF_TRANSACTION =
		"execution(* " + BootConstants.BASE_PACKAGE + "..service..*+.*(..))"
			+ " || @within(javax.transaction.Transactional)"
			+ " || @within(org.springframework.transaction.annotation.Transactional)"
			+ " || @annotation(javax.transaction.Transactional)"
			+ " || @annotation(org.springframework.transaction.annotation.Transactional)"
			+ "";
	private DruidStatInterceptor druidStatInterceptor;

	@PostConstruct
	public void init() {
		druidStatInterceptor = new DruidStatInterceptor();
	}

	@Around(value = POINTCUT_EXPRESSION_OF_TRANSACTION)
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		return druidStatInterceptor.invoke(new MethodInvocationAdapter(joinPoint));
	}


}
