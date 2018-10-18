package io.microvibe.castor.support;

import java.util.Collection;
import java.util.Map;

import org.dom4j.Element;

import io.microvibe.castor.MarshallException;

public class ComplexMarshallerSupport<T> extends AbstractComplexMarshaller<T> {

	private AbstractComplexMarshaller<T> complexMarshaller;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ComplexMarshallerSupport(Class<T> type) {
		super(type);
		if (Map.class.isAssignableFrom(type)) {
			complexMarshaller = new AnyMapComplexMarshaller(type);
		} else if (Collection.class.isAssignableFrom(type)) {
			complexMarshaller = new AnyCollectionComplexMarshaller(type);
		} else if (type.isArray()) {
			complexMarshaller = new AnyArrayComplexMarshaller(type);
		} else if (type.isEnum()) {
			complexMarshaller = new AnyEnumComplexMarshaller(type);
		} else {
			complexMarshaller = new GenericComplexMarshaller<>(type);
		}
	}

	@Override
	protected T createInstance() throws MarshallException {
		return complexMarshaller.createInstance();
	}

	@Override
	protected String defaultRootNodeName(T obj) {
		return complexMarshaller.defaultRootNodeName(obj);
	}

	@Override
	protected void innerMarshal(T obj, Element container, String nodeName) {
		complexMarshaller.innerMarshal(obj, container, nodeName);
	}

	@Override
	public T innerUnmarshalSpecifiedNode(Element container, String nodeName, T obj) {
		return complexMarshaller.innerUnmarshalSpecifiedNode(container, nodeName, obj);
	}

}
