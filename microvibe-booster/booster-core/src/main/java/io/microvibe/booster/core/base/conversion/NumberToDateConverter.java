package io.microvibe.booster.core.base.conversion;

import org.springframework.core.convert.converter.Converter;

import java.util.Date;

public class NumberToDateConverter implements Converter<Number, Date> {

	@Override
	public Date convert(Number source) {
		return new Date(source.longValue());
	}

}
