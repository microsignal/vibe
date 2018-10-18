package io.microvibe.booster.system.entity;

import io.microvibe.booster.core.base.shiro.authc.AuthcChannel;
import io.microvibe.booster.core.base.shiro.session.OnlineStatus;
import io.microvibe.booster.core.base.web.utils.UserAgentType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.*;

/**
 * 系统用户最近在线信息表
 *
 * @author Qt
 * @since Aug 01, 2018
 */
@Entity
@Table(name = "sys_user_last_session")
@Getter
@Setter
@EqualsAndHashCode
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@AttributeOverrides(
	@AttributeOverride(name = "id", column = @Column(name = "user_id"))
)
public class SysUserLastSession extends BaseSysEntity {

	private static final long serialVersionUID = 1L;
	/**
	 * 用户ID
	 */
	@Transient
	private Long userId;

	// region columns

	/**
	 * 认证方式
	 */
	@Column(name = "authc_channel")
	@Enumerated(EnumType.STRING)
	private AuthcChannel authcChannel;

	/**
	 * 认证账号
	 */
	@Column(name = "authc_code")
	private String authcCode;
	/**
	 * 最近在线会话ID
	 */
	@Column(name = "last_session_id")
	private String lastSessionId;
	/**
	 * 主机地址
	 */
	@Column(name = "host")
	private String host;
	/**
	 * 登录时系统IP
	 */
	@Column(name = "system_host")
	private String systemHost;
	/**
	 * 用户浏览器类型
	 */
	@Column(name = "user_agent")
	private String userAgent;
	/**
	 * 浏览器终端类型
	 */
	@Column(name = "user_agent_type")
	@Enumerated(EnumType.STRING)
	private UserAgentType userAgentType;
	/**
	 * 在线状态
	 */
	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private OnlineStatus status;
	/**
	 * 最后登录时间
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_login_timestamp")
	private java.util.Date lastLoginTimestamp;
	/**
	 * 最后退出时间
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_stop_timestamp")
	private java.util.Date lastStopTimestamp;
	/**
	 * 登录次数
	 */
	@Column(name = "login_count")
	private Long loginCount;
	/**
	 * 总的在线时长(秒)
	 */
	@Column(name = "total_online_time")
	private Long totalOnlineTime;

	@Formula("(select coalesce(user_nickname,user_account) from sys_user x where x.user_id = user_id)")
	private String userName;

	public static final SysUserLastSession fromUserOnline(SysUserSession userSession) {
		SysUserLastSession userLastSession = new SysUserLastSession();
		userLastSession.setUserId(userSession.getUserId());
		userLastSession.setAuthcChannel(userSession.getAuthcChannel());
		userLastSession.setAuthcCode(userSession.getAuthcCode());
		userLastSession.setLastSessionId(String.valueOf(userSession.getId()));
		userLastSession.setHost(userSession.getHost());
		userLastSession.setSystemHost(userSession.getSystemHost());
		userLastSession.setUserAgent(userSession.getUserAgent());
		userLastSession.setUserAgentType(userSession.getUserAgentType());
		userLastSession.setStatus(userSession.getStatus());
		userLastSession.setLastLoginTimestamp(userSession.getStartTimestamp());
		userLastSession.setLastStopTimestamp(userSession.getLastAccessTime());
		userLastSession.setDeleted(false);
		return userLastSession;
	}

	public static final void merge(SysUserLastSession from, SysUserLastSession to) {
		to.setUserId(from.getUserId());
		to.setAuthcChannel(from.getAuthcChannel());
		to.setAuthcCode(from.getAuthcCode());
		to.setLastSessionId(from.getLastSessionId());
		to.setHost(from.getHost());
		to.setSystemHost(from.getSystemHost());
		to.setUserAgent(from.getUserAgent());
		to.setUserAgentType(from.getUserAgentType());
		to.setStatus(from.getStatus());
		to.setLastLoginTimestamp(from.getLastLoginTimestamp());
		to.setLastStopTimestamp(from.getLastStopTimestamp());
	}
	// endregion columns

	@Override
	public void setId(Long id) {
		super.setId(id);
		this.userId = id;
	}

	public void setUserId(Long userId) {
		setId(userId);
	}

	public void incLoginCount() {
		if (loginCount == null) {
			loginCount = 0L;
		}
		loginCount += 1L;
	}

	public void incTotalOnlineTime() {
		long onlineTime = 0;
		if (lastStopTimestamp != null && lastLoginTimestamp != null) {
			onlineTime = lastStopTimestamp.getTime() - lastLoginTimestamp.getTime();
		}
		if (totalOnlineTime == null) {
			totalOnlineTime = 0L;
		}
		totalOnlineTime += onlineTime / 1000;
	}

}

