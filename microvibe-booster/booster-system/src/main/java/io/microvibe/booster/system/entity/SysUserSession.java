package io.microvibe.booster.system.entity;

import io.microvibe.booster.commons.spring.ApplicationContextHolder;
import io.microvibe.booster.commons.utils.BeanUtils;
import io.microvibe.booster.core.base.shiro.authc.AuthcChannel;
import io.microvibe.booster.core.base.shiro.session.OnlineSession;
import io.microvibe.booster.core.base.shiro.session.OnlineStatus;
import io.microvibe.booster.core.base.web.utils.UserAgentType;
import io.microvibe.booster.core.env.SystemEnv;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import javax.persistence.*;
import java.util.Base64;

/**
 * 系统用户在线会话信息表
 *
 * @author Qt
 * @since Aug 01, 2018
 */
@Entity
@Table(name = "sys_user_session")
@Getter
@Setter
@EqualsAndHashCode
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@AttributeOverrides(
	@AttributeOverride(name = "id", column = @Column(name = "id"))
)
public class SysUserSession extends BaseSysUuidEntity {

	private static final long serialVersionUID = 1L;

	// region columns
	/**
	 * 用户ID
	 */
	@Column(name = "user_id")
	private Long userId;
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
	 * 登录IP 地址
	 */
	@Column(name = "host")
	private String host;
	/**
	 * 登录时主机地址
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
	 * session创建时间
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "start_timestamp")
	private java.util.Date startTimestamp;
	/**
	 * session最后访问时间
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_access_time")
	private java.util.Date lastAccessTime;
	/**
	 * 超时时间
	 */
	@Column(name = "timeout")
	private Long timeout;
	/**
	 * 备份的当前用户会话
	 */
	@Column(name = "session")
	private String sessionContent;
	@Column(name = "session_persist_time")
	private long sessionPersistTime = 0;
	// endregion columns

	@Formula("(select coalesce(user_nickname,user_account) from sys_user x where x.user_id = user_id)")
	private String userName;

	@Transient
	private transient OnlineSession session;


	public static final SysUserSession fromOnlineSession(OnlineSession session) {
		SysUserSession userSession = new SysUserSession();
		userSession.setId(session.getId().toString());
		userSession.setUserId(session.getUserId() == null || !(session.getUserId() instanceof Long) ? null : (Long)session.getUserId());
		userSession.setAuthcChannel(session.getAuthcChannel());
		userSession.setAuthcCode(session.getAuthcCode());
		userSession.setHost(session.getHost());
		userSession.setSystemHost(session.getSystemHost());
		userSession.setUserAgent(session.getUserAgent());
		userSession.setUserAgentType(session.getUserAgentType());
		userSession.setStatus(session.getStatus());
		userSession.setStartTimestamp(session.getStartTimestamp());
		userSession.setLastAccessTime(session.getLastAccessTime());
		userSession.setTimeout(session.getTimeout());
		userSession.setSession(session);
		userSession.setSessionPersistTime(session.getLastPersistTime());
		userSession.setDeleted(false);
		return userSession;
	}

	public String getSessionContent() {
		if (sessionContent == null && session != null) {
			this.sessionContent = convert(session);
		}
		return sessionContent;
	}

	public void setSessionContent(String sessionContent) {
		this.sessionContent = sessionContent;
		this.session = convert(sessionContent);
	}


	public OnlineSession getSession() {
		if (session == null && sessionContent != null) {
			this.session = convert(sessionContent);
		}
		return session;
	}

	public void setSession(OnlineSession session) {
		this.session = session;
		this.sessionContent = convert(session);
	}

	public OnlineSession convert(String session) {
		Object obj = SerializationUtils.deserialize(Base64.getDecoder().decode(session));
		if (!(obj instanceof OnlineSession)) {
			if (ApplicationContextHolder.getBean(SystemEnv.class).isDevelopMode()) {
				if (obj.getClass().getName().equalsIgnoreCase(OnlineSession.class.getName())) {
					// 类路径相同,类加载器不同
					OnlineSession newOne = new OnlineSession();
					BeanUtils.copyProperties(obj, newOne);
					obj = newOne;
				}
			}
		}
		return (OnlineSession) obj;
	}

	public String convert(OnlineSession session) {
		return Base64.getEncoder().encodeToString(SerializationUtils.serialize(session));
	}
}

