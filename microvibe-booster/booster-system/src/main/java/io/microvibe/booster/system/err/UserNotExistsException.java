package io.microvibe.booster.system.err;

public class UserNotExistsException extends UserException {
	private static final long serialVersionUID = 1L;

	public UserNotExistsException() {
		super("user.not.exists", (Object[]) null);
	}
}
