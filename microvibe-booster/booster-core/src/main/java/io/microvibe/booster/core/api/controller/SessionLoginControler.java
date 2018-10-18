package io.microvibe.booster.core.api.controller;

import io.microvibe.booster.core.api.model.JsonData;
import io.microvibe.booster.core.api.model.RequestData;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class SessionLoginControler extends AbstractApiServiceController {

	static final Logger logger = LoggerFactory.getLogger(SessionLoginControler.class);
	@Autowired
	ApplicationContext context;

	@ResponseBody
	@RequestMapping({"/openapi/userSessionLogin"})
	public void login(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String username = WebUtils.getCleanParam(req, FormAuthenticationFilter.DEFAULT_USERNAME_PARAM);
		String password = WebUtils.getCleanParam(req, FormAuthenticationFilter.DEFAULT_PASSWORD_PARAM);
		Boolean rememberMe = WebUtils.isTrue(req, FormAuthenticationFilter.DEFAULT_REMEMBER_ME_PARAM);

		RequestData data = JsonData.buildByTxnCode("sessionLogin");
		data.setBody(FormAuthenticationFilter.DEFAULT_USERNAME_PARAM, username);
		data.setBody(FormAuthenticationFilter.DEFAULT_PASSWORD_PARAM, password);
		data.setBody(FormAuthenticationFilter.DEFAULT_REMEMBER_ME_PARAM, rememberMe);
		doExec(data, req, resp);
	}

	/*
	 * http://localhost:8080/openapi/userSessionLogin?username=13999999999&password=1234567
	 */
}
