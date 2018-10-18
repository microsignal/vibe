package io.microvibe.booster.core.api.filter;

import com.alibaba.fastjson.JSONObject;
import io.microvibe.booster.core.accessor.CacheAccessor;
import io.microvibe.booster.core.api.ApiConstants;
import io.microvibe.booster.core.api.model.RequestData;
import io.microvibe.booster.core.api.support.RequestApiDataParser;
import io.microvibe.booster.core.base.shiro.session.ShiroSidPos;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Deprecated
public class RestApiFilter extends OncePerRequestFilter {

	private String[] xmlPrefixes;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		System.out.println(session);

		String requestURI = request.getRequestURI();
		String contextPath = request.getContextPath();
		if (!contextPath.equals("/")) {
			requestURI = requestURI.substring(contextPath.length());
		}
		boolean isXml = false;
		for (String prefix : xmlPrefixes) {
			isXml |= requestURI.startsWith(prefix);
		}

		String accessToken = null;
		// try get data param
		String data = WebUtils.getCleanParam(request, ApiConstants.HTTP_PARAM_NAME);
		if (data != null) {
			RequestApiDataParser parser;
			if (isXml) {
				parser = RequestApiDataParser.parseXml(data);
			} else {
				parser = RequestApiDataParser.parseJson(data);
			}
			if (!parser.hasError()) {
				RequestData requestApiData = parser.getRequestApiData();
				accessToken = requestApiData.getHead(ApiConstants.ACCESS_TOKEN, String.class);
			}
		}
		// try get accessToken param
		if (accessToken == null) {
			accessToken = WebUtils.getCleanParam(request, ApiConstants.ACCESS_TOKEN);
		}

		if (accessToken != null) {
			ValueWrapper valueWrapper = CacheAccessor.getApiAccessTokenCache().get(accessToken);// tokenBinding
			if (valueWrapper != null) {
				JSONObject tokenBinding = JSONObject.parseObject(valueWrapper.get().toString());
				String shiroSid = tokenBinding.getString(ApiConstants.SHIRO_SESSION_ID);
				if (shiroSid != null) {
					ShiroSidPos.set(shiroSid);
				}
			}
		}
		try {
			filterChain.doFilter(request, response);
		} finally {
			RequestApiDataParser.clean();
		}
	}

	public void setXmlPrefixes(String... xmlPrefixes) {
		this.xmlPrefixes = xmlPrefixes;
	}

}
