package io.microvibe.booster.core.base.search.builder.support;

import io.microvibe.booster.core.base.search.builder.QLStatement;

public interface Where extends QLStatement {
	int MAX_COUNT_OF_IN = 1000;

	default String conjunction() {
		return "\nAND ";
	}
}
