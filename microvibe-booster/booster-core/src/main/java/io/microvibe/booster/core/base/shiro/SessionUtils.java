package io.microvibe.booster.core.base.shiro;

import io.microvibe.booster.commons.spring.ApplicationContextHolder;
import io.microvibe.booster.commons.utils.BeanUtils;
import io.microvibe.booster.core.api.ReplyCode;
import io.microvibe.booster.core.base.shiro.authc.AuthcChannel;
import io.microvibe.booster.core.base.shiro.authc.AuthcSessionPacket;
import io.microvibe.booster.core.env.SystemEnv;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import java.io.Serializable;

public class SessionUtils {
	/**
	 * 获取shiro的session
	 *
	 * @return
	 */
	public static Session getSession() {
		return SecurityUtils.getSubject().getSession();
	}

	/**
	 * 获取shiro Subject
	 *
	 * @return
	 */
	public static Subject getSubject() {
		return SecurityUtils.getSubject();
	}
	/**
	 * 退出登录
	 */
	public static void logout() {
		SecurityUtils.getSubject().logout();
	}

	/**
	 * @return 当前会话是否认证
	 */
	public static boolean isAuthenticated() {
		Subject subject = SecurityUtils.getSubject();
		return subject.isAuthenticated();
	}

	/**
	 * 拿到当前登录的会话身份
	 */
	public static AuthcSessionPacket getPrincipal() throws ShiroException {
		Object principal = SecurityUtils.getSubject().getPrincipal();
		if (principal == null) {
			throw new AuthenticationException(ReplyCode.TxnSessionUnauthenticated.getMessage());
		}
		if(!(principal instanceof AuthcSessionPacket)){
			if (ApplicationContextHolder.getBean(SystemEnv.class).isDevelopMode()) {
				if (principal.getClass().getName().equalsIgnoreCase(AuthcSessionPacket.class.getName())) {
					// 类路径相同,类加载器不同
					AuthcSessionPacket packet = new AuthcSessionPacket();
					BeanUtils.copyProperties(principal, packet);
					principal = packet;
				}
			}else{
				throw new AuthenticationException(ReplyCode.TxnSessionUnauthenticated.getMessage());
			}
		}
		return (AuthcSessionPacket) principal;
	}

	public static AuthcSessionPacket getPrincipalQuietly() {
		try {
			return getPrincipal();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * @return 当前登录会话用户ID
	 */
	public static Serializable getCurrentUserId() throws ShiroException {
		return getPrincipal().getUserId();
	}

	/**
	 * @return 当前登录会话的登录账号
	 */
	public static String getCurrentAuthcCode() throws ShiroException {
		return getPrincipal().getAuthcCode();
	}

	/**
	 * @return 当前登录会话登录账号类型
	 */
	public static AuthcChannel getCurrentAuthcChannel() throws ShiroException {
		return getPrincipal().getAuthcChannel();
	}

	/**
	 * 获取Session中的值，获取后删除
	 *
	 * @return
	 */
	public static Object popSessionAttribute(String key) {
		Object val = getSessionAttribute(key);
		getSession().removeAttribute(key);
		return val;
	}

	/**
	 * 从当前登录用户的Session里取值
	 *
	 * @param key
	 * @return
	 */
	public static Object getSessionAttribute(Object key) {
		return getSession().getAttribute(key);
	}

	/**
	 * 把值放入到当前登录用户的Session里
	 *
	 * @param key
	 * @param value
	 */
	public static void setSessionAttribute(Object key, Object value) {
		getSession().setAttribute(key, value);
	}

}
