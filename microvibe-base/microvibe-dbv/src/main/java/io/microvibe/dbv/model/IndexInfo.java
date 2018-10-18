package io.microvibe.dbv.model;

import io.microvibe.dbv.annotation.ColumnName;

public class IndexInfo {

	@ColumnName("TABLE_CAT")
	private String tableCatalog;
	@ColumnName("TABLE_SCHEM")
	private String tableSchema;
	@ColumnName("TABLE_NAME")
	private String tableName;

	@ColumnName("NON_UNIQUE")
	private boolean nonUnique;// 索引值是否可以不唯一
	/**
	 * 索引类别（可为 null）；TYPE 为 tableIndexStatistic 时索引类别为 null
	 */
	@ColumnName("INDEX_QUALIFIER")
	private String indexQualifier;
	/**
	 * 索引名称；TYPE 为 tableIndexStatistic 时索引名称为 null
	 */
	@ColumnName("INDEX_NAME")
	private String indexName;
	/**
	 * <pre>
	 * 索引类型：
	 * tableIndexStatistic - 此标识与表的索引描述一起返回的表统计信息
	 * tableIndexClustered - 此为集群索引
	 * tableIndexHashed - 此为散列索引
	 * tableIndexOther - 此为某种其他样式的索引
	 * </pre>
	 */
	@ColumnName("TYPE")
	private short type;
	/**
	 * 索引中的列序列号；TYPE 为 tableIndexStatistic 时该序列号为零
	 */
	@ColumnName("ORDINAL_POSITION")
	private short ordinalPosition;
	/**
	 * 列名称；TYPE 为 tableIndexStatistic 时列名称为 null
	 */
	@ColumnName("COLUMN_NAME")
	private String columnName;
	/**
	 * <pre>
	 * 列排序序列，
	 * "A" => 升序，"D" => 降序，如果排序序列不受支持，可能为 null；
	 * TYPE 为 tableIndexStatistic时排序序列为 null
	 * </pre>
	 */
	@ColumnName("ASC_OR_DESC")
	private String ascOrDesc;
	/**
	 * TYPE 为 tableIndexStatistic 时，它是表中的行数；否则，它是索引中唯一值的数量。
	 */
	@ColumnName("CARDINALITY")
	private int cardinality;
	/**
	 * TYPE 为 tableIndexStatisic 时，它是用于表的页数，否则它是用于当前索引的页数。
	 */
	@ColumnName("PAGES")
	private int pages;

	// 以下为添加的自定义属性
	private String isUnique;

	public String getAscOrDesc() {
		return ascOrDesc;
	}

	public int getCardinality() {
		return cardinality;
	}

	public String getColumnName() {
		return columnName;
	}

	public String getIndexName() {
		return indexName;
	}

	public String getIndexQualifier() {
		return indexQualifier;
	}

	public String getIsUnique() {
		return isUnique;
	}

	public short getOrdinalPosition() {
		return ordinalPosition;
	}

	public int getPages() {
		return pages;
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

	public short getType() {
		return type;
	}

	public boolean isNonUnique() {
		return nonUnique;
	}

	public void setAscOrDesc(String ascOrDesc) {
		this.ascOrDesc = ascOrDesc;
	}

	public void setCardinality(int cardinality) {
		this.cardinality = cardinality;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

	public void setIndexQualifier(String indexQualifier) {
		this.indexQualifier = indexQualifier;
	}

	public void setIsUnique(String isUnique) {
		this.isUnique = isUnique;
	}

	public void setNonUnique(boolean nonUnique) {
		this.nonUnique = nonUnique;
	}

	public void setOrdinalPosition(short ordinalPosition) {
		this.ordinalPosition = ordinalPosition;
	}

	public void setPages(int pages) {
		this.pages = pages;
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

	public void setType(short type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "IndexInfo [tableName=" + tableName + ", indexName=" + indexName + ", ordinalPosition="
				+ ordinalPosition + ", columnName=" + columnName + "]";
	}
}
