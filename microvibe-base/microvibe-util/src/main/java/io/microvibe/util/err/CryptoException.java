package io.microvibe.util.err;

public class CryptoException extends MessageException {

	private static final long serialVersionUID = 1L;

	public CryptoException() {
		super();
	}

	public CryptoException(String code, Object... params) {
		super(code, params);
	}

	public CryptoException(Throwable cause) {
		super(cause);
	}

	public CryptoException(Throwable cause, String code, Object... params) {
		super(cause, code, params);
	}

}
