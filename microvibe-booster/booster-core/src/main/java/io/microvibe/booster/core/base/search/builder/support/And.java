package io.microvibe.booster.core.base.search.builder.support;

import io.microvibe.booster.core.base.search.builder.QLStatement;

public class And extends Conj {

	public And() {
		super();
		this.conjunction = "\nAND ";
	}

	public And(QLStatement st) {
		this();
		this.st = st;
	}
}
