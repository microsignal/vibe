package io.microvibe.castor.support;

public class StringBufferCastor extends AbstractMarshallableCastor<StringBuffer> {
	public StringBufferCastor() {
		super(StringBuffer.class);
	}

	public StringBufferCastor(Class<StringBuffer> type) {
		super(type);
	}

	@Override
	public StringBuffer castFromBasic(Object orig) {
		return new StringBuffer(orig == null ? "" : orig.toString());
	}

	@Override
	public StringBuffer fromString(String s) {
		return new StringBuffer(s == null ? "" : s);
	}

}
