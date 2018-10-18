package io.microvibe.util.castor.support;

import io.microvibe.util.castor.PrimeCastors;

public class BooleanCastor extends AbstractMarshallableCastor<Boolean> {

	public BooleanCastor() {
		super(Boolean.class);
	}

	public BooleanCastor(Class<Boolean> type) {
		super(type);
	}

	@Override
	public Boolean castFromBasic(Object orig) {
		return Boolean.valueOf(PrimeCastors.castToBoolean(orig));
	}

	@Override
	public Boolean fromString(String s) {
		return Boolean.valueOf(s);
	}

}
