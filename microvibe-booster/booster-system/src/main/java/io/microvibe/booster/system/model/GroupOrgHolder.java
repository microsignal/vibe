package io.microvibe.booster.system.model;

import java.io.Serializable;

public interface GroupOrgHolder extends Serializable, GroupHolder, OrgHolder {

	String getGroupName();

	void setGroupName(String groupName);

	String getOrgName();

	void setOrgName(String orgName);
}
