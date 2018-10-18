package io.microvibe.booster.core.base.hibernate.converter;

import javax.persistence.AttributeConverter;

public class LongPathConverter extends LongArrayConverter implements AttributeConverter<Long[], String> {

	public LongPathConverter() {
		setSeparator("/");
	}

}
