package io.microvibe.booster.core.api.model.impl;

import io.microvibe.booster.core.api.ApiConstants;
import io.microvibe.booster.core.api.model.HeadModel;

import java.util.Map;

public class JsonHeadModel extends JSONObjectWrapper implements HeadModel {
	private static final long serialVersionUID = 1L;

	public JsonHeadModel() {
		super(true);
	}

	public JsonHeadModel(Map<String, Object> map) {
		super(map);
	}

	@Override
	public JsonHeadModel clone() {
		return new JsonHeadModel((Map<String, Object>) super.clone());
	}

	@Override
	public String getCode() {
		return getString(ApiConstants.HEAD_CODE);
	}

	@Override
	public void setCode(String code) {
		put(ApiConstants.HEAD_CODE, code);
	}

	@Override
	public boolean isSuccess() {
		return getBooleanValue(ApiConstants.HEAD_SUCCESS);
	}

	@Override
	public void setSuccess(boolean success) {
		put(ApiConstants.HEAD_SUCCESS, success);
	}

	@Override
	public String getMessage() {
		return getString(ApiConstants.HEAD_MESSAGE);
	}

	@Override
	public void setMessage(String message) {
		put(ApiConstants.HEAD_MESSAGE, message);
	}

	@Override
	public String getTxnCode() {
		return getString(ApiConstants.TXN_CODE);
	}

	@Override
	public void setTxnCode(String txnCode) {
		put(ApiConstants.TXN_CODE, txnCode);
	}

}
