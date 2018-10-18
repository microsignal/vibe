package io.microvibe.booster.core.base.shiro.filter;

import io.microvibe.booster.core.api.ReplyCode;
import io.microvibe.booster.core.api.tools.DataKit;
import io.microvibe.booster.core.base.web.captcha.JCaptcha;
import io.microvibe.booster.core.base.web.utils.HttpSpy;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.http.HttpStatus;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 验证码过滤器
 */
public class JCaptchaValidateFilter extends AccessControlFilter {

	private boolean jcaptchaEbabled = true;

	private String jcaptchaParam = "captcha";
	private String jcaptchaKeyParam = "captchaKey";

	private String jcapatchaErrorUrl;

	/**
	 * 是否开启jcaptcha
	 *
	 * @param jcaptchaEbabled
	 */
	public void setJcaptchaEbabled(boolean jcaptchaEbabled) {
		this.jcaptchaEbabled = jcaptchaEbabled;
	}

	/**
	 * 前台传入的验证码
	 *
	 * @param jcaptchaParam
	 */
	public void setJcaptchaParam(String jcaptchaParam) {
		this.jcaptchaParam = jcaptchaParam;
	}

	public void setJcaptchaKeyParam(String jcaptchaKeyParam) {
		this.jcaptchaKeyParam = jcaptchaKeyParam;
	}

	public String getJcapatchaErrorUrl() {
		return jcapatchaErrorUrl;
	}

	public void setJcapatchaErrorUrl(String jcapatchaErrorUrl) {
		this.jcapatchaErrorUrl = jcapatchaErrorUrl;
	}

	@Override
	public boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
		request.setAttribute("jcaptchaEbabled", jcaptchaEbabled);
		return super.onPreHandle(request, response, mappedValue);
	}

	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue)
		throws Exception {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		// 验证码禁用 或不是表单提交 允许访问
		if (jcaptchaEbabled == false || !"post".equals(httpServletRequest.getMethod().toLowerCase())) {
			return true;
		}
		String captcha = httpServletRequest.getParameter(jcaptchaParam);
		String captchaKey = httpServletRequest.getParameter(jcaptchaKeyParam);
		if (StringUtils.isNotBlank(captchaKey)) {
			return JCaptcha.validateForId(captchaKey, captcha);
		} else {
			return JCaptcha.validateResponse(httpServletRequest, captcha);
		}
	}

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		redirectToLogin(request, response);
		return false;
	}

	@Override
	protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
		if (HttpSpy.isAjaxRequest(WebUtils.toHttp(request))) {
			String content = DataKit.buildResponse(ReplyCode.RequestCaptchaInvalid).toString();
			HttpSpy.sendResponse(WebUtils.toHttp(response), HttpStatus.UNAUTHORIZED.value(), content);
		} else {
			WebUtils.issueRedirect(request, response, getJcapatchaErrorUrl());
		}
	}

}
