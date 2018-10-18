package io.microvibe.util.compile;

public class CompileException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CompileException() {
	}

	public CompileException(String message) {
		super(message);
	}

	public CompileException(Throwable cause) {
		super(cause);
	}

	public CompileException(String message, Throwable cause) {
		super(message, cause);
	}

}
