package io.microvibe.booster.system.err;

public class UserPasswordNotMatchException extends UserException {
	private static final long serialVersionUID = 1L;

	public UserPasswordNotMatchException() {
		super("user.password.not.match", (Object[]) null);
	}
}
