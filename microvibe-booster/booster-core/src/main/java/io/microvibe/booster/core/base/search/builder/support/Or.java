package io.microvibe.booster.core.base.search.builder.support;

import io.microvibe.booster.core.base.search.builder.QLStatement;

public class Or extends Conj {

	public Or() {
		super();
		this.conjunction = "\nOR ";
	}

	public Or(QLStatement st) {
		this();
		this.st = st;
	}

}
