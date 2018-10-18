package io.microvibe.dbv;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.microvibe.dbv.cfg.Configurations;
import io.microvibe.dbv.cfg.DatabaseCfg;
import io.microvibe.dbv.model.Catalog;
import io.microvibe.dbv.model.Column;
import io.microvibe.dbv.model.IndexInfo;
import io.microvibe.dbv.model.PrimaryKey;
import io.microvibe.dbv.model.Schema;
import io.microvibe.dbv.model.Table;
import io.microvibe.dbv.model.TableType;
import io.microvibe.util.DBUtil;
import io.microvibe.util.StringUtil;

public final class DBV {

	static final Logger logger = LoggerFactory.getLogger(DBV.class);

	public static String formatFieldName(String fieldName) {
		return StringUtil.formatNameAsJavaStyle(fieldName);
	}

	public static Connection getConnection() throws DbvException {
		try {
			DatabaseCfg cfg = Configurations.getDatabaseCfg();
			return getConnection(cfg);
		} catch (Exception e) {
			throw new DbvException(e);
		}
	}

	public static Connection getConnection(DatabaseCfg cfg) throws SQLException {
		return DBUtil.getConnection(cfg.getJdbcDriver(), cfg.getJdbcUrl(), cfg.getJdbcInfoProperties());
	}

	public static Connection getConnection(String cfgPath) throws DbvException {
		try {
			DatabaseCfg cfg = Configurations.getDatabaseCfg(cfgPath);
			return getConnection(cfg);
		} catch (Exception e) {
			throw new DbvException(e);
		}
	}

	public static <T> List<T> read(ResultSet rs, List<T> list, final Class<? extends T> clazz)
			throws InstantiationException, IllegalAccessException {
		try {
			while (rs.next()) {
				final T object = clazz.newInstance();
				ResultSetUtil.fetch(rs, object);
				list.add(object);
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		} finally {
			DBUtil.close(rs);
		}
		return list;
	}

	public static <T> List<T> read(ResultSet rs, List<T> list, RowMapper<T> mapper)
			throws InstantiationException, IllegalAccessException {
		try {
			while (rs.next()) {
				list.add(mapper.rowToObject(rs));
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		} finally {
			DBUtil.close(rs);
		}
		return list;
	}

	public static List<Catalog> readCatalog(DatabaseMetaData metaData) throws DbvException {
		try {
			List<Catalog> list = new ArrayList<Catalog>(20);
			read(metaData.getCatalogs(), list, Catalog.class);
			return list;
		} catch (Exception e) {
			return new ArrayList<Catalog>();
		}
	}

	public static List<Column> readColumns(DatabaseMetaData metaData, String catalog, String schema,
			String tableName, String columnName) {
		try {
			List<Column> list = read(metaData.getColumns(catalog, schema, tableName, columnName),
					new ArrayList<Column>(50), Column.class);
			return list;
		} catch (Exception e) {
			return new ArrayList<Column>();
		}
	}

	public static List<IndexInfo> readIndexes(DatabaseMetaData metaData, String catalog,
			String schema, String tableName) {
		try {
			List<IndexInfo> indexes = read(
					metaData.getIndexInfo(catalog, schema, tableName, false, false),
					new ArrayList<IndexInfo>(20), IndexInfo.class);
			final Iterator<IndexInfo> iter = indexes.iterator();
			while (iter.hasNext()) {
				final IndexInfo index = iter.next();
				if (index.getIndexName() == null || index.getIndexName().equals("")) {
					iter.remove();
				}
			}
			return indexes;
		} catch (Exception e) {
			return new ArrayList<IndexInfo>();
		}
	}

	public static List<Schema> readSchemas(DatabaseMetaData metaData) {
		try {
			List<Schema> list = new ArrayList<Schema>(30);
			read(metaData.getSchemas(), list, Schema.class);
			return list;
		} catch (Exception e) {
			return new ArrayList<Schema>();
		}
	}

	public static List<Table> readTables(DatabaseMetaData metaData, String catalog, String schema,
			String tableName, String type) {
		try {
			List<Table> tables = new ArrayList<Table>(100);
			ResultSet rs = metaData.getTables(catalog, schema, tableName, StringUtil.isEmpty(type) ? null
					: type.split("[,|;\\s]+"));
			read(rs, tables, Table.class);

			for (final Table table : tables) {

				List<PrimaryKey> primaryKeys = readPrimaryKeys(metaData, table);
				table.setPrimaryKeyList(primaryKeys);

				List<IndexInfo> indexes = readIndexes(metaData, table.getTableCatalog(),
						table.getTableSchema(), table.getTableName());
				table.setIndexInfoList(indexes);

				table.setColumnList(readColumns(metaData, table.getTableCatalog(), table.getTableSchema(),
						table.getTableName(), null));

				table.fit();
			}

			return tables;
		} catch (Exception e) {
			return new ArrayList<Table>();
		}
	}

	public static List<PrimaryKey> readPrimaryKeys(DatabaseMetaData metaData, final Table table)
			throws InstantiationException, IllegalAccessException, SQLException {
		try {
			List<PrimaryKey> primaryKeys = new ArrayList<PrimaryKey>();
			ResultSet rs = metaData.getPrimaryKeys(table.getTableCatalog(), table.getTableSchema(),
					table.getTableName());
			read(rs, primaryKeys, PrimaryKey.class);
			return primaryKeys;
		} catch (Exception e) {
			return new ArrayList<PrimaryKey>();
		}
	}

	public static List<TableType> readTableTypes(DatabaseMetaData metaData) {
		try {
			List<TableType> list = new ArrayList<TableType>(10);
			read(metaData.getTableTypes(), list, TableType.class);
			return list;
		} catch (Exception e) {
			return new ArrayList<TableType>();
		}
	}

	private DBV() {
	}

}
