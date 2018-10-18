package io.microvibe.booster.core.base.entity;

import java.util.UUID;

public interface UuidHexSettable {

	public static void set(UuidHexSettable settable) {
		settable.setId(UUID.randomUUID().toString().replace("-", ""));
	}

	void setId(String id);
}
