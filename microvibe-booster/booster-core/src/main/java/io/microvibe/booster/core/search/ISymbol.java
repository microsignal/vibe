package io.microvibe.booster.core.search;

public interface ISymbol {
	String name();

	String symbol();

	Object repair(Object searchValue);

	SearchValueType valueType();

	default boolean isNoValue() {
		return SearchValueType.NOTHING == valueType();
	}

	default boolean isSingleValue() {
		return SearchValueType.SINGLE == valueType();
	}

	default boolean isDoubleValue() {
		return SearchValueType.DOUBLE == valueType();
	}

	default boolean isListValue() {
		return SearchValueType.LIST == valueType();
	}
}
