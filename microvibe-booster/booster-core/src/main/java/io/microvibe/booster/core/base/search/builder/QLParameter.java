package io.microvibe.booster.core.base.search.builder;

import com.alibaba.fastjson.JSONObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class QLParameter {

	private final String key;
	private final Object value;

	@Override
	public String toString() {
		return JSONObject.toJSONString(this);
	}
}
