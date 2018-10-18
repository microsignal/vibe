package io.microvibe.booster.commons.err;

public class TokenException extends MessageException {

	private static final long serialVersionUID = 1L;
	public static final String RESUBMIT_ERROR_CODE = "error.token";

	public TokenException() {
		super(RESUBMIT_ERROR_CODE);
	}

	public TokenException(Object... params) {
		super(RESUBMIT_ERROR_CODE, params);
	}

	public TokenException(Throwable cause) {
		super(cause, RESUBMIT_ERROR_CODE);
	}

	public TokenException(Throwable cause, Object... params) {
		super(cause, RESUBMIT_ERROR_CODE, params);
	}

}
