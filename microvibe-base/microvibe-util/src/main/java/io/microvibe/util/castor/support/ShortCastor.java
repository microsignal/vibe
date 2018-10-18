package io.microvibe.util.castor.support;

import io.microvibe.util.castor.PrimeCastors;

public class ShortCastor extends AbstractMarshallableCastor<Short> {
	public ShortCastor() {
		super(Short.class);
	}

	public ShortCastor(Class<Short> type) {
		super(type);
	}

	@Override
	public Short castFromBasic(Object orig) {
		return Short.valueOf(PrimeCastors.castToShort(orig));
	}

	@Override
	public Short fromString(String s) {
		return Short.valueOf(s);
	}

}
