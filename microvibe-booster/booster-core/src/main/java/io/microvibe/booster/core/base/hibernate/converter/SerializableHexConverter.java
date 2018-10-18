package io.microvibe.booster.core.base.hibernate.converter;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.util.SerializationUtils;

import javax.persistence.AttributeConverter;
import java.io.Serializable;

public class SerializableHexConverter implements AttributeConverter<Serializable, String> {

	@Override
	public String convertToDatabaseColumn(Serializable attribute) {
		byte[] bytes = SerializationUtils.serialize(attribute);
		String result = Hex.encodeHexString(bytes);
		return result;
	}

	@Override
	public Serializable convertToEntityAttribute(String dbData) {
		try {
			byte[] bytes = Hex.decodeHex(dbData.toCharArray());
			Serializable result = (Serializable) SerializationUtils.deserialize(bytes);
			return result;
		} catch (DecoderException e) {
			return null;
		}
	}
}
