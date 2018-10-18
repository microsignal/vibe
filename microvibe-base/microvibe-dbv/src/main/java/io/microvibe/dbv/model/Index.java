package io.microvibe.dbv.model;

public class Index {
	private String tableCatalog;
	private String tableSchema;
	private String tableName;
	private String indexName;
	private String columnNames;
	private String ascOrDesc;
	private boolean nonUnique = true;// 索引值是否可以不唯一
	private boolean unique = false;
	private String isUnique;

	public String getAscOrDesc() {
		return ascOrDesc;
	}

	public String getColumnNames() {
		return columnNames;
	}

	public String getIndexName() {
		return indexName;
	}

	public String getIsUnique() {
		return isUnique;
	}

	public String getTableCatalog() {
		return tableCatalog;
	}

	public String getTableName() {
		return tableName;
	}

	public String getTableSchema() {
		return tableSchema;
	}

	public boolean isNonUnique() {
		return nonUnique;
	}

	public boolean isUnique() {
		return unique;
	}

	public void setAscOrDesc(String ascOrDesc) {
		this.ascOrDesc = ascOrDesc;
	}

	public void setColumnNames(String columnNames) {
		this.columnNames = columnNames;
	}

	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

	public void setIsUnique(String isUnique) {
		this.isUnique = isUnique;
	}

	public void setNonUnique(boolean nonUnique) {
		this.nonUnique = nonUnique;
	}

	public void setTableCatalog(String tableCatalog) {
		this.tableCatalog = tableCatalog;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setTableSchema(String tableSchema) {
		this.tableSchema = tableSchema;
	}

	public void setUnique(boolean unique) {
		this.unique = unique;
	}

	@Override
	public String toString() {
		return "Index [tableName=" + tableName + ", indexName=" + indexName + ", ascOrDesc="
				+ ascOrDesc + ", unique=" + unique + "]";
	}

}
