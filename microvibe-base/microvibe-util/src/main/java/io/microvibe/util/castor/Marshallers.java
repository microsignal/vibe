package io.microvibe.util.castor;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import org.dom4j.Element;
import org.dom4j.io.OutputFormat;

import io.microvibe.util.castor.support.ComplexMarshallerSupport;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class Marshallers {

	private static Map<Class, ComplexMarshaller> complexMarshallers =
			Collections.synchronizedMap(new WeakHashMap<Class, ComplexMarshaller>());

	private Marshallers() {
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

	/**
	 * 获取内置序列化器
	 *
	 * @param type
	 * @return
	 */
	public static <T> SimpleMarshaller<T> getSimpleMarshaller(Class<T> type) {
		return Castors.getSimpleMarshaller(type);
	}

	/**
	 * 是否有内置序列化器
	 *
	 * @param type
	 * @return
	 */
	public static <T> boolean hasBuildInMarshaller(Class<T> type) {
		return getSimpleMarshaller(type) != null;
	}

	public static <T> Marshaller<T> getMarshaller(Class<T> type) {
		SimpleMarshaller<T> simpleMarshaller = getSimpleMarshaller(type);
		if (simpleMarshaller == null) {
			ComplexMarshaller<T> complexMarshaller = complexMarshallers.get(type);
			if (complexMarshaller == null) {
				complexMarshaller = new ComplexMarshallerSupport<>(type);
				complexMarshallers.put(type, complexMarshaller);
			}
			return complexMarshaller;
		}
		return simpleMarshaller;
	}

	public static <T> Marshaller<T> getMarshaller(T obj) {
		Class<T> type = (Class<T>) (obj == null ? null : obj.getClass());
		Marshaller<T> marshaller = getMarshaller(type);
		return marshaller;
	}

	public static <T> String marshal(T obj) throws MarshallException {
		return getMarshaller(obj).marshal(obj);
	}

	public static <T> String marshal(T obj, String nodeName) throws MarshallException {
		return getMarshaller(obj).marshal(obj, nodeName);
	}

	public static <T> void marshal(T obj, OutputStream out) throws MarshallException {
		getMarshaller(obj).marshal(obj, out);
	}

	public static <T> void marshal(T obj, OutputStream out, String nodeName) throws MarshallException {
		getMarshaller(obj).marshal(obj, out, nodeName);
	}

	public static <T> void marshal(T obj, Writer writer) throws MarshallException {
		getMarshaller(obj).marshal(obj, writer);
	}

	public static <T> void marshal(T obj, Writer writer, String nodeName) throws MarshallException {
		getMarshaller(obj).marshal(obj, writer, nodeName);
	}

	public static <T> void marshal(T obj, Element container) throws MarshallException {
		getMarshaller(obj).marshal(obj, container);
	}

	public static <T> void marshal(T obj, Element container, String nodeName) {
		getMarshaller(obj).marshal(obj, container, nodeName);
	}

	public static <T> T unmarshal(Class<T> clazz, String s) throws MarshallException {
		return getMarshaller(clazz).unmarshal(s);
	}

	public static <T> T unmarshal(T obj, String s) throws MarshallException {
		return getMarshaller(obj).unmarshal(obj, s);
	}

	public static <T> T unmarshal(Class<T> clazz, InputStream in) throws MarshallException {
		return getMarshaller(clazz).unmarshal(in);
	}

	public static <T> T unmarshal(T obj, InputStream in) throws MarshallException {
		return getMarshaller(obj).unmarshal(obj, in);
	}

	public static <T> T unmarshal(Class<T> clazz, Reader reader) throws MarshallException {
		return getMarshaller(clazz).unmarshal(reader);
	}

	public static <T> T unmarshal(T obj, Reader reader) throws MarshallException {
		return getMarshaller(obj).unmarshal(obj, reader);
	}

	public static <T> T unmarshal(Class<T> clazz, Element container) throws MarshallException {
		return getMarshaller(clazz).unmarshal(container);
	}

	public static <T> T unmarshal(T obj, Element container) throws MarshallException {
		return getMarshaller(obj).unmarshal(obj, container);
	}

	public static <T> T unmarshal(Class<T> clazz, Element container, String nodeName) throws MarshallException {
		return getMarshaller(clazz).unmarshal(container, nodeName);
	}

	public static <T> T unmarshal(T obj, Element container, String nodeName) {
		return getMarshaller(obj).unmarshal(obj, container, nodeName);
	}
}
