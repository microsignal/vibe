package io.microvibe.booster.commons.err;

public class ResubmitException extends MessageException {

	private static final long serialVersionUID = 1L;
	public static final String RESUBMIT_ERROR_CODE = "error.resubmit";

	public ResubmitException() {
		super(RESUBMIT_ERROR_CODE);
	}

	public ResubmitException(Object... params) {
		super(RESUBMIT_ERROR_CODE, params);
	}

	public ResubmitException(Throwable cause) {
		super(cause, RESUBMIT_ERROR_CODE);
	}

	public ResubmitException(Throwable cause, Object... params) {
		super(cause, RESUBMIT_ERROR_CODE, params);
	}

}
