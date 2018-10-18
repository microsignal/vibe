package io.microvibe.util.err;

public class ContextLoadException extends MessageException {

	private static final long serialVersionUID = 1L;

	public ContextLoadException() {
		super();
	}

	public ContextLoadException(String code, Object... params) {
		super(code, params);
	}

	public ContextLoadException(Throwable cause) {
		super(cause);
	}

	public ContextLoadException(Throwable cause, String code, Object... params) {
		super(cause, code, params);
	}

}
