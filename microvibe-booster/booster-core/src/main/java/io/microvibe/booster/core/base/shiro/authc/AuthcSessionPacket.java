package io.microvibe.booster.core.base.shiro.authc;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证成功后的会话信息包
 *
 * @since Oct 30, 2017
 */
@Getter
@Setter
public class AuthcSessionPacket<K extends Serializable> implements Serializable {
	private static final long serialVersionUID = 1L;

	private Map<Object, Object> attributes = new HashMap<>();
	private K userId;
	private AuthcChannel authcChannel;
	private String authcCode;

	public AuthcSessionPacket() {
		super();
	}

	public AuthcSessionPacket(K userId, AuthcChannel authcChannel, String authcCode) {
		super();
		this.userId = userId;
		this.authcChannel = authcChannel;
		this.authcCode = authcCode;
	}

	@Override
	public String toString() {
		return "<" + userId + "~" + authcChannel + "~" + authcCode + ">";
	}

	@Override
	public int hashCode() {
        /*int hash = platId.hashCode();
        hash = hash * 31 + userId.hashCode();
        hash = hash * 31 + authcChannel.hashCode();
        hash = hash * 31 + authcCode.hashCode();*/
		int hash = Arrays.hashCode(new Object[]{userId, authcChannel, authcCode});
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof AuthcSessionPacket)) {
			return false;
		}
		AuthcSessionPacket o = (AuthcSessionPacket) obj;
		return ObjectUtils.equals(this.userId, o.userId);
	}

	private Map<Object, Object> getAttributesLazy() {
		Map<Object, Object> attributes = getAttributes();
		if (attributes == null) {
			attributes = new HashMap<Object, Object>();
			setAttributes(attributes);
		}
		return attributes;
	}

	public Object getAttribute(Object key) {
		Map<Object, Object> attributes = getAttributes();
		if (attributes == null) {
			return null;
		}
		return attributes.get(key);
	}

	public void setAttribute(Object key, Object value) {
		if (value == null) {
			removeAttribute(key);
		} else {
			getAttributesLazy().put(key, value);
		}
	}

	public Object removeAttribute(Object key) {
		Map<Object, Object> attributes = getAttributes();
		if (attributes == null) {
			return null;
		} else {
			return attributes.remove(key);
		}
	}
}
