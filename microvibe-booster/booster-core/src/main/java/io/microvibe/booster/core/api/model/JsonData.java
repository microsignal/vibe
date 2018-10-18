package io.microvibe.booster.core.api.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.parser.Feature;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.microvibe.booster.core.api.ApiConstants;
import io.microvibe.booster.core.api.ApiException;
import io.microvibe.booster.core.api.ReplyCode;
import io.microvibe.booster.core.api.model.impl.JsonBodyModel;
import io.microvibe.booster.core.api.model.impl.JsonHeadModel;
import io.microvibe.booster.core.api.tools.DataKit;
import io.microvibe.booster.core.api.validation.ApiValidator;
import io.microvibe.booster.core.api.validation.ApiValidatorImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

@Slf4j
public class JsonData implements Data, RequestData, ResponseData {

	private static final long serialVersionUID = 1L;
	private JSONObject json;
	private JsonHeadModel meta;

	private JsonData(JSONObject json) {
		init(json);
	}

	private JsonData(String str) {
		JSONObject json = null;
		try {
			json = JSON.parseObject(str, Feature.OrderedField);
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
			throw new ApiException(e, ReplyCode.RequestParseError, e.getMessage());
		}
		init(json);
	}

	public static JsonData buildByTxnCode(String txnCode) {
		return buildByTxnCode(txnCode, null);
	}

	public static JsonData buildByTxnCode(String txnCode, String body) {
		JSONObject json = new JSONObject(true);
		JSONObject head = new JSONObject(true);
		head.put(ApiConstants.TXN_CODE, txnCode);
		json.put(ApiConstants.HEAD, head);
		if (body != null) {
			try {
				json.put(ApiConstants.BODY, JSONObject.parseObject(body, Feature.OrderedField));
			} catch (Exception e) {
				log.debug(e.getMessage(), e);
				json.put(ApiConstants.BODY, body);
			}
		}
		return build(json);
	}

	public static JsonData buildByBody(JSONObject body) {
		JSONObject json = new JSONObject(true);
		json.put(ApiConstants.HEAD, new JsonHeadModel());
		if (body != null) {
			json.put(ApiConstants.BODY, body);
		}
		return build(json);
	}

	public static JsonData buildByBody(String data) {
		return build(null, data);
	}

	public static JsonData build(JSONObject head, JSONObject body) {
		JSONObject json = new JSONObject(true);
		json.put(ApiConstants.HEAD, head == null ? new JsonHeadModel() : head);
		json.put(ApiConstants.BODY, body == null ? new JsonBodyModel() : body);
		return build(json);
	}

	public static JsonData build(String head, String body) {
		JSONObject json = new JSONObject(true);
		if (StringUtils.isNotBlank(head)) {
			try {
				json.put(ApiConstants.HEAD, JSONObject.parseObject(head, Feature.OrderedField));
			} catch (Exception e) {
				log.debug(e.getMessage(), e);
				json.put(ApiConstants.HEAD, new JsonHeadModel());
			}
		} else {
			json.put(ApiConstants.HEAD, new JsonHeadModel());
		}
		if (StringUtils.isNotBlank(body)) {
			try {
				json.put(ApiConstants.BODY, JSONObject.parseObject(body, Feature.OrderedField));
			} catch (Exception e) {
				log.debug(e.getMessage(), e);
				json.put(ApiConstants.BODY, body);
			}
		} else {
			json.put(ApiConstants.BODY, new JsonBodyModel());
		}
		return build(json);
	}

	public static JsonData build(JSONObject json) {
		return new JsonData(json);
	}

	public static JsonData build(String json) {
		return new JsonData(json);
	}

	public static JsonData build() {
		return new JsonData(new JSONObject(true));
	}

	public static JsonData copy(XmlData xmlData) {
		return xmlData.getJsonData().clone();
	}

