package io.microvibe.codegen;

import io.microvibe.codegen.bean.db.Table;

public interface TablesReader {

	Table read(String catalogName, String schemaName, String tableName);

	void close();

}
