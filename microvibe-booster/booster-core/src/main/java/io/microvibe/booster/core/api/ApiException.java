package io.microvibe.booster.core.api;

import io.microvibe.booster.commons.err.MessageException;
import io.microvibe.booster.commons.spring.MessageResources;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class ApiException extends MessageException {
	private static final long serialVersionUID = 1L;
	private ReplyCode replyCode = ReplyCode.Error;
	private String message;

	public ApiException() {
		this(ReplyCode.Error);
	}

	public ApiException(ReplyCode replyCode, String message, Object[] params) {
		super(replyCode.getCode(), message);
		this.replyCode = replyCode;
		this.message = message(replyCode, message);
	}

	public ApiException(Throwable cause, ReplyCode replyCode, String message, Object[] params) {
		super(cause, replyCode.getCode(), message, params);
		if(cause instanceof  ApiException){
			this.replyCode = ((ApiException) cause).replyCode;
			this.message = ((ApiException) cause).message;
			log.warn("cause is an ApiException");
		}else {
			this.replyCode = replyCode;
			this.message = message(replyCode, message);
		}
	}

	public ApiException(ReplyCode replyCode) {
		super(replyCode.getCode(), replyCode.getMessage());
		this.replyCode = replyCode;
		this.message = replyCode.getMessage();
	}

	public ApiException(ReplyCode replyCode, String message) {
		this(replyCode, message, (Object[]) null);
	}

	public ApiException(Throwable cause, ReplyCode replyCode, String message) {
		this(cause, replyCode, message, (Object[]) null);
	}

	public ApiException(Throwable cause, String message) {
		this(cause, ReplyCode.Error, message);
	}

	public ApiException(String message) {
		this(ReplyCode.Error, message);
	}

	public ApiException(Throwable cause) {
		this(cause, ReplyCode.Error);
	}

	public ApiException(Throwable cause, ReplyCode replyCode) {
		this(cause, replyCode, cause.getMessage());
	}

	public ApiException(ReplyCode replyCode, Throwable cause) {
		this(cause, replyCode);
	}

	public static Builder builder() {
		return new Builder();
	}

	private String message(ReplyCode replyCode, String message) {
		message = StringUtils.trimToNull(message);
		if (message != null) {
			message = MessageResources.getMessage(message);
		}
		String codeMsg = replyCode.getMessage();
		return (message == null || message.equals(codeMsg) ? codeMsg : codeMsg + ". " + message);
	}

	public ReplyCode getReplyCode() {
		return replyCode;
	}

	@Override
	public String getMessage() {
		return message;
	}

	public String getCauseMessage() {
		Throwable cause = getCause();
		if (cause == null) return null;
		return cause.getMessage();
	}

	public static class Builder {
		Object[] params;
		Throwable cause;
		private ReplyCode replyCode = ReplyCode.Error;
		private String message;

		public void raise() throws ApiException {
			throw build();
		}

		public ApiException build() {
			if (cause != null) {
				if (message == null) {
					message = cause.getMessage();
				}
				return new ApiException(cause, replyCode, message, params);
			} else {
				return new ApiException(replyCode, message, params);
			}
		}

		public Builder replyCode(ReplyCode replyCode) {
			this.replyCode = replyCode;
			return this;
		}

		public Builder message(String message) {
			this.message = message;
			return this;
		}

		public Builder params(Object[] params) {
			this.params = params;
			return this;
		}

		public Builder cause(Throwable cause) {
			this.cause = cause;
			return this;
		}
	}

}
