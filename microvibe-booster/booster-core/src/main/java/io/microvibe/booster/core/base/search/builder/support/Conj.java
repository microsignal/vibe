package io.microvibe.booster.core.base.search.builder.support;

import io.microvibe.booster.core.base.search.builder.QLParameter;
import io.microvibe.booster.core.base.search.builder.QLStatement;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public abstract class Conj implements Where {

	protected QLStatement st;
	protected String conjunction = "\nAND ";

	@Override
	public List<QLParameter> bindedValues() {
		if (st != null) {
			return st.bindedValues();
		}
		return Where.super.bindedValues();
	}

	@Override
	public String statement() {
		if (st == null) {
			return null;
		}
		String clause = StringUtils.trimToNull(st.statement());
		if (clause != null) {
			clause = "(\n" + clause + "\n)";
		}
		return clause;
	}

	@Override
	public String conjunction() {
		return conjunction;
	}
}
