package io.microvibe.booster.core.base.shiro.authz;

import org.apache.shiro.authz.Permission;

public class AntPathMatcherPermission implements Permission {
	static PathMatcher pathMatcher;

	static {
		pathMatcher = new AntPathMatcher(":");
	}

	private String permission;

	public AntPathMatcherPermission(String permission) {
		this.permission = permission;
	}

	public String getPermission() {
		return permission;
	}

	@Override
	public boolean implies(Permission p) {
		if (!(p instanceof AntPathMatcherPermission)) {
			return false;
		}
		return pathMatcher.match(this.permission, ((AntPathMatcherPermission) p).getPermission());
	}

}
