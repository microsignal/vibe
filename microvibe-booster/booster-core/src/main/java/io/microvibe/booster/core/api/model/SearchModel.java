package io.microvibe.booster.core.api.model;

import io.microvibe.booster.core.search.ISymbol;

import java.util.List;
import java.util.Map;

public interface SearchModel extends java.io.Serializable, IJSONObject, Map<String, Object> {

	SearchModel clone();

	Conj getConj();

	List<SearchModel> getSearches();

	String getKey();

	void setKey(String searchKey);

	ISymbol getOp();

	void setOp(ISymbol op);

	Object getVal();

	void setVal(Object searchValue);

	<T> T getVal(Class<T> clazz);

	public static enum Conj {
		AND, OR;
	}

}
