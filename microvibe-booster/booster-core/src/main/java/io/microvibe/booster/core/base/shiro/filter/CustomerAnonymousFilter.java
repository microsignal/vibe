package io.microvibe.booster.core.base.shiro.filter;

import io.microvibe.booster.core.base.web.utils.HttpSpy;
import org.apache.shiro.web.filter.authc.AnonymousFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class CustomerAnonymousFilter extends AnonymousFilter {

	@Override
	protected boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) {
		HttpServletRequest httpRequest = WebUtils.toHttp(request);
		String tokenHeader = httpRequest.getHeader(HttpSpy.TOKEN_HEADER);
		if (tokenHeader != null) {
			HttpSpy.buildAccessTokenHeader(WebUtils.toHttp(response), WebUtils.toHttp(request));
		}
		return super.onPreHandle(request, response, mappedValue);
	}

	@Override
	protected void postHandle(ServletRequest request, ServletResponse response) throws Exception {
		super.postHandle(request, response);
	}
}
