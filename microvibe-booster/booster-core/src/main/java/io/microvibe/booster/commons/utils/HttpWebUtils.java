package io.microvibe.booster.commons.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpWebUtils {

	public static HttpServletRequest toHttp(ServletRequest request) {
		return (HttpServletRequest) request;
	}

	public static HttpServletResponse toHttp(ServletResponse response) {
		return (HttpServletResponse) response;
	}

	public static String getRequestURI(HttpServletRequest request) {
		if (request == null) {
			return "unknown";
		}
		return request.getRequestURI();
	}

	public static String getRequestMethod(HttpServletRequest request) {
		if (request == null) {
			return "unknown";
		}
		return request.getMethod();
	}

	public static String getIpAddr(HttpServletRequest request) {
		if (request == null) {
			return "unknown";
		}
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Forwarded-For");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Real-IP");
		}

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}

		if (ip.length() > 0) {
			String[] ips = ip.split(",");
			ip = ips[0].trim();
		}

		return ip;
	}
}
