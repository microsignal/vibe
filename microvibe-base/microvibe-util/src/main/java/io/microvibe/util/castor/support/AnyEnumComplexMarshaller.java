package io.microvibe.util.castor.support;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import io.microvibe.util.StringUtil;
import io.microvibe.util.castor.Constants;
import io.microvibe.util.castor.MarshallException;
import io.microvibe.util.castor.MarshallerEnv;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class AnyEnumComplexMarshaller<T extends Enum> extends AbstractComplexMarshaller<T> {

	private String xName;

	public AnyEnumComplexMarshaller(Class<T> type) {
		super(type);
		xName = StringUtil.formatNameAsXmlStyle(StringUtil.formatInstanceName(type));
	}

	@Override
	protected String defaultRootNodeName(T obj) {
		return xName;
	}

	@Override
	protected void innerMarshal(T obj, Element container, String nodeName) {
		String str = obj.name();
		boolean isAttributePrior = MarshallerEnv.isAttributePrefer();// Constants.MARSHAL_ATTRIBUTE_PRIOR;;

		if (nodeName != null && !"".equals(nodeName.trim())) {
			nodeName = nodeName.trim();
			if (isAttributePrior) {
				container.addAttribute(nodeName, str);
			} else {
				Element node = DocumentHelper.createElement(nodeName);
				node.setText(str);
				container.add(node);
			}
		} else {
			if (isAttributePrior) {
				container.addAttribute(Constants.VALUE_NODE_NAME, str);
			} else {
				container.setText(str);
			}
		}
	}

	@Override
	protected T createInstance() throws MarshallException {
		return null;
	}

	/**
	 * 忽略参数T
	 */
	@Override
	public T innerUnmarshalSpecifiedNode(Element container, String nodeName, T obj) {
		String value;
		if (nodeName != null && !"".equals(nodeName.trim())) {
			nodeName = nodeName.trim();
			value = StringUtil.coalesce(
					container.elementTextTrim(nodeName), container.attributeValue(nodeName));
		} else {
			value = StringUtil.coalesce(
					container.getTextTrim(), container.attributeValue(Constants.VALUE_NODE_NAME));
		}
		if (value == null || value.trim().equals("")) {
			return null;
		}
		obj = (T) Enum.valueOf(type, value);
		return obj;
	}
}
