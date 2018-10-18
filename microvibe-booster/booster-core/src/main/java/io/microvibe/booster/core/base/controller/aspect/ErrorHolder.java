package io.microvibe.booster.core.base.controller.aspect;

import io.microvibe.booster.commons.spring.ApplicationContextHolder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Map;

/**
 * @author Qt
 * @since Jun 30, 2018
 */
@Slf4j
@Getter
public class ErrorHolder {
	private Throwable cause;
	private String exception;
	private String message;
	private String trace;
	private Date timestamp;
	private Integer status;
	private String path;

	public ErrorHolder(Throwable throwable) {
		initByErrorAttributes();
		initByThrowable(throwable);
	}

	public ErrorHolder(HttpServletRequest request) {
		initByErrorAttributes(request);
	}

	public ErrorHolder(HttpServletRequest request, ErrorAttributes errorAttributes) {
		initByErrorAttributes(request, errorAttributes);
	}

	private void initByThrowable(Throwable throwable) {
		cause = throwable;
		exception = throwable.getClass().getName();
		message = convertMessage(throwable);
		if (trace == null) {
			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter);
			throwable.printStackTrace(printWriter);
			trace = stringWriter.toString();
		}
	}

	private void initByErrorAttributes() {
		if (ApplicationContextHolder.hasApplicationContext()) {
			try {
				HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
					.getRequest();
				initByErrorAttributes(request);
			} catch (Exception e) {
				log.debug(e.getMessage(), e);
			}
		}
	}

	private void initByErrorAttributes(HttpServletRequest request) {
		try {
			ErrorAttributes errorAttributes = ApplicationContextHolder.getBean(ErrorAttributes.class);
			initByErrorAttributes(request, errorAttributes);
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
	}

	private void initByErrorAttributes(HttpServletRequest request, ErrorAttributes errorAttributes) {
		try {
			RequestAttributes requestAttributes = new ServletRequestAttributes(request);
			Map<String, Object> model = errorAttributes.getErrorAttributes(requestAttributes, true);
			Object message = model.get("message");
			if (message instanceof String) {
				this.message = (String) message;
			}
			Object exception = model.get("exception");
			if (exception instanceof String) {
				this.exception = (String) exception;
			}
			Object trace = model.get("trace");
			if (trace instanceof String) {
				this.trace = (String) trace;
			}
			Object path = model.get("path");
			if (path instanceof String) {
				this.path = (String) path;
			}
			Object status = model.get("status");
			if (status instanceof Integer) {
				this.status = (Integer) status;
			}
			Object timestamp = model.get("timestamp");
			if (timestamp instanceof Date) {
				this.timestamp = (Date) timestamp;
			}
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
	}

	private String convertMessage(Throwable e) {
		String errorMessage = e.getMessage();
		//验证失败
		if (e instanceof AuthorizationException) {
			errorMessage = "您没有操作权限，请联系权限配置管理员！";
			try {
				String requestURI = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getRequestURI();
				errorMessage += "[" + requestURI + "]";
			} catch (Exception e1) {
			}
		}
		if(StringUtils.isBlank(errorMessage)){
			return exception;
		}
		return errorMessage;
	}

}
