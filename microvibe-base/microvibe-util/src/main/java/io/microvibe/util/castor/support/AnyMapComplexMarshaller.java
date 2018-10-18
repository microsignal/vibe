package io.microvibe.util.castor.support;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import io.microvibe.util.StringUtil;
import io.microvibe.util.castor.Constants;
import io.microvibe.util.castor.MarshallException;
import io.microvibe.util.castor.Marshallers;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class AnyMapComplexMarshaller<T extends Map> extends AbstractComplexMarshaller<T> {

	private String xName;

	public AnyMapComplexMarshaller(Class<T> type) {
		super(type);
		xName = StringUtil.formatNameAsXmlStyle(StringUtil.formatInstanceName(type));
	}

	@Override
	protected String defaultRootNodeName(T obj) {
		return xName;
	}

	@Override
	protected void innerMarshal(T obj, Element container, String nodeName) {
		nodeName = StringUtil.trimToNull(nodeName);
		if (nodeName == null) {
			nodeName = Constants.ENTRY_NODE_NAME;
		}

		if (handleMarshalledRef(obj, container, nodeName)) {
			return;
		}
		cacheMarshalledObj(obj, container, nodeName);

		Map map = (Map) obj;
		Set<Entry> entrySet = map.entrySet();
		for (Entry entry : entrySet) {
			Object key = entry.getKey();
			Object value = entry.getValue();
			Element entryElement = DocumentHelper.createElement(nodeName);
			container.add(entryElement);
			Marshallers.marshal(key, entryElement, Constants.KEY_NODE_NAME);
			Marshallers.marshal(value, entryElement, Constants.VALUE_NODE_NAME);
		}
	}

	@Override
	protected T createInstance() throws MarshallException {
		try {
			T t = type.newInstance();
			return t;
		} catch (InstantiationException | IllegalAccessException e) {
			if (type.isAssignableFrom(LinkedHashMap.class)) {
				return (T) new LinkedHashMap<>();
			} else if (type.isAssignableFrom(ConcurrentHashMap.class)) {
				return (T) new ConcurrentHashMap<>();
			} else {
				throw new MarshallException(e);
			}
		}
	}

	@Override
	public T innerUnmarshalSpecifiedNode(Element container, String nodeName, T obj) {
		nodeName = StringUtil.trimToNull(nodeName);
		if (nodeName == null) {
			nodeName = Constants.ENTRY_NODE_NAME;
		}

		Object ref = findUnmarshalledRef(container, nodeName);
		if (ref != null) {
			return (T) ref;
		}
		cacheUnmarshalledObj(container, nodeName, obj);

		List<Element> entryList = container.elements(nodeName);

		for (Element entry : entryList) {
			Object key = findUnmarshalledRef(entry, Constants.KEY_NODE_NAME);
			if (key == null) {
				List<Element> xKeyList = entry.elements(Constants.KEY_NODE_NAME);
				int xKeyCount = xKeyList.size();
				if (xKeyCount > 0) {
					Element xKey = xKeyList.get(0);
					if (xKeyCount == 1 && xKey.isTextOnly() && xKey.attributeCount() == 0) {
						key = xKey.getTextTrim();
					} else {
						key = Marshallers.getMarshaller(Map.class)
								.unmarshal(new LinkedHashMap<>(), entry, Constants.KEY_NODE_NAME);
					}
				} else {
					key = entry.attributeValue(Constants.KEY_NODE_NAME);
				}
			}
			if (key != null) {
				Object value = findUnmarshalledRef(entry, Constants.VALUE_NODE_NAME);
				if (value == null) {
					List<Element> xValueList = entry.elements(Constants.VALUE_NODE_NAME);
					int xValueCount = xValueList.size();
					if (xValueCount > 0) {
						Element xValue = xValueList.get(0);
						if (xValueCount == 1 && xValue.isTextOnly() && xValue.attributeCount() == 0) {
							value = xValue.getTextTrim();
						} else {
							value = Marshallers.getMarshaller(Map.class)
									.unmarshal(new LinkedHashMap<>(), entry, Constants.VALUE_NODE_NAME);
						}
					} else {
						value = entry.attributeValue(Constants.VALUE_NODE_NAME);
					}
				}
				if (value != null) {
					obj.put(key, value);
				}
			}
		}
		return obj;
	}

}
