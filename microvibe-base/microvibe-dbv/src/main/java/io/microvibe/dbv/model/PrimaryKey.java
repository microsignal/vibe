package io.microvibe.dbv.model;

import io.microvibe.dbv.annotation.ColumnName;

public class PrimaryKey {

	@ColumnName("TABLE_CAT")
	private String tableCatalog;
	@ColumnName("TABLE_SCHEM")
	private String tableSchema;
	@ColumnName("TABLE_NAME")
	private String tableName;
	@ColumnName("COLUMN_NAME")
	private String columnName;
	@ColumnName("KEY_SEQ")
	private String keySeq;
	@ColumnName("PK_NAME")
	private String pkName;

	public String getTableCatalog() {
		return tableCatalog;
	}

	public void setTableCatalog(String tableCatalog) {
		this.tableCatalog = tableCatalog;
	}

	public String getTableSchema() {
		return tableSchema;
	}

	public void setTableSchema(String tableSchema) {
		this.tableSchema = tableSchema;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getKeySeq() {
		return keySeq;
	}

	public void setKeySeq(String keySeq) {
		this.keySeq = keySeq;
	}

	public String getPkName() {
		return pkName;
	}

	public void setPkName(String pkName) {
		this.pkName = pkName;
	}

	@Override
	public String toString() {
		return "PrimaryKey [tableName=" + tableName + ", columnName=" + columnName + ", pkName="
				+ pkName + "]";
	}
}
