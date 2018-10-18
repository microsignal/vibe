package io.microvibe.booster.system.toolkit;

import io.microvibe.booster.commons.spring.ApplicationContextHolder;
import io.microvibe.booster.core.accessor.CacheAccessor;
import io.microvibe.booster.core.api.ApiException;
import io.microvibe.booster.core.api.ReplyCode;
import io.microvibe.booster.core.api.model.BodyModel;
import io.microvibe.booster.core.api.tools.Assertion;
import io.microvibe.booster.core.base.shiro.SessionUtils;
import io.microvibe.booster.core.base.shiro.realm.UserRealm;
import io.microvibe.booster.core.base.utils.RequestContextUtils;
import io.microvibe.booster.core.base.web.captcha.JCaptcha;
import io.microvibe.booster.core.env.ShiroEnv;
import io.microvibe.booster.system.auth.SystemAuthorizationInfo;
import io.microvibe.booster.system.entity.SysUser;
import io.microvibe.booster.system.model.CurrentUser;
import io.microvibe.booster.system.model.GroupOrgHolder;
import io.microvibe.booster.system.service.SysUserService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * @author Qt
 * @since Jun 28, 2018
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Users {
	private static final String CACHE_NAME = "UsersCache";

	public static Cache getCache() {
		// EhCacheCacheManager cacheManager = ApplicationContextHolder.getBean(EhCacheCacheManager.class);
		CacheManager cacheManager = ApplicationContextHolder.getBean(EhCacheCacheManager.class);
		Cache cache = cacheManager.getCache(CACHE_NAME);
		return cache;
	}

	public static void clearCache() {
		getCache().clear();
	}

	public static SysUser getCache(Long userId) {
		return getCache().get(userId, SysUser.class);
	}

	public static void addCache(SysUser user) {
		getCache().put(user.getId(), user);
	}

	public static void clearCache(Long userId) {
		getCache().evict(userId);
	}

	public static void clearCurrentCache() {
		Long currentUserId = getCurrentUserId();
		if (currentUserId != null) {
			getCache().evict(currentUserId);
		}
	}

	/**
	 * @return 当前会话是否认证
	 */
	public static boolean isOnline() {
		try {
			return SecurityUtils.getSubject().isAuthenticated();
		} catch (ShiroException e) {
			return false;
		}
	}

	public static void login(BodyModel data) {
		ApplicationContext context = ApplicationContextHolder.getApplicationContext();

		// 验证用户密码参数
		ShiroEnv shiroEnv = context.getBean(ShiroEnv.class);
		String username = org.apache.commons.lang3.StringUtils.trimToNull(data.getString(shiroEnv.getUsernameParam()));
		String password = org.apache.commons.lang3.StringUtils.trimToNull(data.getString(shiroEnv.getPasswordParam()));

		if (username == null) {
			throw new ApiException("用户名为空");
		}

		// 验证码
		Environment env = context.getEnvironment();
		String captchaEnabled = env.getProperty("system.api.login.captcha", "false");
		if ("true".equalsIgnoreCase(captchaEnabled)) {
			String captcha = org.apache.commons.lang3.StringUtils.trimToNull(data.getString("captcha"));
			String captchaBindKey = org.apache.commons.lang3.StringUtils.trimToNull(data.getString("captchaKey"));
			if (captchaBindKey == null) {
				captchaBindKey = username;
			}
			if (!JCaptcha.validateForId(captchaBindKey, captcha)) {
				boolean invalid = true;
				Cache captchaCache = CacheAccessor.getCaptchaCache();
				Cache.ValueWrapper valueWrapper = captchaCache.get(captchaBindKey);
				if (valueWrapper != null) {
					Object val = valueWrapper.get();
					if (val != null && val.toString().equalsIgnoreCase(captcha)) {
						invalid = false;
					}
					captchaCache.evict(captchaBindKey);
				}
				if (invalid) { // 验证码错误
					throw new ApiException(ReplyCode.RequestCaptchaInvalid);
				}
			}
		}

		// 注销已认证会话
		Subject subject = SecurityUtils.getSubject();
		if (subject.isAuthenticated()) {
			log.warn("当前会话已认证通过");
			subject.logout();
		}

		try {
			AuthenticationToken token = new UsernamePasswordToken(username, password.toCharArray(), false);
			subject.login(token);
		} catch (AuthenticationException e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}

	/**
	 * @return 当前用户ID
	 */
	public static Long getCurrentUserId() {
		Long currentUserId = null;
		if (isOnline()) {
			try {
				currentUserId = (Long) SessionUtils.getCurrentUserId();
			} catch (ShiroException e) {
				log.error(e.getMessage(), e);
			}
		}
		return currentUserId;
	}

	/**
	 * @return 当前用户ID
	 */
	public static Long getRequiredCurrentUserId() {
		Long currentUserId = getCurrentUserId();
		Assertion.isNotNull(currentUserId);
		return currentUserId;
	}

	private static SysUser currentUser() {
		Long currentUserId = getCurrentUserId();
		if (currentUserId == null) {
			return null;
		}
		SysUser user = getCache(currentUserId);
		if (user == null) {
			SysUserService userService = ApplicationContextHolder.getBean(SysUserService.class);
			user = userService.getById(currentUserId);
			addCache(user);
		}
		return user;
	}

	/**
	 * @return 当前登录的用户
	 */
	public static CurrentUser<Long> getCurrentUser() {
		return currentUser();
	}

//	/**
//	 * @return 当前登录的集团及公司
//	 */
//	public static GroupOrgHolder getCurrentGroupOrg() {
//		return currentUser();
//	}

	/**
	 * @return 当前登录的用户
	 */
	public static CurrentUser<Long> getRequiredCurrentUser() {
		CurrentUser user = getCurrentUser();
		Assertion.isNotNull(user);
		return user;
	}

//	/**
//	 * @return 当前登录的集团及公司
//	 */
//	public static GroupOrgHolder getRequiredCurrentGroupOrg() {
//		GroupOrgHolder holder = getCurrentGroupOrg();
//		Assertion.isNotNull(holder);
//		return holder;
//	}

	/**
	 * 绑定当前用户对象到RequestScope,键名为 {@linkplain CurrentUser#BIND_ID}
	 */
	public static void bindRequestAttribute() {
		HttpServletRequest request = RequestContextUtils.currentHttpRequest();
		Object currentUser = request.getAttribute(CurrentUser.BIND_ID);
		if (currentUser == null) {
			request.setAttribute(CurrentUser.BIND_ID, getCurrentUser());
		}
	}
	/*

	*//**
	 * 获取公司权限
	 *
	 * @return
	 *//*
	public static Set<String> getCurrentOrgPermissions() {
		UserRealm realm = ApplicationContextHolder.getBean(UserRealm.class);
		SystemAuthorizationInfo authorizationInfo = (SystemAuthorizationInfo) realm.getCurrentAuthorizationInfo();
		return authorizationInfo.getOrgPermissions();
	}

	*//**
	 * 是否有公司权限
	 *
	 * @param orgId
	 * @return
	 *//*
	public static boolean hasOrgPermission(String orgId) {
		UserRealm realm = ApplicationContextHolder.getBean(UserRealm.class);
		SystemAuthorizationInfo authorizationInfo = (SystemAuthorizationInfo) realm.getCurrentAuthorizationInfo();
		return authorizationInfo.hasOrgPermission(orgId);
	}

	*//**
	 * 获取集团权限
	 *
	 * @return
	 *//*
	public static Set<String> getCurrentGroupPermissions() {
		UserRealm realm = ApplicationContextHolder.getBean(UserRealm.class);
		SystemAuthorizationInfo authorizationInfo = (SystemAuthorizationInfo) realm.getCurrentAuthorizationInfo();
		return authorizationInfo.getGroupPermissions();
	}

	*//**
	 * 是否有集团权限
	 *
	 * @param groupId
	 * @return
	 *//*
	public static boolean hasGroupPermission(String groupId) {
		UserRealm realm = ApplicationContextHolder.getBean(UserRealm.class);
		SystemAuthorizationInfo authorizationInfo = (SystemAuthorizationInfo) realm.getCurrentAuthorizationInfo();
		return authorizationInfo.hasGroupPermission(groupId);
	}


	*//**
	 * 获取化验单权限
	 *
	 * @return
	 *//*
	public static Set<String> getCurrentAssayPermissions() {
		UserRealm realm = ApplicationContextHolder.getBean(UserRealm.class);
		SystemAuthorizationInfo authorizationInfo = (SystemAuthorizationInfo) realm.getCurrentAuthorizationInfo();
		return authorizationInfo.getAssayPermissions();
	}

	*//**
	 * 是否有化验单权限
	 *
	 * @param assayPermission
	 * @return
	 *//*
	public static boolean hasAssayPermission(String assayPermission) {
		UserRealm realm = ApplicationContextHolder.getBean(UserRealm.class);
		SystemAuthorizationInfo authorizationInfo = (SystemAuthorizationInfo) realm.getCurrentAuthorizationInfo();
		return authorizationInfo.hasAssayPermission(assayPermission);
	}

	*//**
	 * 获取仓库权限
	 *
	 * @return
	 *//*
	public static Set<String> getCurrentWarehousePermissions() {
		// fixme 实现
		throw new UnsupportedOperationException();
	}

	*//**
	 * 是否有手持机仓库权限
	 *
	 * @param warehousePermission
	 * @return
	 *//*
	public static boolean hasWarehousePermission(String warehousePermission) {
		// fixme 实现
		throw new UnsupportedOperationException();
	}
	*/
}
