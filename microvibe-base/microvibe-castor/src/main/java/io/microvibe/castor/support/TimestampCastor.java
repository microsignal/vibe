package io.microvibe.castor.support;

import java.sql.Timestamp;
import java.text.DateFormat;

public class TimestampCastor extends AbstractMarshallableCastor<Timestamp> {

	DateCastor dateCastor = new DateCastor(java.util.Date.class);

	public TimestampCastor(Class<Timestamp> type) {
		super(type);
	}

	@Override
	public Timestamp castFromBasic(Object orig) {
		java.util.Date date = dateCastor.castFromBasic(orig);
		return date == null ? null : new Timestamp(date.getTime());
	}

	@Override
	public String toString(Timestamp o) {
		return DateFormat.getDateTimeInstance().format(o);
	}

	@Override
	public Timestamp fromString(String s) {
		java.util.Date date = dateCastor.fromString(s);
		return date == null ? null : new Timestamp(date.getTime());
	}
}
