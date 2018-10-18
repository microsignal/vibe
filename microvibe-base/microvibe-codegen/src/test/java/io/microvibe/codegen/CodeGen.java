package io.microvibe.codegen;

import java.io.File;
import java.io.IOException;

import org.dom4j.DocumentException;

import io.microvibe.codegen.impl.JdbcTablesReader;

public class CodeGen {

	public static void main(String[] args)
			throws DocumentException, InstantiationException, IllegalAccessException, IOException {
		TablesReader reader;
		reader = new JdbcTablesReader(new File("./codegen/cfg/mysql.xml"));
		/*
		reader = new DefaultTablesReader(
				new File("./codegen/cfg/mysql-tables.xml"),
				new File("./codegen/cfg/mysql.xml"));
		*/

		CodeGenerator generator;

		generator = new CodeGenerator(reader, "./codegen/cfg/mysql-codegen-sys.xml");
		generator.generate();
		reader.close();
	}

}
