package io.microvibe.booster.core.base.conversion;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.core.convert.converter.Converter;

import java.util.Date;

public class StringToDateConverter implements Converter<String, Date> {

	@Override
	public Date convert(String source) {
		try {
			long millis = Long.parseLong(source);
			return new Date(millis);
		} catch (NumberFormatException e) {
			for (String pattern : new String[]{"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd"}) {
				try {
					return LocalDateTime.parse(source, DateTimeFormat.forPattern(pattern)).toDate();
				} catch (Exception ex) {
				}
			}
			throw new IllegalArgumentException(source);
		}
	}

}
