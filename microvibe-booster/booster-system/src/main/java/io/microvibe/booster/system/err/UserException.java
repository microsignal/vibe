package io.microvibe.booster.system.err;

public class UserException extends SystemException {

	private static final long serialVersionUID = 1L;

	public UserException() {
		super();
	}

	public UserException(String code, Object... params) {
		super(code, params);
	}

	public UserException(Throwable cause, String code, Object... params) {
		super(cause, code, params);
	}

	public UserException(Throwable cause) {
		super(cause);
	}

}
