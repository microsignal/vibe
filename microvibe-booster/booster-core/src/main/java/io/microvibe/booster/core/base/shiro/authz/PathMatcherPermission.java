package io.microvibe.booster.core.base.shiro.authz;

import org.apache.shiro.authz.Permission;

public class PathMatcherPermission implements Permission {
	static PathMatcher pathMatcher;

	static {
		pathMatcher = DefaultPathMatchers.buildPathMatcher(':');
	}

	private String permission;

	public PathMatcherPermission(String permission) {
		this.permission = permission;
	}

	public String getPermission() {
		return permission;
	}

	@Override
	public boolean implies(Permission p) {
		if (!(p instanceof PathMatcherPermission)) {
			return false;
		}
		return pathMatcher.match(this.permission, ((PathMatcherPermission) p).getPermission());
	}

}
