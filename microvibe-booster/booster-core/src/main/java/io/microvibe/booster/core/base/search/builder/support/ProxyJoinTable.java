package io.microvibe.booster.core.base.search.builder.support;

import io.microvibe.booster.core.base.search.builder.QLParameter;
import io.microvibe.booster.core.base.search.builder.QLStatement;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProxyJoinTable extends JoinTable {
	private List<QLParameter> bindedValues;

	public ProxyJoinTable(QLStatement st, String alias, String join, String on) {
		super(st.statement(), alias, join, on);
		this.bindedValues = st.bindedValues();
	}

	@Override
	public List<QLParameter> bindedValues() {
		return bindedValues;
	}

}
