package io.microvibe.booster.core.api.model.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.microvibe.booster.core.api.ApiConstants;
import io.microvibe.booster.core.api.ApiException;
import io.microvibe.booster.core.api.ReplyCode;
import io.microvibe.booster.core.api.model.ModelBuilders;
import io.microvibe.booster.core.api.model.SearchModel;
import io.microvibe.booster.core.search.SearchKey;
import io.microvibe.booster.core.search.SearchOper;
import io.microvibe.booster.core.search.ISymbol;
import io.microvibe.booster.core.search.Searches;
import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JsonSearchModel extends JSONObjectWrapper implements SearchModel {
	private static final long serialVersionUID = 1L;

	private Conj conj = Conj.AND;
	private List<SearchModel> searches;

	private String key;
	private String op;
	private Object val;

	public JsonSearchModel() {
		super(true);
	}

	public JsonSearchModel(String key, String op, Object val) {
		this();
		this.key = key;
		this.op = op;
		this.val = val;
		put(ApiConstants.BODY_SEARCH_KEY, this.key);
		put(ApiConstants.BODY_SEARCH_OP, this.op);
		put(ApiConstants.BODY_SEARCH_VALUE, this.val);
	}

	public JsonSearchModel(String searchKey, String op) {
		this(searchKey, op, null);
	}

	public JsonSearchModel(String searchKey, ISymbol op, Object searchValue) {
		this(searchKey, op.name(), searchValue);
	}

	public JsonSearchModel(String searchKey, ISymbol op) {
		this(searchKey, op.name(), null);
	}

	public JsonSearchModel(Map<String, Object> map) {
		super(map);
		this.conj = extractConj();
		if (this.conj != null) { // 子条件组
			JSONArray jsonArray = getJSONArray(ApiConstants.BODY_SEARCH);
			List<SearchModel> list;
			if (jsonArray == null) {
				JSONObject jsonObject = getJSONObject(ApiConstants.BODY_SEARCH);
				if (jsonObject == null) {
					put(ApiConstants.BODY_SEARCH, searches = (List) new JSONArrayWrapper());
				} else {
					// parse objects
					searches = (List) new JSONArrayWrapper();
					for (Entry<String, Object> entry : entrySet()) {
						SearchKey searchKey = Searches.parseSearchKey(entry.getKey());
						searches.add(ModelBuilders.newSearchModel(searchKey.getProperty(),
							searchKey.getSymbol(), entry.getValue()));
					}
				}
			} else if (jsonArray.size() == 0) {
				put(ApiConstants.BODY_SEARCH, searches = (List) new JSONArrayWrapper());
			} else {
				// parse array
				Object first = jsonArray.get(0);
				if (first instanceof SearchModel) {
					searches = (List) jsonArray;
				} else {
					searches = (List) new JSONArrayWrapper();
					for (Object o : jsonArray) {
						if (o instanceof Map) {
							searches.add(ModelBuilders.newSearchModel((Map<String, Object>) o));
						}
					}
					put(ApiConstants.BODY_SEARCH, searches);
				}
			}
			this.key = null;
			this.op = null;
			this.val = null;
		} else { //单一条件
			searches = null;
			remove(ApiConstants.BODY_SEARCH);
			this.key = getString(ApiConstants.BODY_SEARCH_KEY);
			if (this.key == null) {
				if (this.size() == 1) {
					Iterator<Entry<String, Object>> iterator = entrySet().iterator();
					String uniKey = iterator.next().getKey();
					SearchKey searchKey = Searches.parseSearchKey(uniKey);
					this.key = searchKey.getProperty();
					this.op = searchKey.getSymbol().name();
					this.val = get(uniKey);
					iterator.remove();
					put(ApiConstants.BODY_SEARCH_KEY, this.key);
					put(ApiConstants.BODY_SEARCH_OP, this.op);
					put(ApiConstants.BODY_SEARCH_VALUE, this.val);
				} else {
					throw new ApiException(ReplyCode.RequestParseError, "search 格式体有误: " + this.toJSONString());
				}
			} else {
				String op = getString(ApiConstants.BODY_SEARCH_OP);
				if (op == null) {
					put(ApiConstants.BODY_SEARCH_OP, op = SearchOper.eq.name());
				}
				this.op = op;
				this.val = get(ApiConstants.BODY_SEARCH_VALUE);
			}
		}
	}

	private Conj extractConj() {
		try {
			String conj = StringUtils.trimToNull(getString(ApiConstants.BODY_SEARCH_CONJ));
			if (conj == null) {
				return null;
			}
			return Conj.valueOf(conj.toUpperCase());
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	@Override
	public JsonSearchModel clone() {
		return new JsonSearchModel((Map<String, Object>) super.clone());
	}

	@Override
	public Conj getConj() {
		return this.conj;
	}

	@Override
	public List<SearchModel> getSearches() {
		return searches;
	}

	@Override
	public String getKey() {
		return this.key;
	}

	@Override
	public void setKey(String searchKey) {
		put(ApiConstants.BODY_SEARCH_KEY, this.key = searchKey);
	}

	@Override
	public ISymbol getOp() {
		try {
			return Searches.symbolOf(op);
		} catch (Exception e) {
			throw new ApiException(ReplyCode.RequestParseError, e);
		}
	}

	@Override
	public void setOp(ISymbol op) {
		put(ApiConstants.BODY_SEARCH_OP, this.op = op.name());
	}

	@Override
	public Object getVal() {
		return this.val;
	}

	@Override
	public void setVal(Object searchValue) {
		put(ApiConstants.BODY_SEARCH_VALUE, this.val = searchValue);
	}

	@Override
	public <T> T getVal(Class<T> clazz) {
		return getObject(ApiConstants.BODY_SEARCH_VALUE, clazz);
	}
}
