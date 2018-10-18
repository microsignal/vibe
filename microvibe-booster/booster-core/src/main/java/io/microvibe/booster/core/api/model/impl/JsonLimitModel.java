package io.microvibe.booster.core.api.model.impl;

import io.microvibe.booster.core.api.ApiConstants;
import io.microvibe.booster.core.api.ApiException;
import io.microvibe.booster.core.api.ReplyCode;
import io.microvibe.booster.core.api.model.LimitModel;

import java.util.Map;

public class JsonLimitModel extends JSONObjectWrapper implements LimitModel {
	private static final long serialVersionUID = 1L;

	public JsonLimitModel() {
		super(true);
	}

	public JsonLimitModel(Map<String, Object> map) {
		super(map);
	}

	@Override
	public JsonLimitModel clone() {
		return new JsonLimitModel((Map<String, Object>) super.clone());
	}

	@Override
	public boolean hasPageNumber() {
		return containsKey(ApiConstants.BODY_LIMIT_NUM);
	}

	@Override
	public boolean hasPageSize() {
		return containsKey(ApiConstants.BODY_LIMIT_SIZE);
	}

	@Override
	public int getPageNumber() {
		try {
			return getIntValue(ApiConstants.BODY_LIMIT_NUM);
		} catch (Exception e) {
			throw new ApiException(ReplyCode.RequestParseError, e);
		}
	}

	@Override
	public void setPageNumber(int pageNumber) {
		put(ApiConstants.BODY_LIMIT_NUM, pageNumber);
	}

	@Override
	public int getPageSize() {
		try {
			return getIntValue(ApiConstants.BODY_LIMIT_SIZE);
		} catch (Exception e) {
			throw new ApiException(ReplyCode.RequestParseError, e);
		}
	}

	@Override
	public void setPageSize(int pageSize) {
		put(ApiConstants.BODY_LIMIT_SIZE, pageSize);
	}

}
