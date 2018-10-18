package io.microvibe.booster.core.api.tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import io.microvibe.booster.core.api.ApiConstants;
import io.microvibe.booster.core.base.mybatis.configuration.PersistentEnhancerBuilder;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class XmlToolKit {

	private static JSONObject parseJSONObject(String json) {
		return JSON.parseObject(json, Feature.OrderedField);
	}

	public static String asXml(String json) {
		return asXml(parseJSONObject(json));
	}

	public static String asXml(String json, String rootName) {
		return asXml(parseJSONObject(json), rootName);
	}

	public static String asXml(String json, boolean pretty) {
		return asXml(parseJSONObject(json), pretty);
	}

	public static String asXml(String json, String rootName, boolean pretty) {
		return asXml(parseJSONObject(json), rootName, pretty);
	}

	public static String asXml(JSONObject data) {
		return asXml(data, ApiConstants.ROOT);
	}

	public static String asXml(JSONObject data, boolean pretty) {
		return asXml(data, ApiConstants.ROOT, pretty);
	}

	public static String asXml(JSONObject data, String rootName) {
		return asXml(data, rootName, false);
	}

	public static String asXml(JSONObject data, String rootName, boolean pretty) {
		try {
			StringWriter writer = new StringWriter();
			XMLWriter xmlWriter;
			if (pretty) {
				xmlWriter = new XMLWriter(writer, createPrettyPrint());
			} else {
				xmlWriter = new XMLWriter(writer);
			}
			Element root = asXmlElement(data, rootName);
			xmlWriter.write(root);
			String xml = writer.toString();
			return xml;
		} catch (IOException e) {
			throw new IllegalArgumentException(data.toString(), e);
		}
	}

	public static Element asXmlElement(JSONObject data) {
		return asXmlElement(data, ApiConstants.ROOT);
	}

	public static Element asXmlElement(JSONObject data, String rootName) {
		Element root = DocumentHelper.createElement(rootName);
		castToElement(data, root);
		return root;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static void castToElement(JSONObject data, Element element) {
		Set<String> keys = data.keySet();
		for (String key : keys) {
			Object value = data.get(key);
			value = JSON.toJSON(value);
			if (value == null) {
				Element sub = DocumentHelper.createElement(key);
				element.add(sub);
			} else if (value.getClass().isEnum()) {
				Element sub = DocumentHelper.createElement(key);
				sub.setText(((Enum<?>) value).name());
				element.add(sub);
			} else if (ParserConfig.isPrimitive(value.getClass())) {
				Element sub = DocumentHelper.createElement(key);
				sub.setText(value.toString());
				element.add(sub);
			} else if (value instanceof JSONObject) {
				Element sub = DocumentHelper.createElement(key);
				castToElement((JSONObject) value, sub);
				element.add(sub);
			} else if (value instanceof Map) {
				Element sub = DocumentHelper.createElement(key);
				castToElement(new JSONObject((Map) value), sub);
				element.add(sub);
			} else if (value instanceof JSONArray) {
				castToElement((JSONArray) value, element, key);
			} else if (value instanceof Collection) {
				JSONArray arr = new JSONArray();
				arr.addAll((Collection<?>) value);
				castToElement(arr, element, key);
			} else if (value.getClass().isArray()) {
				JSONArray arr = new JSONArray();
				int len = Array.getLength(value);
				for (int i = 0; i < len; i++) {
					arr.add(Array.get(value, i));
				}
				castToElement(arr, element, key);
			} else {
				try {
					System.out.println(key);
					JSONObject jsonObject = data.getJSONObject(key);
					System.out.println(jsonObject);
					Element sub = DocumentHelper.createElement(key);
					castToElement(jsonObject, sub);
					element.add(sub);
				} catch (Exception e) {
					System.out.println(e);
					try {
						JSONArray jsonArray = data.getJSONArray(key);
						castToElement(jsonArray, element, key);
					} catch (Exception e1) {
						System.out.println(e1);
						Element sub = DocumentHelper.createElement(key);
						sub.setText(data.getString(key));
						element.add(sub);
					}
				}
			}
		}
	}

	private static void castToElement(JSONArray jsonArray, Element element, String key) {
		int size = jsonArray.size();
		for (int i = 0; i < size; i++) {
			Element sub = DocumentHelper.createElement(key);
			try {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				castToElement(jsonObject, sub);
			} catch (ClassCastException e) {
				sub.setText(jsonArray.getString(i));
			}
			element.add(sub);
		}
	}

	public static String asJson(String xml) {
		return asJsonObject(xml).toJSONString();
	}

	public static String asJson(Element element) {
		return asJsonObject(element).toJSONString();
	}

	public static JSONObject asJsonObject(String xml) {
		Element root = getRootElement(xml);
		JSONObject data = asJsonObject(root);
		return data;
	}

	@SuppressWarnings("unchecked")
	public static JSONObject asJsonObject(Element element) {
		JSONObject jsonObj = new JSONObject(true);
		List<Element> subs = element.elements();
		for (Element sub : subs) {
			if (sub.isTextOnly()) {
				String key = sub.getName();
				Object curr = jsonObj.get(key);
				if (curr == null) {
					jsonObj.put(sub.getName(), sub.getTextTrim());
				} else if (curr instanceof JSONArray) {
					((JSONArray) curr).add(sub.getTextTrim());
				} else {
					JSONArray subArr = new JSONArray();
					subArr.add(curr);
					subArr.add(sub.getTextTrim());
					jsonObj.put(key, subArr);
				}
			} else {
				String key = sub.getName();
				Object curr = jsonObj.get(key);
				JSONObject subObj = asJsonObject(sub);
				if (curr == null) {
					jsonObj.put(key, subObj);
				} else if (curr instanceof JSONObject) {
					JSONArray subArr = new JSONArray();
					subArr.add(curr);
					subArr.add(subObj);
					jsonObj.put(key, subArr);
				} else if (curr instanceof JSONArray) {
					((JSONArray) curr).add(subObj);
				}
			}
		}
		return jsonObj;
	}

	public static Element getRootElement(String xml) {
		try {
			StringReader sr = new StringReader(xml);
			SAXReader reader = new SAXReader();
			Document doc = reader.read(sr);
			Element root = doc.getRootElement();
			return root;
		} catch (DocumentException e) {
			throw new IllegalArgumentException(xml, e);
		}
	}

	public static OutputFormat createPrettyPrint() {
		// OutputFormat format= OutputFormat.createPrettyPrint();
		OutputFormat format = new OutputFormat();
		format.setIndentSize(2);
		format.setIndent("\t");
		format.setNewlines(true);
		format.setTrimText(true);
		format.setPadText(true);
		format.setNewLineAfterDeclaration(false);
		return format;
	}

	public static SAXReader createSAXReader() throws SAXException {
		SAXReader reader = new SAXReader();
		reader.setEntityResolver(new PersistentEnhancerBuilder.NoOpEntityResolver());
		reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		return reader;
	}
}

