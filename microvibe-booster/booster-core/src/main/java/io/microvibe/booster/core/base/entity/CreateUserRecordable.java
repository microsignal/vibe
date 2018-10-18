package io.microvibe.booster.core.base.entity;

import java.io.Serializable;

public interface CreateUserRecordable<ID extends Serializable> {

	ID getCreateUser();

	void setCreateUser(ID createUser);
}
