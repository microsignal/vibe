package io.microvibe.util.castor;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import org.dom4j.Element;

public interface Marshaller<T> {

	String marshal(T o) throws MarshallException;

	String marshal(T o, String nodeName) throws MarshallException;

	void marshal(T obj, OutputStream out) throws MarshallException;

	void marshal(T obj, OutputStream out, String nodeName) throws MarshallException;

	void marshal(T obj, Writer writer) throws MarshallException;

	void marshal(T obj, Writer writer, String nodeName) throws MarshallException;

	void marshal(T obj, Element container) throws MarshallException;

	void marshal(T obj, Element container, String nodeName) throws MarshallException;

	T unmarshal(String s) throws MarshallException;

	T unmarshal(T obj, String s) throws MarshallException;

	T unmarshal(InputStream in) throws MarshallException;

	T unmarshal(T obj, InputStream in) throws MarshallException;

	T unmarshal(Reader reader) throws MarshallException;

	T unmarshal(T obj, Reader reader) throws MarshallException;

	T unmarshal(Element container) throws MarshallException;

	T unmarshal(T obj, Element container) throws MarshallException;

	T unmarshal(Element container, String nodeName) throws MarshallException;

	T unmarshal(T obj, Element container, String nodeName) throws MarshallException;
}
