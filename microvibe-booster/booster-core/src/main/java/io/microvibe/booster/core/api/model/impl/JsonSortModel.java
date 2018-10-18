package io.microvibe.booster.core.api.model.impl;

import io.microvibe.booster.core.api.ApiConstants;
import io.microvibe.booster.core.api.ApiException;
import io.microvibe.booster.core.api.ReplyCode;
import io.microvibe.booster.core.api.model.SortModel;
import org.springframework.data.domain.Sort.Direction;

import java.util.Map;

public class JsonSortModel extends JSONObjectWrapper implements SortModel {
	private static final long serialVersionUID = 1L;

	public JsonSortModel() {
		super(true);
	}

	public JsonSortModel(Map<String, Object> map) {
		super(map);
		String sortKey = getSortKey();
		if(sortKey == null){
			if(size() == 1){
				Entry<String, Object> entry = entrySet().iterator().next();
				Object val = entry.getValue();
				setSortKey(entry.getKey());
				if(val != null) {
					setSortDirection(Direction.fromStringOrNull(val.toString().trim()));
				}
			}else{
				throw new ApiException(ReplyCode.RequestParseError, "sort 格式体有误: " + this.toJSONString());
			}
		}
	}

	@Override
	public JsonSortModel clone() {
		return new JsonSortModel((Map<String, Object>) super.clone());
	}

	@Override
	public String getSortKey() {
		return getString(ApiConstants.BODY_SORT_KEY);
	}

	@Override
	public Direction getSortDirection() {
		String dir = getString(ApiConstants.BODY_SORT_DIRECTION);
		if (dir == null) {
			dir = Direction.ASC.name();
			put(ApiConstants.BODY_SORT_DIRECTION, dir);
		}
		return Direction.fromStringOrNull(dir);
	}

	@Override
	public void setSortKey(String sortKey) {
		put(ApiConstants.BODY_SORT_KEY, sortKey);
	}

	@Override
	public void setSortDirection(Direction sortDirection) {
		put(ApiConstants.BODY_SORT_DIRECTION, sortDirection.name());
	}

}
