package io.microvibe.booster.system.err;

import io.microvibe.booster.commons.err.MessageException;

public class SystemException extends MessageException {

	private static final long serialVersionUID = 1L;

	public SystemException() {
		super();
	}

	public SystemException(String code, Object... params) {
		super(code, params);
	}

	public SystemException(Throwable cause, String code, Object... params) {
		super(cause, code, params);
	}

	public SystemException(Throwable cause) {
		super(cause);
	}

}
