package io.microvibe.booster.core.base.mybatis.example;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

/**
 * @author Qt
 * @since Jun 22, 2018
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter(AccessLevel.PACKAGE)
public class Example<T> {

	private Set<Criterition> criteria = new LinkedHashSet<>();
	private String orderByClause;

	public static <T> Builder<T> of(Class<T> entityClass, String tableAlias) {
		return new Builder(entityClass, tableAlias);
	}

	public static <T> Builder<T> of(Class<T> entityClass) {
		return new Builder(entityClass, null);
	}

	@NoArgsConstructor(access = AccessLevel.PACKAGE)
	@Getter
	@Setter(AccessLevel.PACKAGE)
	public static class Criterition {
		private String conj;
		private Set<Criterition> criteria/* = new LinkedHashSet<>()*/;
		private String protoSql;
		private String column;
		private String symbol;
		private Object value = null;
		private Object secondValue = null;

		private boolean proto = false;
		private boolean noValue = false;
		private boolean singleValue = false;
		private boolean betweenValue = false;
		private boolean listValue = false;
	}
}
