package io.microvibe.booster.core.api.strategy.implementor;

import io.microvibe.booster.core.api.strategy.SessionAuthcValidator;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Component;

@Component
public class SessionAuthcValidatorImpl implements SessionAuthcValidator {

	@Override
	public boolean validate() {
		// 判断shiro会话是否认证通过
		Subject subject = SecurityUtils.getSubject();
//        return subject.isAuthenticated() || subject.isRemembered();
		return subject.isAuthenticated();
	}

}
