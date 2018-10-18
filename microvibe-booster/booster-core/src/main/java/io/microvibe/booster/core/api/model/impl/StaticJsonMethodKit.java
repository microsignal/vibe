package io.microvibe.booster.core.api.model.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;

public class StaticJsonMethodKit {

	static Object get(Object json, String key) {
		if (json == null) {
			return null;
		}
		if (json instanceof JSONArray) {
			if (!key.matches("^\\d+$")) {
				throw new IllegalArgumentException(key);
			}
			JSONArray ja = (JSONArray) json;
			return wrapJSON(ja.get(Integer.parseInt(key)));
		} else if (json instanceof JSONObject) {
			JSONObject jo = (JSONObject) json;
			return wrapJSON(jo.get(key));
		}
		return null;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	static Object wrapJSON(Object o) {
		if (o == null) {
			return null;
		}
		if (o instanceof JSONArrayWrapper) {
			return o;
		} else if (o instanceof List) {
			return new JSONArrayWrapper((List) o);
		} else if (o instanceof JSONObjectWrapper) {
			return o;
		} else if (o instanceof Map) {
			return new JSONObjectWrapper((Map) o);
		} else {
			o = JSON.toJSON(o);
			if (o instanceof List) {
				return new JSONArrayWrapper((List) o);
			} else if (o instanceof Map) {
				return new JSONObjectWrapper((Map) o);
			}
		}
		return o;
	}

	static Deque<String> parseProperty(String property) {
		char[] charArray = property.toCharArray();
		StringBuilder sb = new StringBuilder();
		boolean escape = false;
		boolean bracket = false;
		Deque<String> queue = new ArrayDeque<>();
		for (char c : charArray) {
			if (escape) {
				sb.append(c);
				escape = false;
				continue;
			} else if (c == '\\') {
				escape = true;
				continue;
			}
			if (bracket) {
				if (c == ']') {
					bracket = false;
					if (sb.length() > 0) {
						queue.add(sb.toString());
						sb.delete(0, sb.length());
					}
					continue;
				}
			} else {
				if (c == '.') {
					if (sb.length() > 0) {
						queue.add(sb.toString());
						sb.delete(0, sb.length());
					}
					continue;
				} else if (c == '[') {
					bracket = true;
					if (sb.length() > 0) {
						queue.add(sb.toString());
						sb.delete(0, sb.length());
					}
					continue;
				} else if (c == ']') {
					throw new IllegalArgumentException(property);
				}
			}
			sb.append(c);
		}
		if (escape || bracket) {
			throw new IllegalArgumentException(property);
		}
		if (sb.length() > 0) {
			queue.add(sb.toString());
		}
		return queue;
	}
}
