package io.microvibe.booster.core.base.shiro.session;

import io.microvibe.booster.commons.utils.HttpWebUtils;
import io.microvibe.booster.core.base.web.utils.HttpSpy;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionFactory;
import org.apache.shiro.web.session.mgt.WebSessionContext;

import javax.servlet.http.HttpServletRequest;

public class OnlineSessionFactory implements SessionFactory {

	@Override
	public Session createSession(SessionContext initData) {
		OnlineSession session = new OnlineSession();
		if (initData != null && initData instanceof WebSessionContext) {
			WebSessionContext sessionContext = (WebSessionContext) initData;
			HttpServletRequest request = (HttpServletRequest) sessionContext.getServletRequest();
			if (request != null) {
				session.setHost(HttpWebUtils.getIpAddr(request));
				session.setUserAgent(request.getHeader("User-Agent"));
				session.setUserAgentType(HttpSpy.getUserAgentType(session.getUserAgent()));
				session.setSystemHost(request.getLocalAddr() + ":" + request.getLocalPort());
			}
		}
		return session;
	}

//    public Session createSession(UserOnline userOnline) {
//        return userOnline.getSession();
//    }
}
