package io.microvibe.booster.core.api.model;

import java.util.Optional;

public enum ApiDataType {

	JSON, XML;

	private static final ThreadLocal<ApiDataType> local = new ThreadLocal<>();

	public static void current(ApiDataType dataType) {
		local.set(dataType);
	}

	public static ApiDataType current() {
		return Optional.ofNullable(local.get()).orElse(JSON);
	}

}
