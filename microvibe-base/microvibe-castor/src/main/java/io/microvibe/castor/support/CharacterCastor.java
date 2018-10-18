package io.microvibe.castor.support;

import io.microvibe.castor.PrimeCastors;

public class CharacterCastor extends AbstractMarshallableCastor<Character> {
	public CharacterCastor() {
		super(Character.class);
	}

	public CharacterCastor(Class<Character> type) {
		super(type);
	}

	@Override
	public Character castFromBasic(Object orig) {
		return Character.valueOf(PrimeCastors.castToChar(orig));
	}

	@Override
	public Character fromString(String s) {
		return s.length() > 0 ? s.charAt(0) : '\0';
	}

}
