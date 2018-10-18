package io.microvibe.booster.core.atomikos;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jdbc.nonxa.AtomikosNonXADataSourceBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @author Qt
 * @since Jul 13, 2018
 */
public class XATest {

	public static void main(String[] args) throws Exception {
		AtomikosNonXADataSourceBean ds1 = new AtomikosNonXADataSourceBean();
//		DruidDataSource ds1 = new DruidDataSource();
		ds1.setUrl("jdbc:mysql://rm-m5eh50s9lj6nw0rk1.mysql.rds.aliyuncs.com:3306/dzwl");
		ds1.setUser("dzwl");
//		ds1.setUsername("dzwl");
		ds1.setPassword("Dzwl123!@#");
		ds1.setDriverClassName("com.mysql.jdbc.Driver");
		ds1.setUniqueResourceName("ds1");

		AtomikosNonXADataSourceBean ds2 = new AtomikosNonXADataSourceBean();
//		DruidDataSource ds2 = new DruidDataSource();
		ds2.setUrl("jdbc:mysql://10.10.1.6:3306/booster");
		ds2.setUser("root");
//		ds2.setUsername("root");
		ds2.setPassword("transnal");
		ds2.setDriverClassName("com.mysql.jdbc.Driver");
		ds2.setUniqueResourceName("ds2");

		AtomikosNonXADataSourceBean ds3 = new AtomikosNonXADataSourceBean();
//		DruidDataSource ds3 = new DruidDataSource();
		ds3.setUrl("jdbc:mysql://localhost:3306/booster");
		ds3.setUser("root");
//		ds3.setUsername("root");
		ds3.setPassword("root");
		ds3.setDriverClassName("com.mysql.jdbc.Driver");
		ds3.setUniqueResourceName("ds3");


		String timesign = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		System.out.println(timesign);

		UserTransactionImp userTransactionImp = new UserTransactionImp();
		UserTransactionManager userTransactionManager = new UserTransactionManager();
		JtaTransactionManager jtaTransactionManager = new JtaTransactionManager(userTransactionImp,userTransactionManager);

		TransactionTemplate template = new TransactionTemplate(jtaTransactionManager);
		String rs = template.execute(status -> {
			{
				JdbcTemplate jdbc = new JdbcTemplate(ds1);
				jdbc.update("update sys_user set intro = 'xa-1 @"+timesign+"' where id = '1'");
			}
			{
				JdbcTemplate jdbc = new JdbcTemplate(ds2);
				jdbc.update("update sys_user set intro = 'xa-2 @"+timesign+"' where id = '1'");
			}

			{
				JdbcTemplate jdbc = new JdbcTemplate(ds3);
				jdbc.update("update sys_user set intro = 'xa-3 @"+timesign+"' where id = '1'");
			}
//			System.out.println(1/0);

			return "ok";
		});
		System.out.println(rs);

		{
			JdbcTemplate jdbc = new JdbcTemplate(ds1);
			Map<String, Object> map = jdbc.queryForMap("select intro from sys_user where id = '1' ");
			System.out.println(JSON.toJSONString(map,true));

		}
		{
			JdbcTemplate jdbc = new JdbcTemplate(ds2);
			Map<String, Object> map = jdbc.queryForMap("select intro from sys_user where id = '1' ");
			System.out.println(JSON.toJSONString(map,true));
		}

		ds1.close();
		ds2.close();
	}
}
