package io.microvibe.booster.core.base.shiro.filter;

import io.microvibe.booster.core.api.ReplyCode;
import io.microvibe.booster.core.api.model.ResponseData;
import io.microvibe.booster.core.api.tools.DataKit;
import io.microvibe.booster.core.base.shiro.SessionUtils;
import io.microvibe.booster.core.base.shiro.authc.AuthcSessionPacket;
import io.microvibe.booster.core.base.shiro.session.OnlineSession;
import io.microvibe.booster.core.base.shiro.session.OnlineStatus;
import io.microvibe.booster.core.base.web.utils.HttpSpy;
import io.microvibe.booster.core.env.ShiroEnv;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.Map;

@Slf4j
public class CustomFormAuthenticationFilter extends FormAuthenticationFilter {

	public static final String RESPONSE_REDIRECT = "response.redirect";
	public static final String RESPONSE_REDIRECT_URL = "response.redirectUrl";
	public static final String RESPONSE_REDIRECT_URL_PARAMETERS = "response.redirectUrl.parameters";
	public static final String RESPONSE_REPLY_CODE = "response.replyCode";
	public static final String RESPONSE_REPLY_MESSAGE = "response.replyMessage";
	public static final String RESPONSE_REPLY_BODY = "response.replyBody";
	@Autowired
	private SessionDAO sessionDAO;
	@Autowired
	private ShiroEnv shiroEnv;

	@Override
	public String getSuccessUrl() {
		return super.getSuccessUrl();
	}

	@Override
	protected boolean isAccessAllowed(ServletRequest request,
		ServletResponse response, Object mappedValue) {
		Subject subject = getSubject(request, response);
		if (subject.isAuthenticated() || subject.isRemembered()) {
			// rememberMe
			if (isLoginRequest(request, response)) {// 登录请求下重置会话
				try {
					subject.logout();
				} catch (UnknownSessionException e) {
					System.err.println("会话登出时异常: " + e.toString());
				}
				return false;
			} else {
				Session session = subject.getSession(false);
				if (session != null) {
					session = sessionDAO.readSession(session.getId());
				}
				if (session != null && session instanceof OnlineSession) {
					OnlineSession onlineSession = (OnlineSession) session;

					// region 被管理员踢出会话
					if (onlineSession.getStatus() == OnlineStatus.FORCE_LOGOUT) {
						onlineSession.stop();
						onlineSession.setStatus(OnlineStatus.OFFLINE);
						sessionDAO.update(onlineSession);
						request.setAttribute(RESPONSE_REPLY_CODE, ReplyCode.TxnSessionKickedOut);
						request.setAttribute(RESPONSE_REDIRECT_URL, shiroEnv.getUserForceLogoutUrl());
						return false;
					}
					// endregion

					// region 填充会话用户信息
					boolean requiredUserInfo = onlineSession.getUserId() == null ||
						onlineSession.getAuthcChannel() == null || onlineSession.getAuthcCode() == null;
					if (requiredUserInfo) {
						AuthcSessionPacket principal = SessionUtils.getPrincipalQuietly();
						if (principal != null) {
							onlineSession.setAuthcChannel(principal.getAuthcChannel());
							onlineSession.setAuthcCode(principal.getAuthcCode());
							onlineSession.setUserId(principal.getUserId());
						}
						sessionDAO.update(onlineSession);
					}
					// endregion

				}
			}
		}
		return super.isAccessAllowed(request, response, mappedValue);
	}

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		// return super.onAccessDenied(request, response);
		if (isLoginRequest(request, response)) {
			if (isLoginSubmission(request, response)) {
				// Login submission detected.  Attempting to execute login.
				return executeLogin(request, response);
			} else {
				//allow them to see the login page ;)
				return true;
			}
		} else {
			// try to save request
			try {
				WebUtils.saveRequest(request);
			} catch (Exception e) {
				// invalid session
				if (log.isDebugEnabled()) {
					log.debug(e.getMessage(), e);
				}
			}
			// redirect to login
			redirectToLogin(request, response);
			return false;
		}
	}

	@Override
	protected boolean onLoginSuccess(AuthenticationToken token,
		Subject subject, ServletRequest request, ServletResponse response)
		throws Exception {
		return super.onLoginSuccess(token, subject, request, response);
	}

	@Override
	protected void setFailureAttribute(ServletRequest request, AuthenticationException ae) {
		request.setAttribute(getFailureKeyAttribute(), ae);
	}

	@Override
	protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request,
		ServletResponse response) {
		return super.onLoginFailure(token, e, request, response);
	}

	@Override
	protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
		// Object failure = request.getAttribute(getFailureKeyAttribute());
		// no authc
		if (HttpSpy.isAjaxRequest(WebUtils.toHttp(request))) {
			ReplyCode replyCode = extractReplyCode(request);
			ResponseData responseData = DataKit.buildResponse(replyCode);
			appendResponseMessage(request, responseData);
			appendResponseBody(request, responseData);
			String content = responseData.toString();
			HttpSpy.sendResponse(WebUtils.toHttp(response), HttpStatus.UNAUTHORIZED.value(), content);
		} else {
			String redirectUrl = extractRedirectUrl(request);
			redirectUrl = appendRedirectParameters(redirectUrl, request);
			redirectUrl = HttpSpy.appendRequestParameters(redirectUrl, request);
			WebUtils.issueRedirect(request, response, redirectUrl);
		}
	}

	/**
	 * 追加响应消息
	 */
	private void appendResponseMessage(ServletRequest request, ResponseData responseData) {
		Object attr = request.getAttribute(RESPONSE_REPLY_MESSAGE);
		if (attr != null && attr instanceof String) {
			responseData.getHead().setMessage((String) attr);
		}
	}

	/**
	 * 追加响应体
	 */
	private void appendResponseBody(ServletRequest request, ResponseData responseData) {
		Object attr = request.getAttribute(RESPONSE_REPLY_BODY);
		if (attr != null) {
			if (attr instanceof Map) {
				((Map) attr).forEach((k, v) -> {
					if (k instanceof String) {
						responseData.setBody((String) k, v);
					}
				});
			} else if (attr instanceof String) {
				responseData.setBodyAsString((String) attr);
			}
		}
	}

	/**
	 * 追加重定向参数
	 */
	private String appendRedirectParameters(String redirectUrl, ServletRequest request) {
		Object attr = request.getAttribute(RESPONSE_REDIRECT_URL_PARAMETERS);
		if (attr != null && attr instanceof String) {
			HttpSpy.appendParameters(redirectUrl, (String) attr);
		}
		return redirectUrl;
	}

	/**
	 * 提取响应码
	 */
	private ReplyCode extractReplyCode(ServletRequest request) {
		ReplyCode replyCode;
		Object attr = request.getAttribute(RESPONSE_REPLY_CODE);
		if (attr != null && attr instanceof ReplyCode) {
			replyCode = (ReplyCode) attr;
		} else {
			replyCode = ReplyCode.TxnSessionUnauthenticated;
		}
		return replyCode;
	}

	/**
	 * 提取重定向地址
	 */
	private String extractRedirectUrl(ServletRequest request) {
		String redirectUrl;
		Object attr = request.getAttribute(RESPONSE_REDIRECT_URL);
		if (attr != null && attr instanceof String) {
			redirectUrl = (String) attr;
		} else {
			redirectUrl = getLoginUrl();
		}
		return redirectUrl;
	}
}
