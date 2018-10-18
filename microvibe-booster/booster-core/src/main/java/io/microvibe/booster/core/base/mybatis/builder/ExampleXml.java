package io.microvibe.booster.core.base.mybatis.builder;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.Iterator;
import java.util.List;

public class ExampleXml {

	private static String whereByExample = "";

	static {
		try {
			init();
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private static void init() throws DocumentException {
		SAXReader reader = new SAXReader();
		Document doc = reader.read(ExampleXml.class.getResourceAsStream("example.xml"));
		Element root = doc.getRootElement();
		List<Element> sqls = root.elements("sql");
		for (Element sql : sqls) {
			if (sql.attributeValue("id").equalsIgnoreCase("whereByExample")) {
				whereByExample = sql.element("trim").asXML();
			}
		}
	}

	private static String getContent(Element element) {
		StringBuilder builder = new StringBuilder();
		for (Iterator<Element> i = element.elementIterator(); i.hasNext();) {
			Element e = i.next();
			builder.append(e.asXML());
		}
		return builder.toString();
	}
	public static String getWhereByExample() {
		return whereByExample;
	}

	public static void main(String[] args) {
		System.out.println(whereByExample);
	}
}
