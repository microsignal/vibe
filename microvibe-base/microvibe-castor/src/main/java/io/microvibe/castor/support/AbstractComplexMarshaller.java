package io.microvibe.castor.support;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import io.microvibe.util.io.IOUtil;
import io.microvibe.util.StringUtil;
import io.microvibe.castor.ComplexMarshaller;
import io.microvibe.castor.Constants;
import io.microvibe.castor.MarshallException;
import io.microvibe.castor.Marshallers;

public abstract class AbstractComplexMarshaller<T> implements ComplexMarshaller<T> {

	protected Class<T> type;

	public AbstractComplexMarshaller(Class<T> type) {
		this.type = type;
	}

	@Override
	public final String marshal(T o) {
		return marshal(o, (String) null);
	}

	@Override
	public final String marshal(T o, String nodeName) throws MarshallException {
		StringWriter writer = new StringWriter();
		marshal(o, writer, nodeName);
		return writer.toString();
	}

	@Override
	public void marshal(T obj, OutputStream out) throws MarshallException {
		marshal(obj, out, null);
	}

	@Override
	public void marshal(T obj, OutputStream out, String nodeName) throws MarshallException {
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
		LocalContext.bind(new LocalContext());
		try {
			if (nodeName == null || "".equals(nodeName.trim())) {
				nodeName = defaultRootNodeName(obj);
				if (nodeName == null || "".equals(nodeName.trim())) {
					nodeName = Constants.ROOT_NODE_NAME;
				}
			}
			Document doc = DocumentHelper.createDocument();
			Element root = DocumentHelper.createElement(nodeName);
			doc.setRootElement(root);
			__innerMarshal(obj, root, null);
			xmlWriter.write(doc);
			writer.flush();
		} catch (IOException e) {
			throw new MarshallException(e);
		} finally {
			LocalContext.unbind();
			IOUtil.close(writer);
			try {
				xmlWriter.close();
			} catch (IOException e) {
			}
		}
	}

	@Override
	public final void marshal(T obj, Element container) throws MarshallException {
		marshal(obj, container, null);
	}

	@Override
	public final void marshal(T obj, Element container, String nodeName) throws MarshallException {
		if (LocalContext.getLocalContext() == null) {
			LocalContext.bind(new LocalContext());
			try {
				__innerMarshal(obj, container, nodeName);
			} finally {
				LocalContext.unbind();
			}
		} else {
			__innerMarshal(obj, container, nodeName);
		}
	}

	private void __innerMarshal(T obj, Element container, String nodeName) throws MarshallException {
		if (obj == null) {
			Marshallers.getSimpleMarshaller(null).marshal(obj, container, nodeName);
		} else {
			innerMarshal(obj, container, nodeName);
		}
	}

	protected abstract String defaultRootNodeName(T obj);

	protected abstract void innerMarshal(T obj, Element container, String nodeName);

	/**
	 * 判断对象是否已被序列化过,如是则作为引用类元素映射,引用已序列化的映射元素路径
	 *
	 * @param object
	 *            待判断对象
	 * @param container
	 *            序列化时挂载的xml元素
	 * @return 是否作为引用元素映射
	 */
	protected final boolean handleMarshalledRef(Object object, Element container, String nodeName) {
		LocalContext.ElementCache cache = LocalContext.getLocalContext().getMarshalledObjectCache(object);
		if (cache != null) { // 这个对象已被映射入xml
			Element refContainer = cache.getContainer();
			String refNodeName = cache.getNodeName();
			Element ref = DocumentHelper.createElement(Constants.XREF_NODE_NAME);
			ref.addAttribute(Constants.XREF_XPATH_ATTR_NAME, refContainer.getUniquePath());
			ref.addAttribute(Constants.XREF_XNODE_ATTR_NAME, refNodeName);
			Element node;
			if (nodeName != null && !"".equals(nodeName.trim())) {
				node = DocumentHelper.createElement(nodeName.trim());
				node.add(ref);
			} else {
				node = ref;
			}
			container.add(node);
			return true;
		}
		return false;
	}

	protected final void cacheMarshalledObj(Object object, Element element, String nodeName) {
		LocalContext.getLocalContext().addMarshalledObjectCache(object, element, nodeName);
	}

	@Override
	public final T unmarshal(String s) throws MarshallException {
		return unmarshal(createInstance(), s);
	}

	@Override
	public final T unmarshal(T obj, String s) {
		StringReader reader = new StringReader(s);
		return unmarshal(obj, reader);
	}

	@Override
	public final T unmarshal(InputStream in) throws MarshallException {
		return unmarshal(createInstance(), in);
	}

	@Override
	public final T unmarshal(T obj, InputStream in) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		return unmarshal(obj, reader);
	};

	@Override
	public final T unmarshal(Reader reader) throws MarshallException {
		return unmarshal(createInstance(), reader);
	}

	@Override
	public final T unmarshal(T obj, Reader reader) {
		LocalContext.bind(new LocalContext());
		try {
			SAXReader saxReader = new SAXReader();
			Element rootElement = saxReader.read(reader).getRootElement();
			return unmarshal(obj, rootElement, null);
		} catch (DocumentException e) {
			return null;
		} finally {
			LocalContext.unbind();
		}
	};

	@Override
	public final T unmarshal(Element container) throws MarshallException {
		return unmarshal(createInstance(), container);
	}

	@Override
	public final T unmarshal(T obj, Element container) {
		return unmarshal(obj, container, (String) null);
	}

	@Override
	public final T unmarshal(Element container, String nodeName) throws MarshallException {
		return unmarshal(createInstance(), container, nodeName);
	}

	@Override
	public final T unmarshal(T obj, Element container, String nodeName) {
		if (LocalContext.getLocalContext() == null) {
			LocalContext.bind(new LocalContext());
			try {
				return innerUnmarshalSpecifiedNode(container, nodeName, obj);
			} finally {
				LocalContext.unbind();
			}
		} else {
			return innerUnmarshalSpecifiedNode(container, nodeName, obj);
		}
	};

	protected abstract T createInstance() throws MarshallException;

	protected abstract T innerUnmarshalSpecifiedNode(Element node, String nodeName, T obj);

	/**
	 * 判断元素是否引用前面的元素,如果是则从已读的xml转换对象缓存池中获取 <br>
	 * 引用的格式形如: &lt;xref xpath="..." /&gt;
	 */
	protected final Object findUnmarshalledRef(Element container, String nodeName) {
		Element node;
		if (nodeName != null && !"".equals(nodeName.trim())) {
			node = container.element(nodeName.trim());
		} else {
			node = container;
		}
		if (node == null) return null;
		Element xrefElement = node.element(Constants.XREF_NODE_NAME);
		if (xrefElement == null) return null;

		if (xrefElement.attributeCount() >= 1 && xrefElement.attributeCount() <= 2
				&& xrefElement.isTextOnly()) {
			String xpath = StringUtil.trimToNull(xrefElement.attributeValue(Constants.XREF_XPATH_ATTR_NAME));
			String xnode = StringUtil.trimToNull(xrefElement.attributeValue(Constants.XREF_XNODE_ATTR_NAME));
			if (xpath != null) {
				Node readNode = container.getDocument().selectSingleNode(xpath);
				if (readNode instanceof org.dom4j.Element) {
					Element ref = (Element) readNode;
					return LocalContext.getLocalContext().getUnmarshalledObjectCache(ref, xnode);
				}
			}
		}
		return null;
	}

	protected final void cacheUnmarshalledObj(Element container, String nodeName, Object object) {
		LocalContext.getLocalContext().addUnmarshalledObjectCache(container, nodeName, object);
	}

}
