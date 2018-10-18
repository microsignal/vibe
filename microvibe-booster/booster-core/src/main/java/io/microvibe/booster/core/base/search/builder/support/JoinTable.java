package io.microvibe.booster.core.base.search.builder.support;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

@Getter
@Setter
public class JoinTable extends Table {

	private String join;
	private String on;

	public JoinTable() {
	}

	public JoinTable(Class<?> entityClass, String alias, String join, String on) {
		super(entityClass, alias);
		this.join = join;
		this.on = on;
	}

	public JoinTable(String subQuery, String alias, String join, String on) {
		super(subQuery, alias);
		this.join = join;
		this.on = on;
	}

	@Override
	public String conjunction() {
		if (StringUtils.isNotBlank(join)) {
			return "\n";
		} else {
			return ", ";
		}
	}

	@Override
	public String statement() {
		StringBuilder builder = new StringBuilder();
		Class<?> entityClass = getEntityClass();
		String subQuery = getSubQuery();
		String alias = getAlias();

		if (StringUtils.isNotBlank(join)) {
			builder.append(join).append(" ");
		}
		if (entityClass != null) {
			builder.append(entityClass.getName());
		} else if (StringUtils.isNotBlank(subQuery)) {
			if (Pattern.compile("\\s").matcher(subQuery).find()) {
				builder.append("( ").append(subQuery).append(" )");
			} else {
				builder.append(subQuery);
			}
		} else {
			throw new IllegalStateException();
		}
		if (StringUtils.isNotBlank(alias)) {
			builder.append(" ").append(alias);
		}
		if (StringUtils.isNotBlank(on)) {
			builder.append(" ON ").append(on);
		}
		return builder.toString();
	}

}
