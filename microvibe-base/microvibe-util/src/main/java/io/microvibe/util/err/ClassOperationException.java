package io.microvibe.util.err;

public class ClassOperationException extends MessageException {

	private static final long serialVersionUID = 1L;

	public ClassOperationException() {
		super();
	}

	public ClassOperationException(String code, Object... params) {
		super(code, params);
	}

	public ClassOperationException(Throwable cause) {
		super(cause);
	}

	public ClassOperationException(Throwable cause, String code, Object... params) {
		super(cause, code, params);
	}

}
