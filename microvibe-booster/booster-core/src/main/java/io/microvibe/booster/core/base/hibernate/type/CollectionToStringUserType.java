package io.microvibe.booster.core.base.hibernate.type;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.Properties;

/**
 * 将List转换为指定分隔符分隔的字符串存储 <br>
 * List的元素类型只支持常见的数据类型可参考{@link org.apache.commons.beanutils.ConvertUtilsBean}
 *
 * @author Qt
 * @since Nov 13, 2017
 */
@SuppressWarnings("rawtypes")
public class CollectionToStringUserType extends AbstractUserType {
	private static final long serialVersionUID = 1L;
	public static final String PARAM_SEPARATOR = "separator";
	public static final String PARAM_COLLECTION_TYPE = "collectionType";
	public static final String PARAM_ELEMENT_TYPE = "elementType";

	private String separator = ",";
	private Class collectionType = java.util.ArrayList.class;
	private Class elementType = Long.TYPE;// 默认为:long

	@Override
	public void setParameterValues(Properties parameters) {
		this.parameters = parameters;
		String separator = (String) parameters.get(PARAM_SEPARATOR);
		if (!StringUtils.isEmpty(separator)) {
			setSeparator(separator);
		}
		String collectionType = (String) parameters.get(PARAM_COLLECTION_TYPE);
		if (!StringUtils.isEmpty(collectionType)) {
			setCollectionType(collectionType);
		}
		String elementType = (String) parameters.get(PARAM_ELEMENT_TYPE);
		if (!StringUtils.isEmpty(elementType)) {
			setElementType(elementType);
		}
	}

	@Override
	public int[] sqlTypes() {
		return new int[]{Types.VARCHAR};
	}

	@Override
	public Class returnedClass() {
		return collectionType;
	}

	private Collection newCollection() {
		try {
			return (Collection) collectionType.newInstance();
		} catch (Exception e) {
			throw new HibernateException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
		throws HibernateException, SQLException {
		Collection result = newCollection();
		String columnName = names[0];
		String valueStr = rs.getString(columnName);
		if (!StringUtils.isEmpty(valueStr)) {
			String[] values = StringUtils.split(valueStr, separator);
			for (String value : values) {
				if (StringUtils.isNotEmpty(value)) {
					result.add(ConvertUtils.convert(value, elementType));
				}
			}
		}
		return result;
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
		throws HibernateException, SQLException {
		String valueStr;
		if (value == null) {
			valueStr = "";
		} else {
			valueStr = StringUtils.join((Collection) value, separator);
		}
		if (StringUtils.isNotEmpty(valueStr)) {
			valueStr = valueStr + separator;
		}
		st.setString(index, valueStr);
	}

	public void setElementType(Class elementType) {
		this.elementType = elementType;
	}

	public void setElementType(String elementType) {
		try {
			this.elementType = Class.forName(elementType);
		} catch (ClassNotFoundException e) {
			throw new HibernateException(e);
		}
	}

	public void setCollectionType(Class collectionType) {
		this.collectionType = collectionType;
	}

	public void setCollectionType(String collectionType) {
		try {
			this.collectionType = Class.forName(collectionType);
		} catch (ClassNotFoundException e) {
			throw new HibernateException(e);
		}
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}
}
