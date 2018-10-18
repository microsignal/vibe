package io.microvibe.booster.core.base.search.builder.support;

import io.microvibe.booster.core.base.search.builder.QLStatement;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.regex.Pattern;

@Getter
@Setter
public class Table implements QLStatement {

	private static ThreadLocal<Deque<List<Table>>> context = ThreadLocal.withInitial(() -> new ArrayDeque<>());

	private Class<?> entityClass;
	private String subQuery;
	private String alias;

	public Table() {
	}

	public Table(Class<?> entityClass, String alias) {
		this();
		this.entityClass = entityClass;
		this.alias = alias;
	}

	public Table(String subQuery, String alias) {
		this();
		this.subQuery = subQuery;
		this.alias = alias;
	}

	public String conjunction() {
		return ", ";
	}

	@Override
	public String statement() {
		StringBuilder builder = new StringBuilder();
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
		return builder.toString();
	}

	static void buildContext(List<Table> tables) {
		context.get().push(tables);
	}

	static List<Table> getContext() {
		return context.get().peek();
	}

	static void clearContext() {
		context.get().pop();
	}

}
