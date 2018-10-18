package io.microvibe.codegen;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import io.microvibe.dbv.DBV;
import io.microvibe.dbv.ExcelTplToolkit;
import io.microvibe.dbv.cfg.Configurations;
import io.microvibe.dbv.cfg.DatabaseCfg;
import io.microvibe.dbv.model.Table;
import io.microvibe.util.DBUtil;

public class DbXlsGen {

	public static void main(String[] args) throws Exception {
		DatabaseCfg cfg = Configurations.getDatabaseCfg("./codegen/cfg/mysql.xml");
		Connection conn = DBV.getConnection(cfg);
		try (InputStream in = ExcelTplToolkit.getTemplateResourceStream();
				OutputStream out = new FileOutputStream("./表结构设计.xlsm")) {
			DatabaseMetaData metaData = conn.getMetaData();
//			List<TableType> types = DBV.readTableTypes(metaData);
//			System.out.println(types);
			List<Table> tables = DBV.readTables(metaData, "mccpdb_dev", null, null, "TABLE");
//			System.out.println(tables);
//			Table table = tables.get(0);
//			System.out.println(table.getPkColumns());
//			System.out.println(table.getRemarks());
//			System.out.println(table.getIndexList());
//			System.out.println(table.getColumnList());

//			System.out.println(table.getPrimaryKeyList().get(0).getPkName());

			XSSFWorkbook book = ExcelTplToolkit.newXSSFWorkbook(in);
			ExcelTplToolkit.addTables(book, tables);
			book.write(out);
			out.flush();
			out.close();

			System.out.println("OK!");
		} finally {
			DBUtil.close(conn);
		}

	}
}
