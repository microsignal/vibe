package io.microvibe.booster.core.base.entity;

import java.io.Serializable;

public interface UpdateUserRecordable<ID extends Serializable> {

	ID getUpdateUser();

	void setUpdateUser(ID updateUser);
}
