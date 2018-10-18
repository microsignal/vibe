package io.microvibe.booster.core.base.shiro.session;

import io.microvibe.booster.core.base.shiro.authc.AuthcChannel;
import io.microvibe.booster.core.base.web.utils.UserAgentType;
import lombok.Getter;
import lombok.Setter;
import org.apache.shiro.session.mgt.SimpleSession;

import java.io.Serializable;

public class OnlineSession extends SimpleSession {
	private static final long serialVersionUID = 1L;

	@Getter
	private transient boolean changed = false;// 属性是否改变 优化session数据同步

	@Getter
	private AuthcChannel authcChannel;// 认证方式
	@Getter
	private Serializable userId;// 当前登录的用户Id
	@Getter
	private String authcCode;// 认证账号
	@Getter
	private String userAgent;// 用户浏览器类型
	@Getter
	private UserAgentType userAgentType;
	@Getter
	private String systemHost;// 用户登录时主机地址
	@Getter
	private OnlineStatus status = OnlineStatus.ONLINE;// 在线状态

	@Getter
	@Setter
	private long lastPersistTime = 0; // 上次持久化时间

	public void persist() {
		changed = false;
	}

	private void change() {
		changed = true;
	}

	public void setAuthcChannel(AuthcChannel authcChannel) {
		this.authcChannel = authcChannel;
		change();
	}

	public void setUserId(Serializable userId) {
		this.userId = userId;
		change();
	}

	public void setAuthcCode(String authcCode) {
		this.authcCode = authcCode;
		change();
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
		change();
	}

	public void setUserAgentType(UserAgentType userAgentType) {
		this.userAgentType = userAgentType;
		change();
	}

	public void setSystemHost(String systemHost) {
		this.systemHost = systemHost;
		change();
	}

	public void setStatus(OnlineStatus status) {
		this.status = status;
		change();
	}

	@Override
	public void stop() {
		super.stop();
		change();
	}

	@Override
	public void setAttribute(Object key, Object value) {
		super.setAttribute(key, value);
		change();
	}

	@Override
	public Object removeAttribute(Object key) {
		Object removedAttribute = super.removeAttribute(key);
		change();
		return removedAttribute;
	}
}
