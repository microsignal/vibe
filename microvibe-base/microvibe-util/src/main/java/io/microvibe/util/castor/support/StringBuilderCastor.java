package io.microvibe.util.castor.support;

public class StringBuilderCastor extends AbstractMarshallableCastor<StringBuilder> {
	public StringBuilderCastor() {
		super(StringBuilder.class);
	}

	public StringBuilderCastor(Class<StringBuilder> type) {
		super(type);
	}

	@Override
	public StringBuilder castFromBasic(Object orig) {
		return new StringBuilder(orig == null ? "" : orig.toString());
	}

	@Override
	public StringBuilder fromString(String s) {
		return new StringBuilder(s == null ? "" : s);
	}

}
