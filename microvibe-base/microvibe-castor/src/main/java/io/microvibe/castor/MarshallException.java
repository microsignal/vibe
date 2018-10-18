package io.microvibe.castor;

public class MarshallException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MarshallException() {
		super();
	}

	public MarshallException(String message, Throwable cause) {
		super(message, cause);
	}

	public MarshallException(String message) {
		super(message);
	}

	public MarshallException(Throwable cause) {
		super(cause);
	}

}
