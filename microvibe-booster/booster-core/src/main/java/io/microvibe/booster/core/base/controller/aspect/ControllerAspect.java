package io.microvibe.booster.core.base.controller.aspect;

import io.microvibe.booster.commons.spring.ApplicationContextHolder;
import io.microvibe.booster.core.api.ApiConstants;
import io.microvibe.booster.core.api.ApiException;
import io.microvibe.booster.core.api.ReplyCode;
import io.microvibe.booster.core.api.model.Data;
import io.microvibe.booster.core.api.model.HeadModel;
import io.microvibe.booster.core.base.controller.annotation.UserAuthc;
import io.microvibe.booster.core.base.controller.annotation.UserAuthz;
import io.microvibe.booster.core.base.resource.annotation.ResourceIdentity;
import io.microvibe.booster.core.base.utils.RequestContextUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Description;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;

@Component
@Aspect
@Order(0)
public class ControllerAspect {
	static Logger logger = LoggerFactory.getLogger(ControllerAspect.class);

	@Value("${spring.mvc.error.page:error/error.ftl}")
	private String errorPage;

	@Pointcut(" @within(org.springframework.stereotype.Controller) " +
		"|| @within(org.springframework.web.bind.annotation.RestController)" +
		"|| bean(*Controller)" +
		"|| @annotation(org.springframework.web.bind.annotation.RequestMapping)" +
		"|| @annotation(org.springframework.web.bind.annotation.GetMapping)" +
		"|| @annotation(org.springframework.web.bind.annotation.PutMapping)" +
		"|| @annotation(org.springframework.web.bind.annotation.PostMapping)" +
		"|| @annotation(org.springframework.web.bind.annotation.DeleteMapping)" +
		"|| @annotation(org.springframework.web.bind.annotation.PatchMapping)"
	)
	public void around() {
	}

