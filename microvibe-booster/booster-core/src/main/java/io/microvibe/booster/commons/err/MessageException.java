package io.microvibe.booster.commons.err;


import io.microvibe.booster.commons.spring.MessageResources;

public class MessageException extends BaseException {

	private static final long serialVersionUID = 1L;
	private static final String CODE_UNKNOWN = "unknown";
	private String code;
	private String message;

	public MessageException(final String code, final String defaultMessage, final Object[] params) {
		super(code);
		this.code = code;
		this.message = MessageResources.getMessageOrDefault(code, defaultMessage, params);
	}

	public MessageException(final Throwable cause, final String code, final String defaultMessage, final Object[] params) {
		super(code, cause);
		this.code = code;
		this.message = MessageResources.getMessageOrDefault(code, defaultMessage, params);
	}

	public MessageException() {
		this(CODE_UNKNOWN);
	}

	public MessageException(final String code) {
		this(code, code, null);
	}

	public MessageException(final String code, final String defaultMessage) {
		this(code, defaultMessage, null);
	}

	public MessageException(final String code, final Object[] params) {
		this(code, code, params);
	}

	public MessageException(final Throwable cause, final String code) {
		this(cause, code, code, null);
	}

	public MessageException(final Throwable cause, final String code, final String defaultMessage) {
		this(cause, code, defaultMessage, null);
	}

	public MessageException(final Throwable cause, final String code, final Object[] params) {
		this(cause, code, code, params);
	}

	public MessageException(final Throwable cause) {
		super(cause);
		this.code = CODE_UNKNOWN;
		if (cause == null) {
			this.message = MessageResources.getMessage(code);
		} else if (cause instanceof MessageException) {
			this.code = ((MessageException) cause).getCode();
			this.message = ((MessageException) cause).getMessage();
		} else {
			String message = cause.getMessage();
			if (message == null) {
				this.message = MessageResources.getMessage(code);
			} else {
				this.message = message;
			}
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
