package io.microvibe.booster.commons.utils.json;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

public class JsonUtil {

	private JsonUtil() {
	}

	public static String toJson(Object o) {
		return JsonConverter.getInstance().toJson(o);
	}

	public static <T> T toJavaObject(Class<T> type, String json) {
		return toJavaObject(type, null, json);
	}

	public static <T> T toJavaObject(Class<T> type, String field, String json) {
		T rs = null;
		JsonConverter jsonConverter = JsonConverter.getInstance();
		if (field == null) {
			rs = jsonConverter.toJavaObject(type, json);
		} else {
			JsonNode root = jsonConverter.toJsonNode(json);
			rs = jsonConverter.toJavaObject(type, root.get(field));
		}
		return rs;
	}

	public static Map<?, ?> toMap(String json) {
		return (Map<?, ?>) toJavaObject(java.util.HashMap.class, json);
	}

	public static Map<?, ?> toMap(String field, String json) {
		return (Map<?, ?>) toJavaObject(java.util.HashMap.class, field, json);
	}

	public static <T> List<T> toList(Class<T> type, String json) {
		return toList(type, null, json);
	}

	public static <T> List<T> toList(Class<T> type, String field, String json) {
		List<T> rs = null;
		JsonConverter jsonConverter = JsonConverter.getInstance();
		if (field == null) {
			rs = jsonConverter.toList(type, json);
		} else {
			JsonNode root = jsonConverter.toJsonNode(json);
			rs = jsonConverter.toList(type, root.get(field));
		}
		return rs;
	}

	public static List<java.util.HashMap> toMapList(String field, String json) {
		return toList(java.util.HashMap.class, field, json);
	}

	public static List<java.util.HashMap> toMapList(String json) {
		return toList(java.util.HashMap.class, json);
	}

	public static JsonNode toJsonNode(String json) {
		return JsonConverter.getInstance().toJsonNode(json);
	}

	/**
	 * @see #toJson(Object)
	 */
	@Deprecated
	public static String object2Json(Object o) {
		return toJson(o);
	}

	/**
	 * @see #json2Object(Class, String)
	 */
	@Deprecated
	public static <T> T json2Object(Class<T> type, String json) {
		return json2Object(type, null, json);
	}

	/**
	 * @see #json2Object(Class, String, String)
	 */
	@Deprecated
	public static <T> T json2Object(Class<T> type, String field, String json) {
		return toJavaObject(type, field, json);
	}

	/**
	 * @see #toMap(String)
	 */
	@Deprecated
	public static Map<?, ?> json2Map(String json) {
		return toMap(json);
	}

	/**
	 * @see #toMap(String, String)
	 */
	@Deprecated
	public static Map<?, ?> json2Map(String field, String json) {
		return toMap(field, json);
	}

	/**
	 * @see #toList(Class, String)
	 */
	@Deprecated
	public static List<?> json2List(Class<?> type, String json) {
		return toList(type, json);
	}

	/**
	 * @see #toList(Class, String, String)
	 */
	@Deprecated
	public static List<?> json2List(Class<?> type, String field, String json) {
		return toList(type, field, json);
	}

	/**
	 * @see #toMapList(String, String)
	 */
	@Deprecated
	public static List<?> json2MapList(String field, String json) {
		return toMapList(field, json);
	}

	/**
	 * @see #toMapList(String)
	 */
	@Deprecated
	public static List<?> json2MapList(String json) {
		return toMapList(json);
	}

	/**
	 * @see #toJsonNode(String)
	 */
	@Deprecated
	public static JsonNode json2JsonNode(String json) {
		return toJsonNode(json);
	}

}
