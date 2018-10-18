package io.microvibe.booster.core.base.entity;

import java.util.UUID;

public interface UuidSettable {

	public static void set(UuidSettable settable) {
		settable.setId(UUID.randomUUID().toString());
	}

	void setId(String id);
}
