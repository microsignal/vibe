package io.microvibe.booster.core.api.model.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import io.microvibe.booster.core.api.ApiConstants;
import io.microvibe.booster.core.api.ApiException;
import io.microvibe.booster.core.api.ReplyCode;
import io.microvibe.booster.core.api.model.*;
import io.microvibe.booster.core.search.ISymbol;
import io.microvibe.booster.core.search.SearchKey;
import io.microvibe.booster.core.search.Searches;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unchecked", "rawtypes"})
public class JsonBodyModel extends JSONObjectWrapper implements BodyModel {
	private static final long serialVersionUID = 1L;

	public JsonBodyModel() {
		super(true);
	}

	public JsonBodyModel(Map<String, Object> map) {
		super(map);
	}

	@Override
	public JsonBodyModel clone() {
		return new JsonBodyModel((Map<String, Object>) super.clone());
	}

	@Override
	public List<SearchModel> getSearches() {
		try {
			JSONArray jsonArray = getJSONArray(ApiConstants.BODY_SEARCH);
			List<SearchModel> list;
			if (jsonArray == null) {
				JSONObject jsonObject = getJSONObject(ApiConstants.BODY_SEARCH);
				if (jsonObject == null) {
					put(ApiConstants.BODY_SEARCH, list = (List) new JSONArrayWrapper());
				} else {
					// parse objects
					list = (List) new JSONArrayWrapper();
					for (Entry<String, Object> entry : entrySet()) {
						SearchKey searchKey = Searches.parseSearchKey(entry.getKey());
						list.add(ModelBuilders.newSearchModel(searchKey.getProperty(),
							searchKey.getSymbol(), entry.getValue()));
					}
				}
			} else if (jsonArray.size() == 0) {
				put(ApiConstants.BODY_SEARCH, list = (List) new JSONArrayWrapper());
			} else {
				Object first = jsonArray.get(0);
				if (first instanceof SearchModel) {
					list = (List) jsonArray;
				} else {
					list = (List) new JSONArrayWrapper();
					for (Object o : jsonArray) {
						if (o instanceof Map) {
							list.add(ModelBuilders.newSearchModel((Map<String, Object>) o));
						}
					}
					put(ApiConstants.BODY_SEARCH, list);
				}
			}
			return list;
		} catch (Exception e) {
			throw new ApiException(e, ReplyCode.RequestParseError);
		}
	}

	@Override
	public List<SearchModel> getSearches(final String searchKey) {
		List<SearchModel> searchModelList = getSearches();
		return searchModelList.stream()
			.filter(model -> model.getKey() != null && model.getKey().equals(searchKey))
			.collect(ArrayList::new, (l, e) -> l.add(e), (a, b) -> a.addAll(b));
	}

	@Override
	public SearchModel getSearch(final String searchKey, ISymbol op) {
		List<SearchModel> searchModelList = getSearches();
		return searchModelList.stream()
			.filter(model -> model.getKey() != null && model.getKey().equals(searchKey)
				&& model.getOp().equals(op))
			.findFirst().orElse(null);
	}

	@Override
	public boolean addSearch(String searchKey, ISymbol op, Object searchValue) {
		if (StringUtils.isBlank(searchKey) || op == null) {
			return false;
		}
		List<SearchModel> searchModelList = getSearches();
		// 移除重复的key
		searchModelList.removeIf(searchModel -> searchKey.equals(searchModel.getKey())
			&& op.equals(searchModel.getOp()));
		searchModelList.add(ModelBuilders.newSearchModel(searchKey, op, searchValue));
		return true;
	}

	@Override
	public boolean clearSearch(String searchKey, ISymbol op) {
		if (op == null) {
			return clearSearch(searchKey);
		}
		List<SearchModel> searchModelList = getSearches();
		return searchModelList.removeIf(searchModel -> searchKey.equals(searchModel.getKey())
			&& op.equals(searchModel.getOp()));
	}

	@Override
	public boolean clearSearch(String searchKey) {
		List<SearchModel> searchModelList = getSearches();
		return searchModelList.removeIf(searchModel -> searchKey.equals(searchModel.getKey()));
	}

	@Override
	public void clearSearch() {
		List<SearchModel> searchModelList = getSearches();
		searchModelList.clear();
	}

	@Override
	public void addSearch(SearchModel... searchModel) {
		List<SearchModel> list;
		try {
			list = getSearches();
		} catch (Exception e) {
			put(ApiConstants.BODY_SEARCH, list = (List) new JSONArrayWrapper());
		}
		for (SearchModel o : searchModel) {
			list.add(o);
		}
	}

	@Override
	public void setSearch(List<SearchModel> searchModelList) {
		put(ApiConstants.BODY_SEARCH, searchModelList);
	}

	@Override
	public LimitModel getLimit() {
		try {
			JSONObject jsonObject = getJSONObject(ApiConstants.BODY_LIMIT);
			if (jsonObject instanceof LimitModel) {
				return (LimitModel) jsonObject;
			}
			if (jsonObject == null) {
				return null;
			}
			LimitModel limit = ModelBuilders.newLimitModel(jsonObject);
			put(ApiConstants.BODY_LIMIT, limit);
			return limit;
		} catch (Exception e) {
			throw new ApiException(e, ReplyCode.RequestParseError);
		}
	}

	@Override
	public void setLimit(LimitModel limit) {
		put(ApiConstants.BODY_LIMIT, limit);
	}

	@Override
	public void setLimit(int pageNumber, int pageSize) {
		LimitModel limit = ModelBuilders.newLimitModel();
		limit.setPageNumber(pageNumber);
		limit.setPageSize(pageSize);
		put(ApiConstants.BODY_LIMIT, limit);
	}

