package io.microvibe.booster.core.base.search.builder.support;

import io.microvibe.booster.core.base.search.builder.QLParameter;
import io.microvibe.booster.core.base.search.builder.QLStatement;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProxyTable extends Table {

	private List<QLParameter> bindedValues;

	public ProxyTable(QLStatement st, String alias) {
		super(st.statement(), alias);
		this.bindedValues = st.bindedValues();
	}

	@Override
	public List<QLParameter> bindedValues() {
		return bindedValues;
	}
}
