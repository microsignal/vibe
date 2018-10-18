package io.microvibe.booster.core.base.web.filter;

import io.microvibe.booster.core.api.tools.DataKit;
import io.microvibe.booster.core.base.shiro.SessionUtils;
import io.microvibe.booster.core.base.shiro.authc.AuthcSessionPacket;
import io.microvibe.booster.core.base.web.utils.HttpSpy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.AuthorizationException;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
public class RestfulApiFilter extends OncePerRequestFilter {

	public static final String MDC_USER = "user";
	private boolean forceRestful = false;

	@SuppressWarnings("unchecked")
	private <T extends Throwable> T getCauseOf(Throwable e, Class<T> klass) {
		for (Throwable cause = e.getCause(); cause != null; cause = cause.getCause()) {
			if (klass.isAssignableFrom(cause.getClass())) {
				return (T) cause;
			}
		}
		return null;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {

		try {
			AuthcSessionPacket principal = SessionUtils.getPrincipalQuietly();
			if(principal != null) {
				MDC.put(MDC_USER, principal.toString());
			}
			// 文件上传请求不作包装
			if (!ServletFileUpload.isMultipartContent(request)) {
				request = HttpServletRequestCacheWrapper.wrap(request);
			}
			filterChain.doFilter(request, response);
		} catch (Throwable e) {
			Throwable oe = e;
			while (e instanceof ServletException) {
				Throwable cause = ((ServletException) e).getCause();
				if (cause != null) {
					e = cause;
				} else {
					break;
				}
			}
			try {
				if (isForceRestful() || HttpSpy.isAjaxRequest(request)) {
					PrintWriter writer = response.getWriter();

					if (response.getCharacterEncoding() == null) {
						response.setCharacterEncoding("UTF-8");
					}
					if (response.getContentType() == null) {
						response.setContentType("text/html; charset=" + response.getCharacterEncoding());
					}
					Throwable cause;
					if ((cause = getCauseOf(e, AuthenticationException.class)) != null) {
						response.setStatus(HttpStatus.UNAUTHORIZED.value());
						// response.setStatus(HttpStatus.FORBIDDEN.value());
						// response.setStatus(HttpStatus.OK.value());
						writer.println(DataKit.buildResponse(cause).toString());
						writer.flush();
						writer.close();
					} else if ((cause = getCauseOf(e, AuthorizationException.class)) != null) {
						response.setStatus(HttpStatus.UNAUTHORIZED.value());
						writer.println(DataKit.buildResponse(cause).toString());
						writer.flush();
						writer.close();
					} else if ((cause = getCauseOf(e, ShiroException.class)) != null) {
						response.setStatus(HttpStatus.UNAUTHORIZED.value());
						writer.println(DataKit.buildResponse(cause).toString());
						writer.flush();
						writer.close();
					} else {
						if (HttpStatus.valueOf(response.getStatus()).is2xxSuccessful()) {
							response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
						}
						writer.println(DataKit.buildResponse(e).toString());
						writer.flush();
						writer.close();
					}
				} else {
					throwServletException(oe);
				}
			} catch (IllegalStateException ex) {// 已打开过writer
				throwServletException(oe);
			}
		}finally {
			MDC.remove(MDC_USER);
		}
	}

	private void throwServletException(Throwable e) throws ServletException, IOException {
		log.error(e.getMessage(), e);
		if (e instanceof ServletException) {
			throw (ServletException) e;
		} else if (e instanceof IOException) {
			throw (IOException) e;
		} else if (e instanceof RuntimeException) {
			throw (RuntimeException) e;
		} else {
			throw new ServletException(e);
		}
	}

	public boolean isForceRestful() {
		return forceRestful;
	}

	public void setForceRestful(boolean forceRestful) {
		this.forceRestful = forceRestful;
	}
}
