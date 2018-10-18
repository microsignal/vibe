package io.microvibe.castor.support;

import java.sql.Date;
import java.text.DateFormat;

public class SqlDateCastor extends AbstractMarshallableCastor<Date> {

	DateCastor dateCastor = new DateCastor(java.util.Date.class);

	public SqlDateCastor(Class<Date> type) {
		super(type);
	}

	@Override
	public Date castFromBasic(Object orig) {
		java.util.Date date = dateCastor.castFromBasic(orig);
		return date == null ? null : new Date(date.getTime());
	}

	@Override
	public String toString(Date o) {
		return DateFormat.getDateInstance().format(o);
	}

	@Override
	public Date fromString(String s) {
		java.util.Date date = dateCastor.fromString(s);
		return date == null ? null : new Date(date.getTime());
	}

}
