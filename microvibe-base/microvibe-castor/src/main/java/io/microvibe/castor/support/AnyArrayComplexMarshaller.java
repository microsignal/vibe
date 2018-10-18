package io.microvibe.castor.support;

import java.lang.reflect.Array;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import io.microvibe.util.StringUtil;
import io.microvibe.castor.ArrayCastors;
import io.microvibe.castor.Constants;
import io.microvibe.castor.MarshallException;
import io.microvibe.castor.Marshaller;
import io.microvibe.castor.Marshallers;

public class AnyArrayComplexMarshaller<T> extends AbstractComplexMarshaller<T> {

	private String arrayXName;

	public AnyArrayComplexMarshaller(Class<T> type) {
		super(type);
		try {
			Class<?> arrayElementType = ArrayCastors.getArrayElementType(type);
			arrayXName = StringUtil.formatNameAsXmlStyle(StringUtil.formatInstanceName(arrayElementType))
					.concat("-").concat(Constants.ARRAY_NODE_NAME);
		} catch (ClassNotFoundException e) {
			arrayXName = Constants.OBJECT_NODE_NAME.concat("-").concat(Constants.ARRAY_NODE_NAME);
		}
	}

	@Override
	protected String defaultRootNodeName(T obj) {
		return arrayXName;
	}

	@Override
	protected void innerMarshal(T obj, Element container, String nodeName) {
		nodeName = StringUtil.trimToNull(nodeName);
		if (nodeName == null) {
			nodeName = Constants.ARRAY_NODE_NAME;
		}

		if (handleMarshalledRef(obj, container, nodeName)) {
			return;
		}
		cacheMarshalledObj(obj, container, nodeName);

		int length = Array.getLength(obj);
		for (int i = 0; i < length; i++) {
			Object iObj = Array.get(obj, i);
			Element node = DocumentHelper.createElement(nodeName);
			container.add(node);
			Marshallers.marshal(iObj, node, null);
		}
	}

	@Override
	protected T createInstance() throws MarshallException {
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public T innerUnmarshalSpecifiedNode(Element container, String nodeName, T obj) {
		nodeName = StringUtil.trimToNull(nodeName);
		if (nodeName == null) {
			nodeName = Constants.ARRAY_NODE_NAME;
		}

		Object ref = findUnmarshalledRef(container, nodeName);
		if (ref != null) {
			return (T) ref;
		}

		try {
			List<Element> entryList = container.elements(nodeName);
			int dimensionsLength = ArrayCastors.getDimensionsLength(type);
			Class arrayElementType = ArrayCastors.getArrayElementType(type);
			if (obj == null) {
				int[] dimensions = (int[]) Array.newInstance(int.class, dimensionsLength);
				Array.setInt(dimensions, 0, entryList.size());
				obj = (T) Array.newInstance(arrayElementType, dimensions);
			}

			cacheUnmarshalledObj(container, nodeName, obj);

			int i = 0;
			for (Element entry : entryList) {
				Object iObj = findUnmarshalledRef(entry, nodeName);
				if (iObj == null) {
					if (dimensionsLength > 1) {
						int[] dimensions = (int[]) Array.newInstance(int.class, dimensionsLength - 1);
						Array.setInt(dimensions, 0, 0);
						iObj = Array.newInstance(arrayElementType, dimensions);
						Marshaller marshaller = Marshallers.getMarshaller(iObj.getClass());
						iObj = marshaller.unmarshal(entry, nodeName);
					} else {
						Marshaller marshaller = Marshallers.getMarshaller(arrayElementType);
						iObj = marshaller.unmarshal(entry, (String)null);
					}
				}
				Array.set(obj, i, iObj);
				i++;
			}

		} catch (ClassNotFoundException e) {
			throw new MarshallException(e);
		}
		return obj;
	}
}
