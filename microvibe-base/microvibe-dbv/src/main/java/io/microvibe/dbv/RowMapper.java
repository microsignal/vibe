package io.microvibe.dbv;

import java.sql.ResultSet;

public interface RowMapper<T> {
	T rowToObject(ResultSet rs) throws DbvException;
}
