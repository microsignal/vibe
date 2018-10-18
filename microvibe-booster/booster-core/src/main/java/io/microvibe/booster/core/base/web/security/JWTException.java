package io.microvibe.booster.core.base.web.security;

import io.microvibe.booster.commons.err.MessageException;

public class JWTException extends MessageException {

	private static final long serialVersionUID = 1L;

	public JWTException() {
		super();
	}

	public JWTException(String code, Object... params) {
		super(code, params);
	}

	public JWTException(Throwable cause, String code, Object... params) {
		super(cause, code, params);
	}

	public JWTException(Throwable cause) {
		super(cause);
	}

}
