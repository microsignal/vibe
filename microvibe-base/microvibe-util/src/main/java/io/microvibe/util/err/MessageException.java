package io.microvibe.util.err;

import io.microvibe.util.spring.MessageReources;

/**
 *
 * @author Qt
 * @since Aug 9, 2017
 * @version 1.0 initial
 * @version 2.0 optimized
 *
 */
public class MessageException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private static final String CODE_UNKNOWN = "unknown";
	private String code;
	private String message;

	public MessageException() {
		super();
		this.code = CODE_UNKNOWN;
		this.message = MessageReources.getMessage(this.code);
	}

	public MessageException(final String code, final Object... params) {
		super(code);
		this.code = code;
		this.message = MessageReources.getMessage(code, params);
	}

	public MessageException(final Throwable cause, final String code,
			final Object... params) {
		super(code, cause);
		this.code = code;
		this.message = MessageReources.getMessage(code, params);
	}

	public MessageException(final Throwable cause) {
		super(cause);
		if (cause instanceof MessageException) {
			this.code = ((MessageException) cause).getCode();
			this.message = ((MessageException) cause).getMessage();
		} else {
			this.code = (cause == null ? CODE_UNKNOWN : cause.getMessage());
			this.message = MessageReources.getMessage(code);
		}
	}

	public String getCode() {
		return this.code;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

}
