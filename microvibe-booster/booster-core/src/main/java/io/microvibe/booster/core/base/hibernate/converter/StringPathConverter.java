package io.microvibe.booster.core.base.hibernate.converter;

import javax.persistence.AttributeConverter;

public class StringPathConverter extends StringArrayConverter implements AttributeConverter<String[], String> {

	public StringPathConverter() {
		setSeparator("/");
	}

}
