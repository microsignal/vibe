package io.microvibe.booster.core.base.hibernate.type;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.springframework.util.Base64Utils;
import org.springframework.util.SerializationUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

@SuppressWarnings("rawtypes")
public class SerializableBase64UserType extends AbstractUserType {
	private static final long serialVersionUID = 1L;
	public static final String PARAM_OBJECT_TYPE = "objectType";

	private Class objectType = Object.class;

	@Override
	public void setParameterValues(Properties parameters) {
		this.parameters = parameters;
		String objectType = (String) parameters.get(PARAM_OBJECT_TYPE);
		if (!StringUtils.isEmpty(objectType)) {
			setObjectType(objectType);
		}
	}

	public void setObjectType(String objectType) {
		try {
			setObjectType(Class.forName(objectType));
		} catch (ClassNotFoundException e) {
			throw new HibernateException(e);
		}
	}

	public void setObjectType(Class objectType) {
		this.objectType = objectType;
	}

	@Override
	public int[] sqlTypes() {
		return new int[]{Types.VARCHAR};
	}

	@Override
	public Class returnedClass() {
		return objectType;
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
		throws HibernateException, SQLException {
		Object result;
		String columnName = names[0];
		String valueStr = rs.getString(columnName);
		if (!StringUtils.isEmpty(valueStr)) {
			byte[] bytes = Base64Utils.decodeFromString(valueStr);
			result = SerializationUtils.deserialize(bytes);
		} else {
			result = null;
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
			byte[] bytes = SerializationUtils.serialize(value);
			valueStr = Base64Utils.encodeToString(bytes);
		}
		st.setString(index, valueStr);
	}

}
