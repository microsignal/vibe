package io.microvibe.util.castor.support;

import java.sql.Time;
import java.text.DateFormat;

public class TimeCastor extends AbstractMarshallableCastor<Time> {

	DateCastor dateCastor = new DateCastor(java.util.Date.class);

	public TimeCastor(Class<Time> type) {
		super(type);
	}

	@Override
	public Time castFromBasic(Object orig) {
		java.util.Date date = dateCastor.castFromBasic(orig);
		return date == null ? null : new Time(date.getTime());
	}

	@Override
	public String toString(Time o) {
		return DateFormat.getTimeInstance().format(o);
	}

	@Override
	public Time fromString(String s) {
		java.util.Date date = dateCastor.fromString(s);
		return date == null ? null : new Time(date.getTime());
	}

}
