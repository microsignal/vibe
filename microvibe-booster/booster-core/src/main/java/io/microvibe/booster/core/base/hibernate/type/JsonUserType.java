package io.microvibe.booster.core.base.hibernate.type;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.microvibe.booster.commons.utils.json.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

@SuppressWarnings("rawtypes")
public class JsonUserType extends AbstractUserType {
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

	@SuppressWarnings("unchecked")
	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
		throws HibernateException, SQLException {
		Object result;
		String columnName = names[0];
		String valueStr = rs.getString(columnName);
		if (!StringUtils.isEmpty(valueStr)) {
			if (objectType != null && objectType != Object.class) {
				result = JsonUtil.toJavaObject(objectType, valueStr);
			} else {
				result = JSONObject.parseObject(valueStr, Object.class);
			}
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
			if (objectType != null && objectType != Object.class) {
				valueStr = JsonUtil.toJson(value);
			} else {
				valueStr = JSONObject.toJSONString(value, SerializerFeature.WriteClassName);
			}
		}
		st.setString(index, valueStr);
	}

}
