package io.microvibe.booster.core.api.model;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.microvibe.booster.core.api.ApiConstants;
import io.microvibe.booster.core.api.ApiException;
import io.microvibe.booster.core.api.ReplyCode;
import io.microvibe.booster.core.api.tools.XmlToolKit;
import io.microvibe.booster.core.api.validation.ApiValidator;
import io.microvibe.booster.core.api.validation.ApiValidatorImpl;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public class XmlData implements Data, RequestData, ResponseData {
	private static final long serialVersionUID = 1L;

	public static XmlData buildByTxnCode(String txnCode) {
		return buildByTxnCode(txnCode, null);
	}

	public static XmlData buildByTxnCode(String txnCode, String data) {
		return build(JsonData.buildByTxnCode(txnCode, data).getJson());
	}

	public static XmlData buildByBody(JSONObject data) {
		return build(JsonData.buildByBody(data).getJson());
	}

	public static XmlData buildByBody(String data) {
		return build(JsonData.buildByBody(data).getJson());
	}

	public static XmlData build(JSONObject head, JSONObject body) {
		return build(JsonData.build(head, body).getJson());
	}

	public static XmlData build(String head, String body) {
		return build(JsonData.build(head, body).getJson());
	}

	public static XmlData build(JSONObject json) {
		return new XmlData(json);
	}

	public static XmlData build(String json) {
		return new XmlData(json);
	}

	public static XmlData build() {
		return new XmlData(new JSONObject(true));
	}

	public static XmlData copy(JsonData jsonData) {
		return new XmlData(jsonData.clone().getJson());
	}

	private JsonData jsonData;

	private XmlData(JSONObject json) {
		jsonData = JsonData.build(json);
	}

	private XmlData(String str) {
		Element root = null;
		try {
			StringReader sr = new StringReader(str);
			SAXReader reader = new SAXReader();
			Document doc = reader.read(sr);
			root = doc.getRootElement();
		} catch (DocumentException e) {
			throw new ApiException(ReplyCode.RequestParseError, e);
		}
		JSONObject json = XmlToolKit.asJsonObject(root);
		jsonData = JsonData.build(json);
	}

	JsonData getJsonData() {
		return jsonData;
	}

	@Override
	@JSONField(serialize = false)
	@JsonIgnore
	public ApiValidator getApiValidator() {
		return new ApiValidatorImpl(this);
	}

	@Override
	public XmlData clone() {
		return new XmlData(this.jsonData.clone().getJson());
	}

	@Override
	public String toPrettyString() {
		try {
			return XmlToolKit.asXml(this.jsonData.getJson(),true);
		} catch (Exception e) {
			throw new ApiException(ReplyCode.ResponseFormatError, e);
		}
	}

	@Override
	public String toString() {
		try {
			return XmlToolKit.asXml(this.jsonData.getJson());
			/*StringWriter writer = new StringWriter();
			XMLWriter xmlWriter = new XMLWriter(writer);
			JSONObject json = this.jsonData.getJson();
			Element root = XmlToolKit.asXmlElement(json, ApiConstants.ROOT);
			xmlWriter.write(root);
			String xml = writer.toString();
			return xml;*/
		} catch (Exception e) {
			throw new ApiException(ReplyCode.ResponseFormatError, e);
		}
	}

	@Override
	public ResponseData buildResponse() {
		JSONObject json = new JSONObject(true);
		json.put(ApiConstants.HEAD, (jsonData.getHead()).clone());
		return new XmlData(json);
	}

	@Override
	@JSONField(name = ApiConstants.HEAD, ordinal = 0)
	@JsonProperty(value = ApiConstants.HEAD, index = 0)
	public HeadModel getHead() {
		return jsonData.getHead();
	}

	@Override
	public void setHead(String key, Object value) {
		jsonData.setBody(key, value);
	}

	@Override
	public Object getHead(String key) {
		return jsonData.getHead(key);
	}

	@Override
	public <T> T getHead(String key, Class<T> clazz) {
		return jsonData.getHead(key, clazz);
	}

	@Override
	@JSONField(serialize = false)
	@JsonIgnore
	public void setBodyAsString(String data) {
		jsonData.setBodyAsString(data);
	}

	@Override
	@JSONField(serialize = false)
	@JsonIgnore
	public String getBodyAsString() {
		return jsonData.getBodyAsString();
	}

	@JSONField(name = ApiConstants.BODY, ordinal = 1)
	@JsonProperty(value = ApiConstants.BODY, index = 1)
	public Object getBodyProperty() {
		return jsonData.getJson().get(ApiConstants.BODY);
	}

	@Override
	@JSONField(serialize = false)
	@JsonIgnore
	public BodyModel getBody() {
		return jsonData.getBody();
	}

	@Override
	@JSONField(serialize = false)
	@JsonIgnore
	public <T> T getBody(Class<T> clazz) {
		return jsonData.getBody(clazz);
	}

	@Override
	@JSONField(serialize = false)
	@JsonIgnore
	public <T> T getValidBody(Class<T> clazz) {
		return jsonData.getValidBody(clazz);
	}

	@Override
	public void setBody(String key, Object value) {
		jsonData.setBody(key, value);
	}

	@Override
	public Object getBody(String key) {
		return jsonData.getBody(key);
	}

	@Override
	public <T> T getBody(String key, Class<T> clazz) {
		return jsonData.getBody(key, clazz);
	}

	@Override
	public String getHeadAsString(String key) {
		return jsonData.getHeadAsString(key);
	}

	@Override
	public String getBodyAsString(String key) {
		return jsonData.getBodyAsString(key);
	}

	public <T> T getValidHead(String key, Class<T> clazz) {
		return jsonData.getValidHead(key, clazz);
	}

	public <T> T getValidBody(String key, Class<T> clazz) {
		return jsonData.getValidBody(key, clazz);
	}
}
