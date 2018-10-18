package io.microvibe.codegen.impl;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import org.dom4j.DocumentException;

import io.microvibe.codegen.bean.db.Catalog;
import io.microvibe.codegen.bean.db.Schema;
import io.microvibe.codegen.bean.db.Table;
import io.microvibe.util.io.IOUtil;
import io.microvibe.util.castor.Marshallers;

public class DefaultTablesReader extends XmlTablesReader {

	private JdbcTablesReader jdbcTablesReader;
	private File xmlDataFile;

	public DefaultTablesReader(File xmlDataFile, File jdbcCfgFile)
			throws DocumentException, InstantiationException, IllegalAccessException, IOException {
		super(xmlDataFile);
		this.xmlDataFile = xmlDataFile;
		this.jdbcTablesReader = new JdbcTablesReader(jdbcCfgFile);
	}

	@Override
	public Table read(String catalogName, String schemaName, String tableName) {
		Table table = super.read(catalogName, schemaName, tableName);
		if (table == null) {
			table = jdbcTablesReader.read(catalogName, schemaName, tableName);
			if (table != null) {
				// 添加到xml中
				Catalog catalog = super.tables.getCatalogs().get(catalogName);
				if (catalog == null) {
					catalog = new Catalog();
					catalog.setName(catalogName);
					super.tables.getCatalogs().put(catalogName, catalog);
				}
				Schema schema = catalog.getSchemas().get(schemaName);
				if (schema == null) {
					schema = new Schema();
					schema.setName(schemaName);
					catalog.getSchemas().put(schemaName, schema);
				}
				schema.getTables().put(tableName, table);
				return table;
			}
		}
		return table;
	}

	@Override
	public void close() {
		this.jdbcTablesReader.close();
		Writer writer = null;
		try {
			writer = new PrintWriter(xmlDataFile);
			Marshallers.marshal(super.tables, writer);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtil.close(writer);
		}
	}
}
