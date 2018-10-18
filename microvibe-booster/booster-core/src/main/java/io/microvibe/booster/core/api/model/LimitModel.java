package io.microvibe.booster.core.api.model;

import java.util.Map;

public interface LimitModel extends java.io.Serializable, IJSONObject, Map<String, Object> {

	LimitModel clone();

	boolean hasPageNumber();

	boolean hasPageSize();

	int getPageNumber();

	int getPageSize();

	void setPageNumber(int pageNumber);

	void setPageSize(int pageSize);
}
