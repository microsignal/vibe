package io.microvibe.booster.system.model;


import java.io.Serializable;

public interface CurrentUser<ID extends Serializable> extends Serializable {
	String BIND_ID = "currentUser";

	public boolean isAdministrator();

	public ID getId();

	public String getUsername();

//	public UserStatus getStatus();

//	public String getMobilePhone();

}
