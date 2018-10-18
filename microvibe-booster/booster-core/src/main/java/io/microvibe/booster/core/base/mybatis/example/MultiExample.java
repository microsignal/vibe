package io.microvibe.booster.core.base.mybatis.example;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TODO
 * @author Qt
 * @since Jul 12, 2018
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter(AccessLevel.PACKAGE)
public class MultiExample<T> extends Example<T> {

	public static <T> MultiBuilder<T> of(Class<T> entityClass, String tableAlias) {
		return new MultiBuilder<>(entityClass, tableAlias);
	}

}
