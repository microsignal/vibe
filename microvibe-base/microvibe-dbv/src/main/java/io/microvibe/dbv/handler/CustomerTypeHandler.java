package io.microvibe.dbv.handler;

import java.sql.ResultSet;

public interface CustomerTypeHandler<T> {

	T getColumnValue(final ResultSet rs, final String columnName);
}
