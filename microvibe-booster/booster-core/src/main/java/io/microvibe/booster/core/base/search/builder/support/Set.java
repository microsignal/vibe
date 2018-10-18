package io.microvibe.booster.core.base.search.builder.support;

import io.microvibe.booster.core.base.search.builder.QL;
import io.microvibe.booster.core.base.search.builder.QLParameter;
import io.microvibe.booster.core.base.search.builder.QLStatement;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Setter
@Getter
public class Set {
	private String key;
	private Optional<Object> val;

	private Optional<String> subQuery;
	private Object[] args;

	public QLStatement toStatement() {
		QL st = new QL();
		List<QLParameter> bindedValues = new ArrayList<>();
		List<String> bindedKeys = new ArrayList<>();

		Pattern p = Pattern.compile("(\\\\*)\\{\\}");
		Matcher m = p.matcher(subQuery.get());
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			if ((m.group(1).length() & 1) == 0) {
				m.appendReplacement(sb, m.group(1));
				String bindedKey = BindedKeys.nextBindedKey();
				bindedKeys.add(bindedKey);
				sb.append(":").append(bindedKey);
			} else {
				m.appendReplacement(sb, m.group());
			}
		}
		m.appendTail(sb);

		Object[] args = this.args;
		int i = 0;
		for (String bindedKey : bindedKeys) {
			if (i < args.length) {
				bindedValues.add(new QLParameter(bindedKey, args[i]));
				i++;
			} else {
				bindedValues.add(new QLParameter(bindedKey, null));
			}
		}
		return st;
	}

}
