package io.microvibe.castor.support;

public class StringCastor extends AbstractMarshallableCastor<String> {

	public StringCastor() {
		super(String.class);
	}

	public StringCastor(Class<String> type) {
		super(type);
	}

	@Override
	public String castFromBasic(Object orig) {
		return orig == null ? "" : orig.toString();
	}

	@Override
	public String fromString(String s) {
		return s == null ? "" : s;
	}

}
