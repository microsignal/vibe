package io.microvibe.booster.core.base.search.builder;

import java.util.Collections;
import java.util.List;

public interface QLStatement {

	default boolean isNative() {
		return false;
	}

	default String statement() {
		return "";
	}

	;

	default String countStatement() {
		throw new UnsupportedOperationException();
	}

	default List<QLParameter> bindedValues() {
		return Collections.emptyList();
	}
}
