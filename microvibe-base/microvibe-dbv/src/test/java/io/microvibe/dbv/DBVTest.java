package io.microvibe.dbv;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import io.microvibe.dbv.cfg.DatabaseCfg;
import io.microvibe.dbv.model.Table;

public class DBVTest {

	@Test
	public void test()   {
		try {
			Connection conn = DBV.getConnection("/test.xml");
			List<Table> tables = DBV.readTables(conn.getMetaData(), "booster", null, null, null);
			System.out.println(tables.size());
			System.out.println(tables.get(0).getColumnList());

			InputStream in = ExcelTplToolkit.getTemplateResourceStream();
			XSSFWorkbook book = ExcelTplToolkit.newXSSFWorkbook(in);
			ExcelTplToolkit.addTables(book, tables);
			FileOutputStream out = new FileOutputStream("./test.xlsm");
			book.write(out);

			out.flush();
			out.close();
			book.close();


		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
