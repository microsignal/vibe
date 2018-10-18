package io.microvibe.booster.commons.err;

public class JsonException extends MessageException {

	private static final long serialVersionUID = 1L;

	public JsonException() {
		super();
	}

	public JsonException(String code, Object... params) {
		super(code, params);
	}

	public JsonException(Throwable cause) {
		super(cause);
	}

	public JsonException(Throwable cause, String code, Object... params) {
		super(cause, code, params);
	}

}
