package io.microvibe.booster.core.base.conversion;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.core.convert.converter.Converter;

import java.sql.Timestamp;

public class StringToTimestampConverter implements Converter<String, Timestamp> {

	@Override
	public Timestamp convert(String source) {
		try {
			long millis = Long.parseLong(source);
			return new Timestamp(millis);
		} catch (NumberFormatException e) {
			for (String pattern : new String[]{"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd"}) {
				try {
					return new Timestamp(LocalDateTime.parse(source, DateTimeFormat.forPattern(pattern)).toDate()
						.getTime());
				} catch (Exception ex) {
				}
			}
			throw new IllegalArgumentException(source);
		}
	}

}
