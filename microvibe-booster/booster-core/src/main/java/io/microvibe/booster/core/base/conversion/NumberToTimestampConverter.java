package io.microvibe.booster.core.base.conversion;

import org.springframework.core.convert.converter.Converter;

import java.sql.Timestamp;

public class NumberToTimestampConverter implements Converter<Number, Timestamp> {

	@Override
	public Timestamp convert(Number source) {
		return new Timestamp(source.longValue());
	}

}
