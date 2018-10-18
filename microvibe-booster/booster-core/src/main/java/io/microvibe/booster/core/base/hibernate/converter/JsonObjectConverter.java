package io.microvibe.booster.core.base.hibernate.converter;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;

public class JsonObjectConverter implements AttributeConverter<Object, String> {

	@Override
	public String convertToDatabaseColumn(Object attribute) {
		if (attribute == null)
			return null;
		return JSONObject.toJSONString(attribute, SerializerFeature.WriteClassName);
	}

	@Override
	public Object convertToEntityAttribute(String dbData) {
		if (StringUtils.isEmpty(dbData)) {
			return null;
		}
		return JSONObject.parseObject(dbData, Object.class);
	}
}
