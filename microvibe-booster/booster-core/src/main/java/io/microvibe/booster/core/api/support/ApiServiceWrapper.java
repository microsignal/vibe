package io.microvibe.booster.core.api.support;

import io.microvibe.booster.core.api.ApiService;

public interface ApiServiceWrapper {

	boolean isSessionAuthcRequired();

	ApiService getApiService();

	boolean isAuthzRequired();

	String getAuthzPermission();

}
