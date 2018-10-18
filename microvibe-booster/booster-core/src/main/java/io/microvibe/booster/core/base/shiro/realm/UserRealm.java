package io.microvibe.booster.core.base.shiro.realm;

import io.microvibe.booster.core.base.shiro.authc.AuthcSessionPacket;
import io.microvibe.booster.core.base.shiro.authc.IShiroAuthcService;
import io.microvibe.booster.core.base.shiro.authz.AntPathMatcherPermissionResolver;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

@Slf4j
public class UserRealm extends AuthorizingRealm {
	private static final String OR_OPERATOR = " or ";
	private static final String AND_OPERATOR = " and ";
	private static final String NOT_OPERATOR = "not ";

	@Autowired
	@Lazy
	private IShiroAuthcService shiroAuthcService;

	public UserRealm() {
		super();
		// 覆盖默认的`this.permissionResolver = new WildcardPermissionResolver();`实现
		setPermissionResolver(new AntPathMatcherPermissionResolver());
	}

	@Override
	public AuthorizationInfo getAuthorizationInfo(PrincipalCollection principals) {
		return super.getAuthorizationInfo(principals);
	}

	public AuthorizationInfo getCurrentAuthorizationInfo() {
		SimplePrincipalCollection principalCollection = new SimplePrincipalCollection(
			SecurityUtils.getSubject().getPrincipal(), getName());
		return super.getAuthorizationInfo(principalCollection);
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		AuthcSessionPacket sessionKey = (AuthcSessionPacket) principals.getPrimaryPrincipal();
		return shiroAuthcService.getAuthorizationInfo(sessionKey);
		/*
		SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
		String username = (String) principals.getPrimaryPrincipal();
		User user = userService.findByUsername(username);
		if (user == null) {
			user = userService.findByOpenId(username);
		}
		authorizationInfo.setRoles(userAuthService.findStringRoles(user));
		authorizationInfo.setStringPermissions(userAuthService.findStringPermissions(user));
		return authorizationInfo;
		*/
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		try {
			AuthcSessionPacket packet = shiroAuthcService.doAuthentication(token);
			return new SimpleAuthenticationInfo(packet, token.getCredentials(), getName());
			/*
			SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(token.getPrincipal(), token.getCredentials(),
				this.getName());
			User user = null;
			String password = "";
			if (token instanceof UsernamePasswordToken) {
				UsernamePasswordToken upToken = (UsernamePasswordToken) token;
				String username = upToken.getUsername().trim();
				if (upToken.getPassword() != null) {
					password = new String(upToken.getPassword());
				}
				user = userService.login(username, password);
				info = new SimpleAuthenticationInfo(user.getUsername(), password.toCharArray(), getName());
			} else if (token instanceof ShiroWechatToken) {
				ShiroWechatToken shiroWechatToken = (ShiroWechatToken) token;
				user = userService.findByOpenId(shiroWechatToken.getOpenid());
				info = new SimpleAuthenticationInfo(user.getUsername(), shiroWechatToken.getCredentials(), getName());
			}
			return info;*/
		} catch (AuthenticationException e) {
			throw e;
		} catch (Exception e) {
			log.error("login error", e);
			throw new AuthenticationException(e.getMessage(), e);
		}
	}

	/**
	 * 支持or and not 关键词  不支持and or混用
	 *
	 * @param principals
	 * @param permission
	 * @return
	 */
	@Override
	public boolean isPermitted(PrincipalCollection principals, String permission) {
		if (permission.contains(OR_OPERATOR)) {
			String[] permissions = permission.split(OR_OPERATOR);
			for (String orPermission : permissions) {
				if (isPermittedWithNotOperator(principals, orPermission)) {
					return true;
				}
			}
			return false;
		} else if (permission.contains(AND_OPERATOR)) {
			String[] permissions = permission.split(AND_OPERATOR);
			for (String orPermission : permissions) {
				if (!isPermittedWithNotOperator(principals, orPermission)) {
					return false;
				}
			}
			return true;
		} else {
			return isPermittedWithNotOperator(principals, permission);
		}
	}

	private boolean isPermittedWithNotOperator(PrincipalCollection principals, String permission) {
		if (permission.startsWith(NOT_OPERATOR)) {
			return !super.isPermitted(principals, permission.substring(NOT_OPERATOR.length()));
		} else {
			return super.isPermitted(principals, permission);
		}
	}

	@Override
	public boolean supports(AuthenticationToken token) {
		return token instanceof UsernamePasswordToken;
	}
}
