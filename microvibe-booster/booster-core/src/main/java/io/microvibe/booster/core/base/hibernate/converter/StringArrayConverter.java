package io.microvibe.booster.core.base.hibernate.converter;

import javax.persistence.AttributeConverter;

public class StringArrayConverter implements AttributeConverter<String[], String> {

	private String separator = ",";

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	@Override
	public String convertToDatabaseColumn(String[] attribute) {
		StringBuilder sb = new StringBuilder();
		if (attribute != null && attribute.length > 0) {
			for (int i = 0; i < attribute.length; i++) {
				sb.append(attribute[i]).append(separator);
			}
		}
		return sb.toString();
	}

	@Override
	public String[] convertToEntityAttribute(String dbData) {
		if (dbData != null) {
			return dbData.split(separator);
		}
		return null;
	}
}
