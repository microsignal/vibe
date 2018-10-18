package io.microvibe.util.castor.support;


public class NullCastor extends AbstractMarshallableCastor<Object> {

	public NullCastor() {
		super(Object.class);
	}

	public NullCastor(Class<Object> type) {
		super(type);
	}

	@Override
	public Object castFromBasic(Object orig) {
		return null;
	}

	@Override
	public Object fromString(String s) {
		return null;
	}

}
