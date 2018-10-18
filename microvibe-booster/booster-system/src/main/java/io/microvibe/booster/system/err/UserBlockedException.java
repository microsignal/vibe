package io.microvibe.booster.system.err;

public class UserBlockedException extends UserException {
	private static final long serialVersionUID = 1L;

	public UserBlockedException() {
		super("user.blocked", new Object[]{});
	}

	public UserBlockedException(String reason) {
		super("user.blocked", new Object[]{reason});
	}
}
