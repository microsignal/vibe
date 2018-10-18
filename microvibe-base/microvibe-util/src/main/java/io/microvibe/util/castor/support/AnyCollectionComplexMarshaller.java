package io.microvibe.util.castor.support;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.LinkedBlockingDeque;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import io.microvibe.util.StringUtil;
import io.microvibe.util.castor.Constants;
import io.microvibe.util.castor.MarshallException;
import io.microvibe.util.castor.Marshallers;

@SuppressWarnings("rawtypes")
public class AnyCollectionComplexMarshaller<T extends Collection> extends AbstractComplexMarshaller<T> {

	private String xName;

	public AnyCollectionComplexMarshaller(Class<T> type) {
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
			nodeName = Constants.OBJECT_NODE_NAME;
		}

		if (handleMarshalledRef(obj, container, nodeName)) {
			return;
		}
		cacheMarshalledObj(obj, container, nodeName);

		for (Object iObj : obj) {
			Element node = DocumentHelper.createElement(nodeName);
			container.add(node);
			Marshallers.marshal(iObj, node, null);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected T createInstance() throws MarshallException {
		try {
			T t = type.newInstance();
			return t;
		} catch (InstantiationException | IllegalAccessException e) {
			if (type.isAssignableFrom(ArrayList.class)) {
				return (T) new ArrayList();
			} else if (type.isAssignableFrom(HashSet.class)) {
				return (T) new HashSet();
			} else if (type.isAssignableFrom(TreeSet.class)) {
				return (T) new TreeSet();
			} else if (type.isAssignableFrom(ArrayDeque.class)) {
				return (T) new ArrayDeque();
			} else if (type.isAssignableFrom(LinkedBlockingDeque.class)) {
				return (T) new LinkedBlockingDeque();
			} else {
				throw new MarshallException(e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public T innerUnmarshalSpecifiedNode(Element container, String nodeName, T obj) {
		nodeName = StringUtil.trimToNull(nodeName);
		if (nodeName == null) {
			nodeName = Constants.OBJECT_NODE_NAME;
		}

		Object ref = findUnmarshalledRef(container, nodeName);
		if (ref != null) {
			return (T) ref;
		}
		cacheUnmarshalledObj(container, nodeName, obj);

		List<Element> entryList = container.elements(nodeName);
		for (Element entry : entryList) {
			Object iObj = findUnmarshalledRef(entry, nodeName);
			if (iObj == null) {
				// 由于无法得知集合容器内的元素类型，不能作进一步的精确转换
				iObj = StringUtil.coalesce(entry.getTextTrim(), entry.attributeValue(Constants.VALUE_NODE_NAME));
			}
			obj.add(iObj);
		}

		return obj;
	}

}
