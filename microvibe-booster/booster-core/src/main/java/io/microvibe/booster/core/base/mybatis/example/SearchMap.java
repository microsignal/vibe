package io.microvibe.booster.core.base.mybatis.example;

import io.microvibe.booster.commons.utils.ReflectionUtils;
import io.microvibe.booster.core.search.ISymbol;
import io.microvibe.booster.core.search.SearchKey;
import io.microvibe.booster.core.search.Searches;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Qt
 * @since Aug 30, 2018
 */
public interface SearchMap extends Map<String, Object>, Serializable {
	static SearchMap create() {
		return new SearchMapImpl();
	}

	@Override
	Object put(String searchKey, Object searchValue);

	default Object put(String searchKey, ISymbol op, Object searchValue) {
		if (op == null) {
			return this.put(searchKey, searchValue);
		} else {
			SearchKey key = Searches.toSearchKey(searchKey, op);
			return this.put(key.toString(), key.getSymbol().repair(searchValue));
		}
	}

	default <T, R> Object put(ReflectionUtils.SerializableFunction<T, R> getter, ISymbol op, Object searchValue) {
		String searchKey = ReflectionUtils.getPropertyName(getter);
		return this.put(searchKey, op, searchValue);
	}

	default <R> Object put(ReflectionUtils.SerializableConsumer<R> getter, ISymbol op, Object searchValue) {
		String searchKey = ReflectionUtils.getPropertyName(getter);
		return this.put(searchKey, op, searchValue);
	}

}
