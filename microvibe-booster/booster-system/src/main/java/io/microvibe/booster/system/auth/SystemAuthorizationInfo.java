package io.microvibe.booster.system.auth;

import io.microvibe.booster.commons.utils.StringUtils;
import org.apache.shiro.authz.SimpleAuthorizationInfo;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Qt
 * @since Aug 09, 2018
 */
public class SystemAuthorizationInfo extends SimpleAuthorizationInfo {
	/*
	public static final String ORG_PERMISSION_PREFIX = "data-org:";
	public static final String GROUP_PERMISSION_PREFIX = "data-group:";
	public static final String ASSAY_PERMISSION_PREFIX = "data-assay:";

	// 公司权限
	protected Set<String> orgPermissions = new LinkedHashSet<String>();
	// 集团权限
	protected Set<String> groupPermissions = new LinkedHashSet<String>();
	// 化验单档案权限
	protected Set<String> assayPermissions = new LinkedHashSet<String>();

	public SystemAuthorizationInfo() {
		super.roles = new LinkedHashSet<>();
		super.stringPermissions = new LinkedHashSet<>();
	}

	public boolean hasOrgPermission(String orgId) {
		return orgPermissions.contains(orgId);
	}

	public boolean hasGroupPermission(String groupId) {
		return groupPermissions.contains(groupId);
	}

	public boolean hasAssayPermission(String assayFormatId) {
		return assayPermissions.contains(assayFormatId);
	}

	public void addOrgPermission(SysOrganization organization) {
		String orgId = organization.getId();
		if (StringUtils.isNotBlank(orgId)) {
			this.orgPermissions.add(orgId);
			addStringPermission(ORG_PERMISSION_PREFIX + orgId);
		}
		// 集团
		String groupId = organization.getGroupId();
		if (StringUtils.isNotBlank(groupId)) {
			this.groupPermissions.add(groupId);
			addStringPermission(GROUP_PERMISSION_PREFIX + groupId);
		}
	}

	public void addGroupPermission(SysGroup group) {
		String groupId = group.getId();
		if (StringUtils.isNotBlank(groupId)) {
			this.groupPermissions.add(groupId);
			addStringPermission(GROUP_PERMISSION_PREFIX + groupId);
		}
	}

	public void addAssayPermission(SysViewQcAssayFormat assay) {
		String assayId = assay.getId();
		if (StringUtils.isNotBlank(assayId)) {
			this.assayPermissions.add(assayId);
			addStringPermission(ASSAY_PERMISSION_PREFIX + assayId);
		}
	}

	public Set<String> getOrgPermissions() {
		return orgPermissions;
	}

	public Set<String> getGroupPermissions() {
		return groupPermissions;
	}

	public Set<String> getAssayPermissions() {
		return assayPermissions;
	}*/

	/*
	public Set<String> getStringPermissions() {
		return stringPermissions;
	}
	public Set<String> getRoles() {
		return roles;
	}
	*/

}