	private void init(JSONObject json) {
		try {
			JSONObject _meta_ = json.getJSONObject(ApiConstants.HEAD);
			if (_meta_ != null) {
				this.meta = new JsonHeadModel(_meta_);
			}
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
		if (this.meta == null) {
			this.meta = new JsonHeadModel();
		}
		json.put(ApiConstants.HEAD, this.meta);
		this.json = json;
	}

	@Override
	@JSONField(serialize = false)
	@JsonIgnore
	public ApiValidator getApiValidator() {
		return new ApiValidatorImpl(this);
	}

	@Override
	public JsonData clone() {
		return new JsonData((JSONObject) this.json.clone());
	}

	@Override
	public String toPrettyString() {
		return JSON.toJSONString(json, true);
	}

	@Override
	public String toString() {
		return json.toJSONString();
	}

	@Override
	public ResponseData buildResponse() {
		JSONObject json = new JSONObject(true);
		json.put(ApiConstants.HEAD, this.meta.clone());
		return new JsonData(json);
	}

	@Override
	@JSONField(name = ApiConstants.HEAD, ordinal = 0)
	@JsonProperty(value = ApiConstants.HEAD, index = 0)
	public HeadModel getHead() {
		return meta;
	}

	@Override
	public void setHead(String key, Object value) {
		meta.put(key, value);
	}

	@Override
	public Object getHead(String key) {
		return meta.get(key);
	}

	@Override
	public <T> T getHead(String key, Class<T> clazz) {
		return meta.getObject(key, clazz);
	}

	@Override
	@JSONField(serialize = false)
	@JsonIgnore
	public String getBodyAsString() {
		return json.getString(ApiConstants.BODY);
	}

	@Override
	@JSONField(serialize = false)
	@JsonIgnore
	public void setBodyAsString(String data) {
		json.put(ApiConstants.BODY, data);
	}

	@JSONField(name = ApiConstants.BODY, ordinal = 1)
	@JsonProperty(value = ApiConstants.BODY, index = 1)
	@JsonInclude(Include.NON_NULL)
	public Object getBodyProperty() {
		return json.get(ApiConstants.BODY);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	@JSONField(serialize = false)
	@JsonIgnore
	public BodyModel getBody() {
		Object body = json.get(ApiConstants.BODY);
		if (body == null || !(body instanceof JsonBodyModel)) {
			synchronized (this) {
				body = json.get(ApiConstants.BODY);
				if (body == null) {
					json.put(ApiConstants.BODY, body = new JsonBodyModel());
				} else if (body instanceof Map) {
					json.put(ApiConstants.BODY, body = new JsonBodyModel((Map) body));
				} else if (body instanceof String) {
					try {
						body = new JsonBodyModel(JSONObject.parseObject((String) body, Feature.OrderedField));
					} catch (Exception e) {
						json.put(ApiConstants.BODY, body = new JsonBodyModel());
					}
				} else {
					json.put(ApiConstants.BODY, body = new JsonBodyModel());
				}
			}
		}
		return (JsonBodyModel) body;
	}

	@Override
	@JSONField(serialize = false)
	@JsonIgnore
	public <T> T getBody(Class<T> clazz) {
		return getBody().toJavaObject(clazz);
	}

	@Override
	@JSONField(serialize = false)
	@JsonIgnore
	public <T> T getValidBody(Class<T> clazz) {
		return getBody().toValidJavaObject(clazz);
	}

	@Override
	public void setBody(String key, Object value) {
		getJsonBodyModel().put(key, value);
	}

	@Override
	public Object getBody(String key) {
		return getJsonBodyModel().get(key);
	}

	@Override
	public <T> T getBody(String key, Class<T> clazz) {
		return getJsonBodyModel().getObject(key, clazz);
	}

	@Override
	public String getHeadAsString(String key) {
		return meta.getString(key);
	}

	@Override
	public String getBodyAsString(String key) {
		return getJsonBodyModel().getString(key);
	}

	JsonBodyModel getJsonBodyModel() {
		return ((JsonBodyModel) getBody());
	}

	JSONObject getJson() {
		return json;
	}

	public <T> T getValidHead(String key, Class<T> clazz) {
		T o = getHead(key, clazz);
		DataKit.validate(o);
		return o;
	}

	public <T> T getValidBody(String key, Class<T> clazz) {
		T o = getBody(key, clazz);
		DataKit.validate(o);
		return o;
	}
}
