package io.microvibe.castor.support;

import java.math.BigInteger;

;

public class BigIntegerCastor extends AbstractMarshallableCastor<BigInteger> {

	public BigIntegerCastor() {
		super(BigInteger.class);
	}

	public BigIntegerCastor(Class<BigInteger> type) {
		super(type);
	}

	@Override
	public BigInteger castFromBasic(Object orig) {
		return fromString(orig.toString());
	}

	@Override
	public BigInteger fromString(String s) {
		return new BigInteger(s);
	}

}
