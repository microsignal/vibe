package io.microvibe.booster.core.api.model.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.microvibe.booster.core.api.model.IJSONObject;
import io.microvibe.booster.core.api.tools.DataKit;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.Deque;
import java.util.Map;

public class JSONObjectWrapper extends JSONObject implements IJSONObject {

	private static final long serialVersionUID = 1L;

	public JSONObjectWrapper() {
		super();
	}

	public JSONObjectWrapper(boolean ordered) {
		super(ordered);
	}

	public JSONObjectWrapper(int initialCapacity, boolean ordered) {
		super(initialCapacity, ordered);
	}

	public JSONObjectWrapper(int initialCapacity) {
		super(initialCapacity);
	}

	public JSONObjectWrapper(Map<String, Object> map) {
		super(map);
	}

	@Override
	public JSONObjectWrapper clone() {
		return new JSONObjectWrapper((JSONObject) super.clone());
	}

	@Override
	public void putAll(Object o) {
		Object json = JSON.toJSON(o);
		if (json instanceof JSONObject) {
			putAll((JSONObject) json);
		}
	}

	@Override
	public <T> T getObject(String key, Class<T> clazz) {
		if (clazz.isArray()) {//array
			try {
				String className = clazz.getName().substring(1);
				if (className.startsWith("L")) {
					className = className.substring(1, className.length() - 1);
				}
				JSONArray jsonArray = getJSONArray(key);
				Class eleClass = Class.forName(className);
				Object array = Array.newInstance(eleClass, jsonArray.size());
				for (int i = 0; i < jsonArray.size(); i++) {
					Array.set(array, i, jsonArray.getObject(i, eleClass));
				}
				return (T) array;
			} catch (Exception e) {
				return null;
			}
		}
		try {
			Constructor<T> constructor = clazz.getDeclaredConstructor(Map.class);
			constructor.setAccessible(true);
			if (constructor != null) {
				return constructor.newInstance(getJSONObject(key));
			}
		} catch (Exception e) {
		}
		try {
			return super.getObject(key, clazz);
		} catch (RuntimeException e) {
			T o = JSONObject.parseObject(getJSONObject(key).toJSONString(), clazz);
			if (o != null) {
				return o;
			} else {
				throw e;
			}
		}
	}

	@Override
	public <T> T toJavaObject(String key, Class<T> clazz) {
		return getObject(key, clazz);
	}

	@Override
	public <T> T toJavaObject(Class<T> clazz) {
		return super.toJavaObject(clazz);
	}

	@Override
	public <T> T toValidJavaObject(Class<T> clazz) {
		T o = toJavaObject(clazz);
		DataKit.validate(o);
		return o;
	}

	@Override
	public <T> T toValidJavaObject(String key, Class<T> clazz) {
		T o = toJavaObject(key, clazz);
		DataKit.validate(o);
		return o;
	}

	@Override
	public <T> T getValidObject(String key, Class<T> clazz) {
		T o = getObject(key, clazz);
		DataKit.validate(o);
		return o;
	}

	@Override
	public JSONObject getJSONObject(String key) {
		JSONObject value = super.getJSONObject(key);
		if (value == null || value instanceof JSONObjectWrapper) {
			return value;
		} else {
			value = new JSONObjectWrapper(value);
			super.put(key, value);
			return value;
		}
	}

	@Override
	public JSONArray getJSONArray(String key) {
		JSONArray value = super.getJSONArray(key);
		if (value == null || value instanceof JSONArrayWrapper) {
			return value;
		} else {
			value = new JSONArrayWrapper(value);
			super.put(key, value);
			return value;
		}
	}

	@Override
	public Object getByKeyPath(String path) {
		Object json = this;
		Deque<String> queue = StaticJsonMethodKit.parseProperty(path);
		String key;
		while ((key = queue.poll()) != null) {
			json = StaticJsonMethodKit.get(json, key);
			if (json == null) {
				break;
			}
		}
		return json;
	}

	@Override
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
