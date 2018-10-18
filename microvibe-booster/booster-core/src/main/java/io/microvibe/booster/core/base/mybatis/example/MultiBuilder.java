package io.microvibe.booster.core.base.mybatis.example;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * TODO
 * @author Qt
 * @since Jul 12, 2018
 */
@Getter(AccessLevel.PROTECTED)
public class MultiBuilder<T> extends Builder<T> {

	private MultiExample<T> example;
	private final String table;
	private final String tableAlias;

	MultiBuilder(Class<T> entityClass, String tableAlias) {
		super(entityClass, tableAlias);
		if (StringUtils.isBlank(getTableAlias())) {
			throw new IllegalArgumentException("table alias is required!");
		}
		example = (MultiExample<T>) super.getExample();
		this.table = getEntityMetaData().getTableName();
		this.tableAlias = getTableAlias();
	}

	@Override
	protected MultiExample<T> createExample() {
		return new MultiExample<>();
	}

	@Override
	public Example<T> build() {
		return example;
	}
}
