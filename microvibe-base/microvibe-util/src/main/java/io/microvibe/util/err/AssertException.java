package io.microvibe.util.err;

public class AssertException extends MessageException {

	private static final long serialVersionUID = 1L;

	public AssertException() {
		super();
	}

	public AssertException(String code, Object... params) {
		super(code, params);
	}

	public AssertException(Throwable cause) {
		super(cause);
	}

	public AssertException(Throwable cause, String code, Object... params) {
		super(cause, code, params);
	}

}
