package io.microvibe.booster.core.base.shiro.session;

import io.microvibe.booster.core.base.web.security.WebSessionConfiguration;
import io.microvibe.booster.core.base.web.utils.HttpSpy;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

public class OnlineWebSessionManager extends DefaultWebSessionManager implements WebSessionConfiguration {

	public static final String TOKEN_HEADER = "Access-Token";
	static final Logger logger = LoggerFactory.getLogger(OnlineWebSessionManager.class);

	@Override
	protected Serializable getSessionId(ServletRequest req, ServletResponse resp) {
		// try token
		Serializable sid = ShiroSidPos.pop();
		if (sid != null) {
			return sid;
		}
		// try token header
		sid = HttpSpy.getSessionIdByAccessToken(WebUtils.toHttp(req));
		if (sid != null) {
			return sid;
		}
		// default
		return super.getSessionId(req, resp);
	}

	@Override
	protected void onStart(Session session, SessionContext context) {
		super.onStart(session, context);

		if (!WebUtils.isHttp(context)) {
			return;
		}
		// token header
		HttpServletResponse response = WebUtils.getHttpResponse(context);
		HttpSpy.buildAccessTokenHeader(response, session.getId());
	}

}
