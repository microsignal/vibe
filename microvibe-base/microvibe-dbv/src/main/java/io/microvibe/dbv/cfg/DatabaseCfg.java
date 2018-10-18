package io.microvibe.dbv.cfg;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import io.microvibe.util.castor.Constants;
import io.microvibe.util.castor.annotation.XComplexKey;
import io.microvibe.util.castor.annotation.XComplexType;
import io.microvibe.util.castor.annotation.XComplexValue;
import io.microvibe.util.castor.annotation.XIgnore;
import io.microvibe.util.castor.annotation.XName;

public class DatabaseCfg {
	private String databaseType;
	private String jdbcDriver;
	private String jdbcUrl;
	private String jdbcUsername;
	private String jdbcPassword;
	private String jdbcInfoPropertiesPath;

	@XComplexType(Constants.DefaultMapEntry.class)
	@XComplexKey(Constants.DEFAULT_MAP_COMPLEX_KEY)
	@XComplexValue(Constants.DEFAULT_MAP_COMPLEX_VALUE)
	private Properties jdbcInfoProperties = new Properties();

	@XName("sql")
	@XComplexType(SqlCfg.class)
	@XComplexKey("id")
	private Map<String, SqlCfg> sqlmap = new LinkedHashMap<String, SqlCfg>();

	public String getDatabaseType() {
		return databaseType;
	}

	public void setDatabaseType(String databaseType) {
		this.databaseType = databaseType;
	}

	public Map<String, SqlCfg> getSqlmap() {
		return sqlmap;
	}

	public void setSqlmap(Map<String, SqlCfg> sqlmap) {
		this.sqlmap = sqlmap;
	}

	public String getJdbcDriver() {
		return jdbcDriver;
	}

	public void setJdbcDriver(String jdbcDriver) {
		this.jdbcDriver = jdbcDriver;
	}

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}

	public String getJdbcUsername() {
		return jdbcUsername;
	}

	public void setJdbcUsername(String jdbcUsername) {
		this.jdbcUsername = jdbcUsername;
	}

	public String getJdbcPassword() {
		return jdbcPassword;
	}

	public void setJdbcPassword(String jdbcPassword) {
		this.jdbcPassword = jdbcPassword;
	}

	public String getJdbcInfoPropertiesPath() {
		return jdbcInfoPropertiesPath;
	}

	public void setJdbcInfoPropertiesPath(String jdbcInfoPropertiesPath) {
		this.jdbcInfoPropertiesPath = jdbcInfoPropertiesPath;
	}

	public Properties getJdbcInfoProperties() {
		return jdbcInfoProperties;
	}

	public void setJdbcInfoProperties(Properties jdbcInfoProperties) {
		this.jdbcInfoProperties = jdbcInfoProperties;
	}

}
