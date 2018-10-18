package io.microvibe.booster.core.api.model;

import io.microvibe.booster.core.api.model.impl.*;
import io.microvibe.booster.core.search.ISymbol;

import java.util.Map;

public class ModelBuilders {

	public static HeadModel newHeadModel() {
		return new JsonHeadModel();
	}

	public static HeadModel newHeadModel(Map<String, Object> map) {
		return new JsonHeadModel(map);
	}

	public static BodyModel newBodyModel() {
		return new JsonBodyModel();
	}

	public static BodyModel newBodyModel(Map<String, Object> map) {
		return new JsonBodyModel(map);
	}

	public static SortModel newSortModel() {
		return new JsonSortModel();
	}

	public static SortModel newSortModel(Map<String, Object> map) {
		return new JsonSortModel(map);
	}

	public static LimitModel newLimitModel() {
		return new JsonLimitModel();
	}

	public static LimitModel newLimitModel(Map<String, Object> map) {
		return new JsonLimitModel(map);
	}

	public static SearchModel newSearchModel() {
		return new JsonSearchModel();
	}

	public static SearchModel newSearchModel(String searchKey, String op, Object searchValue) {
		return new JsonSearchModel(searchKey, op, searchValue);
	}

	public static SearchModel newSearchModel(String searchKey, String op) {
		return new JsonSearchModel(searchKey, op, null);
	}

	public static SearchModel newSearchModel(String searchKey, ISymbol op, Object searchValue) {
		return new JsonSearchModel(searchKey, op, searchValue);
	}

	public static SearchModel newSearchModel(String searchKey, ISymbol op) {
		return new JsonSearchModel(searchKey, op, null);
	}

	public static SearchModel newSearchModel(Map<String, Object> map) {
		return new JsonSearchModel(map);
	}

}
