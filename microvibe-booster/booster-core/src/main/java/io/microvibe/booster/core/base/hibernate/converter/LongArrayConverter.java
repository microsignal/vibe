package io.microvibe.booster.core.base.hibernate.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.AttributeConverter;

public class LongArrayConverter implements AttributeConverter<Long[], String> {
	private static final Logger logger = LoggerFactory.getLogger(LongArrayConverter.class);
	private String separator = ",";

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	@Override
	public String convertToDatabaseColumn(Long[] attribute) {
		StringBuilder sb = new StringBuilder();
		if (attribute != null && attribute.length > 0) {
			for (int i = 0; i < attribute.length; i++) {
				sb.append(attribute[i]).append(separator);
			}
		}
		return sb.toString();
	}

	@Override
	public Long[] convertToEntityAttribute(String dbData) {
		if (dbData != null) {
			String[] arr = dbData.split(separator);
			Long[] rs = new Long[arr.length];
			for (int i = 0; i < arr.length; i++) {
				try {
					rs[i] = Long.valueOf(arr[i]);
				} catch (NumberFormatException e) {
					logger.debug(e.getMessage(), e);
					rs[i] = 0L;
				}
			}
			return rs;
		} else {
			return null;
		}
	}
}
