package io.microvibe.util.castor.support;

import io.microvibe.util.castor.PrimeCastors;

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
