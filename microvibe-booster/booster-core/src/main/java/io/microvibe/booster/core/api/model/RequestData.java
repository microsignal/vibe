package io.microvibe.booster.core.api.model;

public interface RequestData extends Data {

	@Override
	RequestData clone();

	ResponseData buildResponse();

}
