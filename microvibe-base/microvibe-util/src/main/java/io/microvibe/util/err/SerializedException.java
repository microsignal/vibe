package io.microvibe.util.err;

public class SerializedException extends MessageException {

	private static final long serialVersionUID = 1L;

	public SerializedException() {
		super();
	}

	public SerializedException(String code, Object... params) {
		super(code, params);
	}

	public SerializedException(Throwable cause) {
		super(cause);
	}

	public SerializedException(Throwable cause, String code, Object... params) {
		super(cause, code, params);
	}

}
