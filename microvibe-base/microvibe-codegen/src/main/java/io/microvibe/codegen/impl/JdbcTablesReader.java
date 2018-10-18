package io.microvibe.codegen.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import io.microvibe.codegen.bean.db.Column;
import io.microvibe.codegen.bean.db.Table;
import io.microvibe.dbv.DBV;
import io.microvibe.dbv.cfg.Configurations;
import io.microvibe.dbv.cfg.DatabaseCfg;
import io.microvibe.util.DBUtil;
import io.microvibe.util.StringUtil;

public class JdbcTablesReader implements io.microvibe.codegen.TablesReader {
	private Connection conn;
	private DatabaseCfg cfg;

	public JdbcTablesReader(File jdbcCfgFile) throws FileNotFoundException {
		cfg = Configurations.getDatabaseCfg(new FileInputStream(jdbcCfgFile));
	}

	@Override
	public Table read(String catalogName, String schemaName, String tableName) {
		try {
			if (conn == null || conn.isClosed()) {
				conn = DBV.getConnection(cfg);
			}
			DatabaseMetaData metaData = conn.getMetaData();
			// 读取数据库中的表的元数据
			List<io.microvibe.dbv.model.Table> tables = DBV.readTables(metaData, catalogName, schemaName,
					tableName, "TABLE");
			if (tables != null && tables.size() > 0) {
				io.microvibe.dbv.model.Table tab = tables.get(0);
				// 构造代码生成工具所需表结构明细的元数据对象
				Table table = new Table();
				table.setName(tab.getTableName());
				table.setComment(StringUtil.coalesce(tab.getRemarks(), tab.getTableName()));

				Set<String> pkColumns = tab.getPkColumns();
				List<io.microvibe.dbv.model.Column> columnList = tab.getColumnList();
				for (io.microvibe.dbv.model.Column col : columnList) {
					Column column = new Column();
					column.setName(col.getColumnName());
					column.setType(col.getDataType());
					column.setComment(StringUtil.coalesce(col.getRemarks(), col.getColumnName()));
					column.setDefaultValue(col.getColumnDef());
					column.setNullable(1 == col.getNullable());
					table.getColumns().add(column);
					if (pkColumns.contains(col.getColumnName())) {
						column.setPrimary(true);
						table.getPkColumns().add(column);
					} else {
						table.getNormalColumns().add(column);
					}
				}
				table.prepare4Java();
				return table;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void close() {
		DBUtil.close(conn);
	}

}
