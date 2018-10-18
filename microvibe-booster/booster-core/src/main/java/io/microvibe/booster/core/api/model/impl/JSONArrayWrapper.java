package io.microvibe.booster.core.api.model.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Deque;
import java.util.List;

public class JSONArrayWrapper extends JSONArray {

	private static final long serialVersionUID = 1L;

	public JSONArrayWrapper() {
		super();
	}

	public JSONArrayWrapper(int initialCapacity) {
		super(initialCapacity);
	}

	public JSONArrayWrapper(List<Object> list) {
		super(list);
	}

	@Override
	public JSONObject getJSONObject(int index) {
		JSONObject value = super.getJSONObject(index);
		if (value == null || value instanceof JSONObjectWrapper) {
			return value;
		} else {
			value = new JSONObjectWrapper(value);
			set(index, value);
			return value;
		}
	}

	@Override
	public JSONArray getJSONArray(int index) {
		JSONArray value = super.getJSONArray(index);
		if (value == null || value instanceof JSONArrayWrapper) {
			return value;
		} else {
			value = new JSONArrayWrapper(value);
			set(index, value);
			return value;
		}
	}

	public Object getByKeyPath(String path) {
		Deque<String> queue = StaticJsonMethodKit.parseProperty(path);
		Object json = this;
		String key;
		while ((key = queue.poll()) != null) {
			json = StaticJsonMethodKit.get(json, key);
			if (json == null) {
				break;
			}
		}
		return json;
	}

	@SuppressWarnings("unchecked")
	public <T> T getByKeyPath(String path, Class<T> clazz) {
		Object o = getByKeyPath(path);
		if (o == null) {
			return (T) o;
		}
		if (clazz.isAssignableFrom(o.getClass())) {
			return (T) o;
		}
		if (o instanceof JSON) {
			return ((JSON) o).toJavaObject(clazz);
		}
		return (T) o;
	}
}
