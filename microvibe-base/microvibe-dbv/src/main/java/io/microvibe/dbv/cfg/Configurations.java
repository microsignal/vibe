package io.microvibe.dbv.cfg;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.microvibe.util.io.IOUtil;
import io.microvibe.util.castor.Marshallers;

public class Configurations {

	private static final Logger logger = LoggerFactory.getLogger(Configurations.class);
	private static final String DATABASE_XML = "/database.xml";

	private static InputStream getDatabaseCfgInputStream() throws FileNotFoundException {
		return IOUtil.getInputStream(DATABASE_XML);
	}

	private static InputStream getDatabaseCfgInputStream(String cfgPath) throws FileNotFoundException {
		return IOUtil.getInputStream(cfgPath);
	}

	private static DatabaseCfg readSqlCfg(DatabaseCfg cfg, String cfgPath)
			throws FileNotFoundException {
		InputStream in = getDatabaseCfgInputStream(cfgPath);
		try {
			cfg = Marshallers.unmarshal(cfg, in);
			return cfg;
		} catch (Exception e) {
			return cfg;
		} finally {
			IOUtil.close(in);
		}
	}

	public static DatabaseCfg getDatabaseCfg(String cfgPath) throws FileNotFoundException {
		InputStream in = Configurations.getDatabaseCfgInputStream(cfgPath);
		return getDatabaseCfg(in);
	}

	public static DatabaseCfg getDatabaseCfg() throws FileNotFoundException {
		InputStream in = Configurations.getDatabaseCfgInputStream();
		return getDatabaseCfg(in);
	}

	public static DatabaseCfg getDatabaseCfg(InputStream in) {
		return getDatabaseCfg(in, new DatabaseCfg());
	}

	public static DatabaseCfg getDatabaseCfg(InputStream in, DatabaseCfg cfg) {
		try {
			cfg = Marshallers.unmarshal(cfg, in);
			{ // 配置参数再加工
				Properties jdbcInfoProperties = cfg.getJdbcInfoProperties();
				if (jdbcInfoProperties == null) {
					jdbcInfoProperties = new Properties();
					cfg.setJdbcInfoProperties(jdbcInfoProperties);
				}
				String jdbcInfoPropertiesPath = cfg.getJdbcInfoPropertiesPath();
				if (jdbcInfoPropertiesPath != null) {
					jdbcInfoProperties.load(IOUtil.getInputStream(jdbcInfoPropertiesPath));
				}
				String jdbcUsername = cfg.getJdbcUsername();
				if (jdbcUsername != null) {
					jdbcInfoProperties.setProperty("user", jdbcUsername);
				}
				String jdbcPassword = cfg.getJdbcPassword();
				if (jdbcPassword != null) {
					jdbcInfoProperties.setProperty("password", jdbcPassword);
				}
			}
			try {
				String databaseType = cfg.getDatabaseType();
				if (databaseType != null) {
					DatabaseType type = DatabaseType.valueOf(databaseType);
					readSqlCfg(cfg, type.getCfgFile());
				}
			} catch (Exception e) {
				logger.debug(e.getMessage(), e);
			}
			return cfg;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		} finally {
			IOUtil.close(in);
		}
	}
}
