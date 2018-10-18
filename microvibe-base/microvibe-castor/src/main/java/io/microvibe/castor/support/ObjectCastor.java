package io.microvibe.castor.support;

public class ObjectCastor extends AbstractMarshallableCastor<Object> {

	public ObjectCastor() {
		super(Object.class);
	}

	public ObjectCastor(Class<Object> type) {
		super(type);
	}

	@Override
	public Object castFromBasic(Object orig) {
		return orig;
	}

	@Override
	public Object fromString(String s) {
		return s == null ? "" : s;
	}

}
