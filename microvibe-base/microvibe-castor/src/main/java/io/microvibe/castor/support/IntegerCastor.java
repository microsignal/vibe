package io.microvibe.castor.support;

import io.microvibe.castor.PrimeCastors;

public class IntegerCastor extends AbstractMarshallableCastor<Integer> {

	public IntegerCastor() {
		super(Integer.class);
	}
	public IntegerCastor(Class<Integer> type) {
		super(type);
	}

	@Override
	public Integer castFromBasic(Object orig) {
		return Integer.valueOf(PrimeCastors.castToInt(orig));
	}

	@Override
	public Integer fromString(String s) {
		return Integer.valueOf(s);
	}

}
