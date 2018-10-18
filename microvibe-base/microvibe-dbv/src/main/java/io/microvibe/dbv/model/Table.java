package io.microvibe.dbv.model;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import io.microvibe.dbv.annotation.ColumnName;
import io.microvibe.util.collection.IgnoreCaseLinkedHashMap;

public class Table {
	@ColumnName("TABLE_CAT")
	private String tableCatalog;
	@ColumnName("TABLE_SCHEM")
	private String tableSchema;
	@ColumnName("TABLE_NAME")
	private String tableName;
	@ColumnName("TABLE_TYPE")
	private String tableType;
	@ColumnName("REMARKS")
	private String remarks;

	private List<PrimaryKey> primaryKeyList = new ArrayList<PrimaryKey>();
	private List<IndexInfo> indexInfoList = new ArrayList<IndexInfo>();
	private List<Column> columnList = new ArrayList<Column>();

	private Set<String> pkColumns = new LinkedHashSet<String>();
	private Map<String, Column> columnMap = new IgnoreCaseLinkedHashMap<String, Column>();
	private List<Index> indexList = new ArrayList<Index>();

	/**
	 * 处理自定义的附加字段值
	 */
	public void fit() {
		Set<IndexInfo> sortedIndexInfos = new TreeSet<IndexInfo>(new Comparator<IndexInfo>() {
			@Override
			public int compare(IndexInfo o1, IndexInfo o2) {
				if (o1 == o2) return 0;
				if (o1 == null) return 1;
				if (o2 == null) return -1;
				int i = o1.getIndexName().compareTo(o2.getIndexName());
				if (i == 0) {
					if (o1.getOrdinalPosition() < o2.getOrdinalPosition()) {
						return -1;
					} else {
						return 1;
					}
				} else {
					return i;
				}
			}
		});
		for (IndexInfo idx : indexInfoList) {
			sortedIndexInfos.add(idx);
			if (!idx.isNonUnique()) {
				idx.setIsUnique("YES");
			} else {
				idx.setIsUnique("NO");
			}
		}
		for (IndexInfo idxInfo : sortedIndexInfos) {
			if (indexList.size() > 0
					&& indexList.get(indexList.size() - 1).getIndexName().equals(idxInfo.getIndexName())) {
				Index idx = indexList.get(indexList.size() - 1);
				idx.setColumnNames(idx.getColumnNames() + "," + idxInfo.getColumnName());
			} else {
				Index idx = new Index();
				idx.setTableCatalog(idxInfo.getTableCatalog());
				idx.setTableSchema(idxInfo.getTableSchema());
				idx.setTableName(idxInfo.getTableName());
				idx.setIndexName(idxInfo.getIndexName());
				idx.setColumnNames(idxInfo.getColumnName());
				idx.setAscOrDesc(idxInfo.getAscOrDesc());
				idx.setNonUnique(idxInfo.isNonUnique());
				idx.setUnique(!idxInfo.isNonUnique());
				idx.setIsUnique(idxInfo.getIsUnique());
				indexList.add(idx);
			}
		}

		for (PrimaryKey pk : primaryKeyList) {
			pkColumns.add(pk.getColumnName());
		}
		for (Column col : columnList) {
			columnMap.put(col.getColumnName(), col);

			// 为自定义的主键、非空、列类型等的字段赋值
			col.setIsPrimaryKey("NO");
			col.setIsNotNullable("NO");
			if (pkColumns.contains(col.getColumnName())) {
				col.setIsPrimaryKey("YES");
				col.setPrimaryKey(true);
			}
			if (col.getNullable() == 0) {
				col.setIsNotNullable("YES");
				col.setNotNull(true);
			}
			int dataType = col.getDataType();
			switch (dataType) {
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.NCHAR:
			case Types.NVARCHAR:
				col.setColumnType(col.getTypeName() + "(" + col.getColumnSize() + ")");
				break;
			case Types.NUMERIC:
			case Types.DECIMAL:
			case Types.DOUBLE:
			case Types.FLOAT:
				int decimalDigits = col.getDecimalDigits();
				col.setColumnType(col.getTypeName() + "(" + col.getColumnSize()
						+ (decimalDigits > 0 ? "," + decimalDigits : "") + ")");
				break;
			default:
				col.setColumnType(col.getTypeName());
			}
		}
	}

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

	public String getTableType() {
		return tableType;
	}

	public void setTableType(String tableType) {
		this.tableType = tableType;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public List<PrimaryKey> getPrimaryKeyList() {
		return primaryKeyList;
	}

	public void setPrimaryKeyList(List<PrimaryKey> primaryKeyList) {
		this.primaryKeyList = primaryKeyList;
	}

	public List<IndexInfo> getIndexInfoList() {
		return indexInfoList;
	}

	public void setIndexInfoList(List<IndexInfo> indexInfoList) {
		this.indexInfoList = indexInfoList;
	}

	public List<Column> getColumnList() {
		return columnList;
	}

	public void setColumnList(List<Column> columnList) {
		this.columnList = columnList;
	}

	public Set<String> getPkColumns() {
		return pkColumns;
	}

	public void setPkColumns(Set<String> pkColumns) {
		this.pkColumns = pkColumns;
	}

	public Map<String, Column> getColumnMap() {
		return columnMap;
	}

	public void setColumnMap(Map<String, Column> columnMap) {
		this.columnMap = columnMap;
	}

	public List<Index> getIndexList() {
		return indexList;
	}

	public void setIndexList(List<Index> indexList) {
		this.indexList = indexList;
	}

	@Override
	public String toString() {
		return "Table [tableName=" + tableName + ", tableType=" + tableType + ", remarks=" + remarks
				+ "]";
	}

}
