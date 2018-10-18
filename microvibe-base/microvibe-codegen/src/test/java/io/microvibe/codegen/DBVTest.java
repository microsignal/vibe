package io.microvibe.codegen;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;

import org.junit.Test;

import io.microvibe.dbv.DBV;
import io.microvibe.dbv.cfg.Configurations;
import io.microvibe.dbv.cfg.DatabaseCfg;
import io.microvibe.dbv.model.Table;
import io.microvibe.dbv.model.TableType;
import io.microvibe.util.DBUtil;
import io.microvibe.util.castor.MarshallerEnv;
import io.microvibe.util.castor.Marshallers;

public class DBVTest {


	@Test
	public void testCfg() throws FileNotFoundException {
		MarshallerEnv.setAttributePrefer(true);
		DatabaseCfg databaseCfg = Configurations.getDatabaseCfg();
		System.out.println(databaseCfg.getSqlmap());
		System.out.println(Marshallers.marshal(databaseCfg));
		System.out.println(Marshallers.marshal(Marshallers.unmarshal(databaseCfg, Marshallers.marshal(databaseCfg))));
	}

	@Test
	public void testDbv() throws FileNotFoundException, SQLException{
		DatabaseCfg cfg = Configurations.getDatabaseCfg("./codegen/cfg/mysql.xml");
		Connection conn = DBV.getConnection(cfg);
		try{
			DatabaseMetaData metaData = conn.getMetaData();
			List<TableType> types = DBV.readTableTypes(metaData);
			System.out.println(types);
			List<Table> tables = DBV.readTables(metaData, "mccpdb_dev", null, "sys_version", "TABLE");
			System.out.println(tables);
			Table table = tables.get(0);
			System.out.println(table.getPkColumns());
			System.out.println(table.getRemarks());
			System.out.println(table.getIndexList());
			System.out.println(table.getColumnList());
		}finally{
			DBUtil.close(conn);
		}
	}

}
