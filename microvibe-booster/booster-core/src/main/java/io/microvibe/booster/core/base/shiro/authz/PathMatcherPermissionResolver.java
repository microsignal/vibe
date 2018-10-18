package io.microvibe.booster.core.base.shiro.authz;

import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.PermissionResolver;

public class PathMatcherPermissionResolver implements PermissionResolver {

	@Override
	public Permission resolvePermission(String permissionString) {
		return new PathMatcherPermission(permissionString);
	}

}