	@Override
	public void clearLimit() {
		remove(ApiConstants.BODY_LIMIT);
	}

	@Override
	public List<SortModel> getSorts() {
		try {
			JSONArray jsonArray = getJSONArray(ApiConstants.BODY_SORT);
			List<SortModel> list;
			if (jsonArray == null || jsonArray.size() == 0) {
				put(ApiConstants.BODY_SORT, list = (List) new JSONArrayWrapper());
				return list;
			}
			Object first = jsonArray.get(0);
			if (first instanceof SortModel) {
				return list = (List) jsonArray;
			} else {
				list = (List) new JSONArrayWrapper();
				for (Object o : jsonArray) {
					if (o instanceof Map) {
						list.add(new JsonSortModel((Map<String, Object>) o));
					}
				}
				put(ApiConstants.BODY_SORT, list);
			}
			return list;
		} catch (Exception e) {
			throw new ApiException(e, ReplyCode.RequestParseError);
		}
	}

	@Override
	public boolean addSort(String sortKey, Sort.Direction sortDirection) {
		if (StringUtils.isBlank(sortKey)) {
			return false;
		}
		if (sortDirection == null) {
			sortDirection = Sort.Direction.ASC;
		}
		List<SortModel> sortModelList = getSorts();
		sortModelList.removeIf(model -> sortKey.equals(model.getSortKey()));// 去重
		SortModel sortModel = ModelBuilders.newSortModel();
		sortModel.setSortKey(sortKey);
		sortModel.setSortDirection(sortDirection);
		sortModelList.add(sortModel);
		return true;
	}

	@Override
	public boolean clearSort(String sortKey) {
		List<SortModel> sortModelList = getSorts();
		return sortModelList.removeIf(model -> sortKey.equals(model.getSortKey()));
	}

	@Override
	public void clearSort() {
		List<SortModel> sortModelList = getSorts();
		sortModelList.clear();
	}

	@Override
	public void addSort(SortModel... sort) {
		List<SortModel> list;
		try {
			list = getSorts();
		} catch (Exception e) {
			put(ApiConstants.BODY_SORT, list = (List) new JSONArrayWrapper());
		}
		for (SortModel o : sort) {
			list.add(o);
		}
	}

	@Override
	public List<String> getSortable() {
		try {
			JSONArray jsonArray = getJSONArray(ApiConstants.BODY_SORTABLE);
			List<String> list;
			if (jsonArray == null || jsonArray.size() == 0) {
				put(ApiConstants.BODY_SORTABLE, list = (List) new JSONArrayWrapper());
				return list;
			}
			Object first = jsonArray.get(0);
			if (first instanceof String) {
				return list = (List) jsonArray;
			} else {
				list = (List) new JSONArrayWrapper();
				for (Object o : jsonArray) {
					list.add(TypeUtils.castToString(o));
				}
				put(ApiConstants.BODY_SORTABLE, list);
			}
			return list;
		} catch (Exception e) {
			throw new ApiException(ReplyCode.RequestParseError, e);
		}
	}

	@Override
	public void setSortable(List<String> sortable) {
		put(ApiConstants.BODY_SORTABLE, sortable);
	}

	@Override
	public void addSortable(String... sortable) {
		List<String> list;
		try {
			list = getSortable();
		} catch (Exception e) {
			list = new ArrayList<>();
		}
		for (String s : sortable) {
			list.add(s);
		}
		put(ApiConstants.BODY_SORTABLE, list);

	}

	@Override
	public List<?> getDataList() {
		try {
			JSONArray jsonArray = getJSONArray(ApiConstants.BODY_LIST);
			return jsonArray;
		} catch (Exception e) {
			throw new ApiException(ReplyCode.RequestParseError, e);
		}
	}

	@Override
	public void setDataList(List<?> list) {
		put(ApiConstants.BODY_LIST, list);
	}

	@Override
	public void addDataList(Object... data) {
		List list;
		try {
			list = getDataList();
		} catch (Exception e) {
			list = new ArrayList<>();
		}
		for (Object o : data) {
			list.add(o);
		}
		put(ApiConstants.BODY_LIST, list);
	}

	@Override
	public long getDataTotal() {
		return getLongValue(ApiConstants.BODY_TOTAL);
	}

	@Override
	public void setDataTotal(long total) {
		put(ApiConstants.BODY_TOTAL, total);
	}

	@Override
	public Sort getSort() {
		List<SortModel> sortModels = getSorts();
		if (sortModels == null || sortModels.size() == 0) {
			return null;
		}
		List<Order> orders = new ArrayList<>(sortModels.size());
		for (SortModel model : sortModels) {
			Order order = new Order(model.getSortDirection(), model.getSortKey());
			orders.add(order);
		}
		Sort sort = new Sort(orders);
		return sort;
	}

	@Override
	public void setSort(List<SortModel> sort) {
		put(ApiConstants.BODY_SORT, sort);
	}

	@Override
	public Pageable getPageable() {
		LimitModel limitModel = getLimit();
		if (limitModel == null) {
			return null;
		}
		int pageNumber = limitModel.getPageNumber();
		pageNumber = Math.max(0, pageNumber - 1);
		Pageable pageable = new PageRequest(pageNumber, limitModel.getPageSize(), getSort());
		return pageable;
	}


	@Override
	public Pageable getPageableWithoutSort() {
		LimitModel limitModel = getLimit();
		if (limitModel == null) {
			return null;
		}
		int pageNumber = limitModel.getPageNumber();
		pageNumber = Math.max(0, pageNumber - 1);
		Pageable pageable = new PageRequest(pageNumber, limitModel.getPageSize());
		return pageable;
	}

}
