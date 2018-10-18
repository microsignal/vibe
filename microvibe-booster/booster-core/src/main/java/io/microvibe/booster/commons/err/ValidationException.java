package io.microvibe.booster.commons.err;

public class ValidationException extends MessageException {

	private static final long serialVersionUID = 1L;

	public ValidationException() {
		super();
	}

	public ValidationException(String code, Object... params) {
		super(code, params);
	}

	public ValidationException(Throwable cause) {
		super(cause);
	}

	public ValidationException(Throwable cause, String code, Object... params) {
		super(cause, code, params);
	}

	public static void shouldNotHappen(Throwable cause) throws ValidationException {
		throw new ValidationException(cause);
	}
}
