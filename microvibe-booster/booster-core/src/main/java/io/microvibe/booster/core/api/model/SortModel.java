package io.microvibe.booster.core.api.model;

import org.springframework.data.domain.Sort.Direction;

import java.util.Map;

public interface SortModel extends java.io.Serializable, IJSONObject, Map<String, Object> {

	SortModel clone();

	String getSortKey();

	Direction getSortDirection();

	void setSortKey(String sortKey);

	void setSortDirection(Direction sortDirection);
}
