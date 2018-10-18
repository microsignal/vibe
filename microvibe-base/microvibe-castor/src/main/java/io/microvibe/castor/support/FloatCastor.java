package io.microvibe.castor.support;

import io.microvibe.castor.PrimeCastors;

public class FloatCastor extends AbstractMarshallableCastor<Float> {
	public FloatCastor() {
		super(Float.class);
	}

	public FloatCastor(Class<Float> type) {
		super(type);
	}

	@Override
	public Float castFromBasic(Object orig) {
		return Float.valueOf(PrimeCastors.castToFloat(orig));
	}

	@Override
	public Float fromString(String s) {
		return Float.valueOf(s);
	}

}
