package io.microvibe.booster.core.base.shiro.authz;

public interface PathMatcher {

	boolean match(String pattern, String path);

	boolean matchStart(String pattern, String path);

}
