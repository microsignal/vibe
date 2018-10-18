package io.microvibe.booster.core.base.hibernate.converter;

import org.springframework.util.Base64Utils;
import org.springframework.util.SerializationUtils;

import javax.persistence.AttributeConverter;
import java.io.Serializable;

public class SerializableBase64Converter implements AttributeConverter<Serializable, String> {

	@Override
	public String convertToDatabaseColumn(Serializable attribute) {
		byte[] bytes = SerializationUtils.serialize(attribute);
		String result = Base64Utils.encodeToString(bytes);
		return result;
	}

	@Override
	public Serializable convertToEntityAttribute(String dbData) {
		byte[] bytes = Base64Utils.decodeFromString(dbData);
		Serializable result = (Serializable) SerializationUtils.deserialize(bytes);
		return result;
	}
}
