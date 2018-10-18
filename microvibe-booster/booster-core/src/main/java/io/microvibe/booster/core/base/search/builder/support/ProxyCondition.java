package io.microvibe.booster.core.base.search.builder.support;

import io.microvibe.booster.core.base.search.builder.QLParameter;
import io.microvibe.booster.core.base.search.builder.QLStatement;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class ProxyCondition extends Condition {

	private QLStatement st;

	public ProxyCondition(QLStatement st) {
		this.st = st;
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
	public List<QLParameter> bindedValues() {
		return st.bindedValues();
	}

}
