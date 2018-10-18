package io.microvibe.castor.support;

import io.microvibe.castor.PrimeCastors;

public class ByteCastor extends AbstractMarshallableCastor<Byte> {

	public ByteCastor() {
		super(Byte.class);
	}

	public ByteCastor(Class<Byte> type) {
		super(type);
	}

	@Override
	public Byte castFromBasic(Object orig) {
		return Byte.valueOf(PrimeCastors.castToByte(orig));
	}

	@Override
	public Byte fromString(String s) {
		return Byte.valueOf(s);
	}

}
