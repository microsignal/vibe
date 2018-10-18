package io.microvibe.booster.core.api.txn;

import io.microvibe.booster.core.api.ApiException;
import io.microvibe.booster.core.api.ApiService;
import io.microvibe.booster.core.api.ReplyCode;
import io.microvibe.booster.core.api.annotation.ApiName;
import io.microvibe.booster.core.api.model.BodyModel;
import io.microvibe.booster.core.api.model.RequestData;
import io.microvibe.booster.core.api.model.ResponseData;
import io.microvibe.booster.core.base.web.utils.HttpSpy;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 用户会话登录
 *
 * @author Qt
 * @since Oct 16, 2017
 */
@Component
@ApiName({"sessionLogin"})
public class Txn00003ApiService extends BaseApiService implements ApiService {

	private static final Logger logger = LoggerFactory.getLogger(Txn00003ApiService.class);

	@Override
	public ResponseData execute(RequestData request) throws ApiException {
		BodyModel body = request.getBody();
		ResponseData response = request.buildResponse();

		Subject subject = SecurityUtils.getSubject();

		if (subject.isAuthenticated()) {
			logger.warn("当前会话已认证通过");
			// subject.logout();
		}

		// 认证方式
		String authChannel = StringUtils.trimToNull(body.getString("authChannel"));
		if (authChannel == null || authChannel.equals("username")) {
			// 用户密码认证
			String username = body.getString("username");
			String password = body.getString("password");
			Boolean rememberMe = Boolean.TRUE.equals(body.getBoolean("rememberMe"));
			AuthenticationToken token = new UsernamePasswordToken(username, password.toCharArray(), rememberMe);
			subject.login(token);
		} else {
			ReplyCode.TxnAuthChannelUnsupported.fail();
		}

		// FIXME 后面加入其他认证方式

		String sessionToken = HttpSpy.buildSessionToken();
		response.setBody("token", sessionToken);
		return response;
	}

}
