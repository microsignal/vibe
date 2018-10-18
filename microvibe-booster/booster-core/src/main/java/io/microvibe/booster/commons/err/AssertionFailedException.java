package io.microvibe.booster.commons.err;

public class AssertionFailedException extends MessageException {

	private static final long serialVersionUID = 1L;

	public AssertionFailedException() {
		super();
	}

	public AssertionFailedException(String code, Object... params) {
		super(code, params);
	}

	public AssertionFailedException(Throwable cause) {
		super(cause);
	}

	public AssertionFailedException(Throwable cause, String code, Object... params) {
		super(cause, code, params);
	}

	public static void shouldNotHappen(Throwable cause) throws AssertionFailedException {
		throw new AssertionFailedException(cause);
	}
}
