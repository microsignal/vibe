package io.microvibe.booster.core.base.web.utils;

import io.microvibe.booster.commons.spring.ApplicationContextHolder;
import io.microvibe.booster.core.base.utils.RequestContextUtils;
import io.microvibe.booster.core.base.web.security.JWTModel;
import io.microvibe.booster.core.base.web.security.WebSessionConfiguration;
import org.apache.commons.compress.utils.CharsetNames;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;

public class HttpSpy {

	public static final String X_REQUEST_HEADER = "X-Requested-With";
	public static final String AJAX_HEADER = "Access-Ajax";
	public static final String TOKEN_HEADER = "Access-Token";

	public static boolean isAjaxRequest(HttpServletRequest request) {
		boolean isAjax = false;
		// X-Requested-With: XMLHttpRequest
		String header = request.getHeader(X_REQUEST_HEADER);
		isAjax = header != null;
		if (!isAjax) {
			header = request.getHeader(AJAX_HEADER);
			isAjax = header != null;
		}
		if (!isAjax) {
			if (request.getHeader("Access-Control-Request-Method") != null ||
				"OPTIONS".equalsIgnoreCase(request.getMethod())) {
				// CORS "pre-flight" request
				isAjax = true;
			}
		}
		return isAjax;
	}

	public static Serializable getSessionIdByAccessToken(HttpServletRequest request) {
		Serializable sid = null;
		if (HttpSpy.isAjaxRequest(request)) {
			String tokenHeader = request.getHeader(TOKEN_HEADER);
			if (tokenHeader != null) {
				JWTModel jwtModel = JWTModel.fromString(tokenHeader);
				if (jwtModel != null) {
					sid = jwtModel.getInfo();
				}
			}
		}
		return sid;
	}

	public static String buildAccessTokenHeader(HttpServletResponse response, HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		return buildAccessTokenHeader(response, session);
	}

	public static String buildAccessTokenHeader(HttpServletResponse response, HttpSession session) {
		return buildAccessTokenHeader(response, session != null ? session.getId() : null);
	}

	public static String buildAccessTokenHeader(HttpServletResponse response, Serializable sessionId) {
		String token = response.getHeader(TOKEN_HEADER);
		if (null == token && sessionId != null) {
			token = buildSessionToken(sessionId);
			response.setHeader(TOKEN_HEADER, token);
		}
		return token;
	}

	public static String buildSessionToken() {
		HttpServletRequest request = RequestContextUtils.currentHttpRequest();
		HttpSession session = request.getSession(true);
		return buildSessionToken(session.getId());
	}

	public static String buildSessionToken(Serializable sessionId) {
		ApplicationContext context = ApplicationContextHolder.getApplicationContext();
		JWTModel jwtModel;
		try {
			WebSessionConfiguration webSessionConfiguration = context.getBean(WebSessionConfiguration.class);
			jwtModel = new JWTModel(webSessionConfiguration.getGlobalSessionTimeout(), sessionId);
		} catch (BeansException e) {
			jwtModel = new JWTModel(sessionId);
		}
		return jwtModel.toString();
	}

	public static void sendResponse(HttpServletResponse response, int status, String content) throws IOException {
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("utf-8");
		response.setStatus(status);
		PrintWriter writer = response.getWriter();
		writer.print(content);
		writer.flush();
	}

	public static String getUserAgent(HttpServletRequest request) {
		return request.getHeader("User-Agent");
	}

	public static UserAgentType getUserAgentType(HttpServletRequest request) {
		return getUserAgentType(getUserAgent(request));
	}

	public static UserAgentType getUserAgentType(String userAgent) {
		if (StringUtils.isBlank(userAgent)) {
			return UserAgentType.pc;
		}
		userAgent = userAgent.toLowerCase();
		if (userAgent.contains("micromessenger")) {
			return UserAgentType.wechat;
		}
		if (userAgent.contains("android")) {
			return UserAgentType.android;
		}
		if (userAgent.contains("iphone")) {
			return UserAgentType.iphone;
		}
		return UserAgentType.pc;
	}

	public static String appendRequestParameters(String url, ServletRequest request) {
		Map<String, String[]> parameterMap = request.getParameterMap();
		String requestCharacterEncoding = StringUtils.trimToNull(request.getCharacterEncoding());
		return appendParameters(url, parameterMap, requestCharacterEncoding);
	}

	public static String appendParameters(String url, String parameters) {
		if (StringUtils.isBlank(parameters)) {
			return url;
		}
		if (url.indexOf("?") > 0) {
			return url + "&" + parameters;
		} else {
			return url + "?" + parameters;
		}
	}

	public static String appendParameters(String url, Map<String, String[]> parameterMap) {
		return appendParameters(url, parameterMap, CharsetNames.UTF_8);
	}

	public static String appendParameters(String url, Map<String, String[]> parameterMap, String characterEncoding) {
		try {
			Iterator<Map.Entry<String, String[]>> iter = parameterMap.entrySet().iterator();
			if (!iter.hasNext()) {
				return url;
			}
			StringBuilder redirectUrl = new StringBuilder().append(url);
			String enc = characterEncoding != null &&
				Charset.isSupported(characterEncoding) ? characterEncoding : CharsetNames.UTF_8;
			if (url.indexOf("?") > 0) {
				Map.Entry<String, String[]> entry = iter.next();
				String[] value = entry.getValue();
				if (value.length > 0) {
					redirectUrl.append("&").append(entry.getKey()).append("=").append(URLEncoder.encode(value[0], enc));
				}
				for (int i = 1; i < value.length; i++) {
					redirectUrl.append("&").append(entry.getKey()).append("=").append(URLEncoder.encode(value[i], enc));
				}
			} else {
				Map.Entry<String, String[]> entry = iter.next();
				String[] value = entry.getValue();
				for (int i = 0; i < value.length; i++) {
					redirectUrl.append("&").append(entry.getKey()).append("=").append(URLEncoder.encode(value[i], enc));
				}
			}
			while (iter.hasNext()) {
				Map.Entry<String, String[]> entry = iter.next();
				String[] value = entry.getValue();
				for (int i = 0; i < value.length; i++) {
					redirectUrl.append("&").append(entry.getKey()).append("=").append(URLEncoder.encode(value[i], enc));
				}
			}
			return redirectUrl.toString();
		} catch (UnsupportedEncodingException e) {
			return url;
		}
	}
}
