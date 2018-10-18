package io.microvibe.booster.core.base.shiro.filter;

import io.microvibe.booster.core.base.web.utils.HttpSpy;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.LogoutFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class CustomerLogoutFilter extends LogoutFilter {

	public CustomerLogoutFilter() {
	}

	public CustomerLogoutFilter(String redirectUrl) {
		super.setRedirectUrl(redirectUrl);
	}

	@Override
	protected String getRedirectUrl(ServletRequest request, ServletResponse response, Subject subject) {
		String redirectUrl = getRedirectUrl();
		return HttpSpy.appendRequestParameters(redirectUrl, request);
	}

}
