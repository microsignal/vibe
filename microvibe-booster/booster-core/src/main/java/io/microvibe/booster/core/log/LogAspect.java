package io.microvibe.booster.core.log;

import io.microvibe.booster.commons.utils.HttpWebUtils;
import io.microvibe.booster.core.base.utils.RequestContextUtils;
import io.microvibe.booster.core.env.BootConstants;
import io.microvibe.booster.core.env.SystemEnv;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Description;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

/**
 * @author Qt
 * @since May 15, 2018
 */
@Aspect
@Order(BootConstants.ASPECT_ORDER_OF_LOG_ASPECT)
@Component
@Slf4j
public class LogAspect {

	@Autowired
	SystemEnv systemEnv;
	@Autowired
	private ApplicationContext context;
	@Autowired(required = false)
	private IMethodTraceService logService;
	private ThreadLocal<LogInfo> local = ThreadLocal.withInitial(() -> new LogInfo());

	@PostConstruct
	public void init() {
	}

	@Pointcut("@annotation(io.microvibe.booster.core.log.Log)")
	private void pointcut() {
	}

	@Around(value = "pointcut()")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Object target = joinPoint.getTarget();
		Class<?> clazz = target.getClass();
		Method method = signature.getMethod();

		Logger logger = LoggerFactory.getLogger(clazz);
		try {
			before(logger, joinPoint);
			Object rs = joinPoint.proceed(joinPoint.getArgs());
			after(logger, joinPoint, rs);
			return rs;
		} catch (Throwable throwable) {
			afterThrowing(logger, joinPoint, throwable);
			throw throwable;
		} finally {
			local.remove();
		}
	}

	private void before(Logger logger, ProceedingJoinPoint joinPoint) {
		try {
			LogInfo info = local.get();

			MethodSignature signature = (MethodSignature) joinPoint.getSignature();
			Method method = signature.getMethod();
			Log logAnno = AnnotationUtils.findAnnotation(method, Log.class);

			// level
			info.setLevel(logAnno.level());
			info.setErrorLevel(logAnno.errorLevel());
			info.addPointcut(logAnno.pointcut());

			// signature
			info.setSignature(signature);

			// request
			HttpServletRequest request = RequestContextUtils.currentHttpRequest();
			info.setRequestIp(HttpWebUtils.getIpAddr(request));
			info.setRequestUri(HttpWebUtils.getRequestURI(request));
			info.setRequestMethod(HttpWebUtils.getRequestMethod(request));

			// class
			Object target = joinPoint.getTarget();
			info.setWithinType(target.getClass());

			// method
			info.setMethodName(method.getName());

			// return type
			info.setMethodReturnType(signature.getReturnType());

			// args
			info.setMethodArgs(joinPoint.getArgs());

			// module & content
			info.setModule(logAnno.module());
			String content = StringUtils.trimToEmpty(logAnno.content());
			if (content.length() == 0) {
				content = getDescription(method);
			}
			info.setContent(content);
			if (systemEnv.getMethodLogLevel().ordinal() <= info.getLevel().ordinal()
				&& info.hasPointcut(Log.Pointcut.Before)) {
				if (logService == null || !logService.logForBefore(info)) {
					switch (info.getLevel()) {
						case DEBUG:
							logger.debug("Exec-IP: {}", info.getRequestIp());
							logger.debug("Exec-Module: {}", info.getModule());
							logger.debug("Exec-Content: {}", info.getContent());
							logger.debug("Exec-Method: {}", info.getSignatureString());
							logger.debug("Exec-Args: {}", info.getMethodArgsString());
							break;
						case INFO:
							logger.info("Exec-IP: {}", info.getRequestIp());
							logger.info("Exec-Module: {}", info.getModule());
							logger.info("Exec-Content: {}", info.getContent());
							logger.info("Exec-Method: {}", info.getSignatureString());
							logger.info("Exec-Args: {}", info.getMethodArgsString());
							break;
						case WARN:
							logger.warn("Exec-IP: {}", info.getRequestIp());
							logger.warn("Exec-Module: {}", info.getModule());
							logger.warn("Exec-Content: {}", info.getContent());
							logger.warn("Exec-Method: {}", info.getSignatureString());
							logger.warn("Exec-Args: {}", info.getMethodArgsString());
							break;
						case ERROR:
							logger.error("Exec-IP: {}", info.getRequestIp());
							logger.error("Exec-Module: {}", info.getModule());
							logger.error("Exec-Content: {}", info.getContent());
							logger.error("Exec-Method: {}", info.getSignatureString());
							logger.error("Exec-Args: {}", info.getMethodArgsString());
							break;
					}
				}
			}
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
		}

	}

	private void after(Logger logger, ProceedingJoinPoint joinPoint, Object rs) {
		try {
			LogInfo info = local.get();
			if (systemEnv.getMethodLogLevel().ordinal() <= info.getLevel().ordinal()
				&& (info.hasPointcut(Log.Pointcut.AfterReturning) || info.hasPointcut(Log.Pointcut.After))) {
				info.setMethodReturnValue(rs);
				if (logService == null || !logService.logForAfter(info)) {
					switch (info.getLevel()) {
						case DEBUG:
							logger.debug("Exec-Result: {}", info.getMethodReturnValue());
							break;
						case INFO:
							logger.info("Exec-Result: {}", info.getMethodReturnValue());
							break;
						case WARN:
							logger.warn("Exec-Result: {}", info.getMethodReturnValue());
							break;
						case ERROR:
							logger.error("Exec-Result: {}", info.getMethodReturnValue());
							break;
					}
				}
			}
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
		}
	}

	private void afterThrowing(Logger logger, ProceedingJoinPoint joinPoint, Throwable throwable) {
		try {
			LogInfo info = local.get();
			if (systemEnv.getMethodLogLevel().ordinal() <= info.getErrorLevel().ordinal()
				&& (info.hasPointcut(Log.Pointcut.AfterThrowing) || info.hasPointcut(Log.Pointcut.After))) {
				info.setThrowable(throwable);
				if (logService == null || !logService.logForThrowing(info)) {
					switch (info.getLevel()) {
						case DEBUG:
							logger.debug("Exec-Throws: {}", throwable.toString());
							break;
						case INFO:
							logger.info("Exec-Throws: {}", throwable.toString());
							break;
						case WARN:
							logger.warn("Exec-Throws: {}", throwable.toString());
							break;
						case ERROR:
							logger.error("Exec-Result: {}", throwable.toString());
							break;
					}
				}
			}
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
		}
	}

	private String getDescription(AnnotatedElement element) {
		Description description = AnnotationUtils.findAnnotation(element, Description.class);
		if (description != null) {
			return description.value();
		}
		return "";
	}


}
