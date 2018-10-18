package io.microvibe.castor.support;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.Collection;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import io.microvibe.util.io.IOUtil;
import io.microvibe.castor.Constants;
import io.microvibe.castor.MarshallException;
import io.microvibe.castor.MarshallerEnv;
import io.microvibe.castor.Marshallers;
import io.microvibe.castor.SimpleMarshaller;

public abstract class AbstractMarshallableCastor<T> extends AbstractCastor<T> implements SimpleMarshaller<T> {

	public AbstractMarshallableCastor(Class<T> type) {
		super(type);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <pre>
	 * 1.如果对象本身就是该类型，返回之
	 * 2.如果对象是一个数组或集合，则只取其第一个元素，递归调用本方法
	 * </pre>
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public final T cast(Object orig) {
		T t = null;
		if (orig != null) {
			if (type.isAssignableFrom(orig.getClass())) {
				t = (T) orig;
			} else if (orig.getClass().isArray()) {
				if (Array.getLength(orig) > 0) {
					t = this.cast(Array.get(orig, 0));
				}
			} else if (Collection.class.isAssignableFrom(orig.getClass())) {
				final Collection c = (Collection) orig;
				if (c.size() > 0) {
					t = this.cast(c.iterator().next());
				}
			} else {
				if (orig instanceof String) {
					t = fromString((String) orig);
				}
				if (t == null) {
					t = castFromBasic(orig);
				}
				if (t == null) {
					t = fromString(orig.toString());
				}
			}
		}
		return t;
	}

	/**
	 * 从一个非数组或集合的基本类型对象转换为指定类型的对象
	 *
	 * @param orig
	 *            原基本类型对象
	 * @return 新类型对象
	 */
	protected abstract T castFromBasic(Object orig);

	protected abstract T fromString(String s);

	protected String toString(T t) {
		return t == null ? "" : t.toString();
	}

	/*@Override
	public String marshal(T o) {
		return o.toString();
	}

	@Override
	public String marshal(T o, String nodeName) {
		return o.toString();
	}*/

	@Override
	public final String marshal(T o) {
		return marshal(o, (String) null);
	}

	@Override
	public final String marshal(T o, String nodeName) {
		StringWriter writer = new StringWriter();
		marshal(o, writer, nodeName);
		return writer.toString();
	}

	@Override
	public final void marshal(T obj, OutputStream out) throws MarshallException {
		marshal(obj, out, null);
	}

	@Override
	public final void marshal(T obj, OutputStream out, String nodeName) throws MarshallException {
		Writer writer = new BufferedWriter(new OutputStreamWriter(out));
		marshal(obj, writer, nodeName);
	}

	@Override
	public final void marshal(T obj, Writer writer) throws MarshallException {
		marshal(obj, writer, null);
	}

	@Override
	public final void marshal(T obj, Writer writer, String nodeName) throws MarshallException {
		XMLWriter xmlWriter = new XMLWriter(writer, Marshallers.createPrettyPrint());
		try {
			if (nodeName == null || "".equals(nodeName.trim())) {
				nodeName = Constants.ROOT_NODE_NAME;
			}
			Document doc = DocumentHelper.createDocument();
			Element root = DocumentHelper.createElement(nodeName);
			doc.setRootElement(root);
			marshal(obj, root);
			xmlWriter.write(doc);
			writer.flush();
		} catch (IOException e) {
			throw new MarshallException(e);
		} finally {
			IOUtil.close(writer);
			try {
				xmlWriter.close();
			} catch (IOException e) {
			}
		}
	}

	@Override
	public final void marshal(T obj, Element container) {
		marshal(obj, container, null);
	}

	@Override
	public final void marshal(T obj, Element container, String nodeName) {
		String str = toString(obj);
		boolean isAttributePrior = MarshallerEnv.isAttributePrefer();// Constants.MARSHAL_ATTRIBUTE_PRIOR;
		if (isAttributePrior) {
			if (str != null && !str.equals("")) {
				if (nodeName != null && !"".equals(nodeName.trim())) {
					container.addAttribute(nodeName, str);
				} else {
					container.addAttribute(Constants.VALUE_NODE_NAME, str);
				}
			}
		} else {
			if (nodeName != null && !"".equals(nodeName.trim())) {
				nodeName = nodeName.trim();
				Element node = DocumentHelper.createElement(nodeName);
				container.add(node);
				container = node;
			}
			if (str != null && !str.equals("")) {
				container.setText(str);
			}
		}
	}

	@Override
	public final T unmarshal(String s) {
		return unmarshal((T) null, s);
	}

	/**
	 * 参数T将被忽略
	 */
	@Override
	public final T unmarshal(T obj, String s) {
		try {
			SAXReader saxReader = new SAXReader();
			Element rootElement = saxReader.read(new StringReader(s)).getRootElement();
			return unmarshal(obj, rootElement, null);
		} catch (DocumentException e) {
			return null;
		}
	}

	@Override
	public final T unmarshal(InputStream in) {
		return unmarshal((T) null, in);
	}

	/**
	 * 参数T将被忽略
	 */
	@Override
	public final T unmarshal(T obj, InputStream in) {
		try {
			SAXReader saxReader = new SAXReader();
			Element rootElement = saxReader.read(in).getRootElement();
			return unmarshal(obj, rootElement, null);
		} catch (DocumentException e) {
			return null;
		}
	};

	@Override
	public final T unmarshal(Reader reader) {
		return unmarshal((T) null, reader);
	}

	/**
	 * 参数T将被忽略
	 */
	@Override
	public final T unmarshal(T obj, Reader reader) {
		try {
			SAXReader saxReader = new SAXReader();
			Element rootElement = saxReader.read(reader).getRootElement();
			return unmarshal(obj, rootElement, null);
		} catch (DocumentException e) {
			return null;
		}
	};

	@Override
	public final T unmarshal(Element container) {
		return unmarshal((T) null, container, (String) null);
	}

	/**
	 * 参数T将被忽略
	 */
	@Override
	public final T unmarshal(T obj, Element container) {
		return unmarshal(obj, container, (String) null);
	}

	@Override
	public final T unmarshal(Element container, String nodeName) {
		return unmarshal((T) null, container, nodeName);
	}

	/**
	 * 参数T将被忽略
	 */
	@Override
	public final T unmarshal(T obj, Element container, String nodeName) {
		String value;
		if (nodeName == null || "".equals(nodeName.trim())) {
			value = container.getTextTrim();
			if (value == null || "".equals(value)) {
				value = container.attributeValue(Constants.VALUE_NODE_NAME);
			}
		} else {
			nodeName = nodeName.trim();
			value = container.elementTextTrim(nodeName);
			if (value == null || "".equals(value)) {
				value = container.attributeValue(nodeName);
			}
		}
		if (value == null || "".equals(value)) {
			return null;
		}
		return fromString(value);
	};

}