	@Around(value = "around()")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Object target = joinPoint.getTarget();
		Class<?> ctrlerType = target.getClass();
		Method method = signature.getMethod();
		boolean isAjax = AnnotationUtils.findAnnotation(method, ResponseBody.class) != null
			|| AnnotationUtils.findAnnotation(ctrlerType, ResponseBody.class) != null;
		try {
			log(ctrlerType, method);
			checkAuth(ctrlerType, method);
			Object rs = joinPoint.proceed(joinPoint.getArgs());
			fillResponseApiData(rs);
			return rs;
		} catch (RedirectException e) {
			throw e;
		} catch (ApiException e) {
			if (!isAjax) {
				throw new RedirectException(e, errorPage);
			}
			throw e;
		} catch (Throwable e) {
			if (!isAjax) {
				throw new RedirectException(e, errorPage);
			}
			throw e;
		} finally {
		}
	}

	private void log(Class<?> ctrlerType, Method method) {
		Description description = AnnotationUtils.findAnnotation(method, Description.class);
		HttpServletRequest request = RequestContextUtils.currentHttpRequest();
		String requestURI = request.getRequestURI().substring(request.getContextPath().length());
		String funcName = description != null ? description.value() : "";
		logger.info("控制器方法: {}.{}", ctrlerType.getName(), method.getName());
		logger.info("控制器功能:{}, 请求路径:{}", funcName, requestURI);
	}

	private void fillResponseApiData(Object rs) {
		if (rs instanceof Data) {
			Data apiData = (Data) rs;
			HeadModel meta = apiData.getHead();
			if (meta.getCode() == null) {
				meta.setCode(ReplyCode.Success.getCode());
			}
			if (meta.getMessage() == null) {
				meta.setMessage(ReplyCode.Success.getMessage());
			}
			if (apiData.getHead(ApiConstants.HEAD_SUCCESS) == null) {
				meta.setSuccess(true);
			}
		}
	}

	// 判断登录者权限
	private void checkAuth(Class<?> ctrlerType, Method method) {
		UserAuthc typeAuthc = AnnotationUtils.findAnnotation(ctrlerType, UserAuthc.class);
		UserAuthc methodAuthc = AnnotationUtils.findAnnotation(method, UserAuthc.class);
		boolean authcRequired = (methodAuthc != null && methodAuthc.value())
			|| (typeAuthc != null && typeAuthc.value());

		UserAuthz typeAuthz = AnnotationUtils.findAnnotation(ctrlerType, UserAuthz.class);
		UserAuthz methodAuthz = AnnotationUtils.findAnnotation(method, UserAuthz.class);
		boolean authzRequired = (methodAuthz != null && methodAuthz.value())
			|| (typeAuthz != null && typeAuthz.value());

		Subject subject = SecurityUtils.getSubject();
		if (authcRequired || authzRequired) {
			if (!subject.isAuthenticated() && !subject.isRemembered()) {
				throw new AuthenticationException("");
			}
		}
		if (!authcRequired) {
			Boolean authDefault = Boolean.valueOf(ApplicationContextHolder.getApplicationContext().getEnvironment()
				.getProperty("system.authz.default", "true"));
			if (authDefault && (subject.isAuthenticated() || subject.isRemembered())) {
				// 开启默认权限控制后, 所有登录者都需要控制
				authzRequired = true;
			}
		}
		if (authzRequired) {
			Set<ResourceIdentity> methodResIds = AnnotationUtils.getRepeatableAnnotations(method, ResourceIdentity.class);
			Set<String> permissions = new LinkedHashSet<>();
			if (methodResIds.size() > 0) {
				for (ResourceIdentity identity : methodResIds) {
					String permission = identity.value();
					if (identity.inherited() && StringUtils.isBlank(identity.parent())) {
						Set<ResourceIdentity> typeResIds = AnnotationUtils.getRepeatableAnnotations(ctrlerType, ResourceIdentity.class);
						if (typeResIds.size() > 0) {
							ResourceIdentity parentIdentity = typeResIds.iterator().next();
							permission = parentIdentity.value() + ResourceIdentity.SEPARATOR + permission;
						}else{
							continue;// 需要但未配置父资源时, 忽略
						}
					}
					permissions.add(permission);
				}
			} else {
				Set<ResourceIdentity> typeResIds = AnnotationUtils.getRepeatableAnnotations(ctrlerType, ResourceIdentity.class);
				if (typeResIds.size() > 0) {
					for (ResourceIdentity identity : typeResIds) {
						String permission = identity.value();
						permissions.add(permission);
					}
				}
			}

			// tryFetchMappingPermissions(ctrlerType, method, permissions);
			//RequestMapping methodMapping = AnnotationUtils.findAnnotation(method, RequestMapping.class);

			if (permissions.size() > 0) {
				for (String permission : permissions) {
					logger.info("before executing, check permission : {}", permission);
					subject.checkPermission(permission);// 调用shiro校验权限方法
				}
			}
		}
	}

	private void tryFetchMappingPermissions(Class<?> ctrlerType, Method method, Set<String> permissions) {
		if (permissions.size() == 0) {
			RequestMapping typeMapping = AnnotationUtils.findAnnotation(ctrlerType, RequestMapping.class);
			RequestMapping methodMapping = AnnotationUtils.findAnnotation(method, RequestMapping.class);
			StringBuilder permission = new StringBuilder();
			if (typeMapping != null && typeMapping.value().length > 0) {
				permission.append(typeMapping.value()[0]
					.replaceFirst("^/+", "")
					.replaceFirst("/+$", "")
					.replaceAll("/+", ":"));
			}
			if (methodMapping != null && methodMapping.value().length > 0) {
				if (permission.length() > 0) {
					permission.append(":");
				}
				permission.append(typeMapping.value()[0]
					.replaceFirst("^/+", "")
					.replaceFirst("/+$", "")
					.replaceAll("/+", ":"));
			}
			permissions.add(permission.toString());
		}
	}

}
