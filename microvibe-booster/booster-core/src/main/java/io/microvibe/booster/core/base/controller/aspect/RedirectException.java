package io.microvibe.booster.core.base.controller.aspect;

public class RedirectException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private String redirectUri;

    public RedirectException(Throwable cause, String redirectUri) {
        super(cause);
        this.redirectUri = redirectUri;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

	@Override
	public String getMessage() {
		return super.getMessage();
	}
}
