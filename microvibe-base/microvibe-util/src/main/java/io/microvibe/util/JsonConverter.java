package io.microvibe.util;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;

import io.microvibe.util.err.JsonException;

public class JsonConverter {

	private static JsonConverter jsonConverter = new JsonConverter();
	private ObjectMapper mapper;
	private TypeFactory typeFactory;

	private JsonConverter() {
		mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		mapper.configure(JsonParser.Feature.ALLOW_MISSING_VALUES, true);
		mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
		typeFactory = TypeFactory.defaultInstance();
	}

	public static JsonConverter getInstance() {
		return jsonConverter;
	}

	public String toJson(Object object) {
		try {
			return mapper.writeValueAsString(object);
		} catch (Exception e) {
			throw new JsonException(e);
		}
	}

	public JsonNode toJsonNode(String json) {
		try {
			return mapper.readValue(json, JsonNode.class);
		} catch (Exception e) {
			throw new JsonException(e);
		}
	}

	public <T> T toJavaObject(Class<T> type, String json) {
		try {
			return mapper.readValue(json, type);
		} catch (Exception e) {
			throw new JsonException(e);
		}
	}

	public <T> T toJavaObject(Class<T> type, JsonNode json) {
		try {
			return mapper.readValue(json.toString(), type);
		} catch (Exception e) {
			throw new JsonException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> toList(Class<T> type, String json) {
		try {
			return (List<T>) mapper.readValue(json.toString(), typeFactory.constructParametricType(
					java.util.ArrayList.class, new Class[] { type }));
		} catch (Exception e) {
			throw new JsonException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> toList(Class<T> type, JsonNode json) {
		try {
			return (List<T>) mapper.readValue(json.toString(), typeFactory.constructParametricType(
					java.util.ArrayList.class, new Class[] { type }));
		} catch (Exception e) {
			throw new JsonException(e);
		}
	}
}
