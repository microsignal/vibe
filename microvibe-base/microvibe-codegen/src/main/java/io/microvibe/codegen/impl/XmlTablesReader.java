package io.microvibe.codegen.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import org.dom4j.DocumentException;

import io.microvibe.codegen.TablesReader;
import io.microvibe.codegen.bean.Tables;
import io.microvibe.codegen.bean.db.Catalog;
import io.microvibe.codegen.bean.db.Schema;
import io.microvibe.codegen.bean.db.Table;
import io.microvibe.util.castor.Marshallers;

public class XmlTablesReader implements TablesReader {

	Tables tables;

	public XmlTablesReader(File xmlDataFile)
			throws DocumentException, InstantiationException, IllegalAccessException, IOException {
		if (!xmlDataFile.exists()) {
			if (xmlDataFile.getParentFile() != null && !xmlDataFile.getParentFile().exists()) {
				xmlDataFile.getParentFile().mkdirs();
			}
			PrintWriter writer = new PrintWriter(xmlDataFile);
			writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			writer.println("<xml></xml>");
			writer.flush();
			writer.close();
		}
		this.tables = Marshallers.unmarshal(Tables.class, new FileInputStream(xmlDataFile));
		//this.tables = (Tables) XmlUtil.readObject(new FileInputStream(xmlDataFile), Tables.class);
	}

	@Override
	public Table read(String catalogName, String schemaName, String tableName) {
		Catalog catalog = tables.getCatalogs().get(catalogName);
		if (catalog != null) {
			Schema schema = catalog.getSchemas().get(schemaName);
			if (schema != null) {
				Table table = schema.getTables().get(tableName);
				if (table != null) {
					table.prepare4Java();
					return table;
				}
			}
		}
		return null;
	}

	@Override
	public void close() {
	}

}
