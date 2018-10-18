package io.microvibe.booster.core.base.utils;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public abstract class RequestContextUtils extends org.springframework.web.servlet.support.RequestContextUtils {

	public static HttpServletRequest currentHttpRequest() {
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
	}
}
