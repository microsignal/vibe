package io.microvibe.util.castor.support;

import java.math.BigDecimal;

public class BigDecimalCastor extends AbstractMarshallableCastor<BigDecimal> {
	public BigDecimalCastor() {
		super(BigDecimal.class);
	}

	public BigDecimalCastor(Class<BigDecimal> type) {
		super(type);
	}

	@Override
	public BigDecimal castFromBasic(Object orig) {
		return fromString(orig.toString());
	}

	@Override
	public BigDecimal fromString(String s) {
		return new BigDecimal(s);
	}

}
