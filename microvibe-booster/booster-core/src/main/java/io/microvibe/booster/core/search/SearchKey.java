package io.microvibe.booster.core.search;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class SearchKey {

	final String property;
	final ISymbol symbol;

	@Override
	public String toString() {
		return property + Searches.DELIMITER + symbol.name();
	}

}
