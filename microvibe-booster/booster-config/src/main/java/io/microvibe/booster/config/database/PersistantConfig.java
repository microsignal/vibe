package io.microvibe.booster.config.database;

import com.alibaba.druid.pool.DruidDataSource;
import io.microvibe.booster.commons.spring.AfterApplicationContextHolder;
import io.microvibe.booster.core.base.datasource.DynamicDataSourceFactory;
import io.microvibe.booster.core.env.HibernateEnv;
import io.microvibe.booster.core.env.SpringJpaEnv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;
import javax.validation.Validator;

/**
 * @author Qt
 * @since Aug 12, 2018
 */
@Configuration
@SuppressWarnings({"SpringJavaInjectionPointsAutowiringInspection"})
public class PersistantConfig {

	@Autowired
	private ApplicationContext context;
	@Autowired
	private Environment env;
	@Autowired
	private Validator validator;
	@Autowired
	private HibernateEnv hibernateEnv;
	@Autowired
	private SpringJpaEnv springJpaEnv;

	@Bean("druidDataSource")
	@ConfigurationProperties("spring.datasource.druid")
	public DruidDataSource druidDataSource() {
		return new DruidDataSource();
	}

	@Primary
	@Bean("dataSource")
	@DependsOn({"druidDataSource"})
	@ConditionalOnProperty(
		prefix = "spring.datasource.dynamic", name = "enabled", havingValue = "false", matchIfMissing = true
	)
	public DataSource dataSourceProxy() {
		return new TransactionAwareDataSourceProxy(druidDataSource());
	}

	@Primary
	@Bean("dataSource")
	@DependsOn({"druidDataSource"})
	@ConditionalOnProperty(
		prefix = "spring.datasource.dynamic", name = "enabled", havingValue = "true"
	)
	public DataSource dynamicDataSource() throws Exception {
		DynamicDataSourceFactory factory = new DynamicDataSourceFactory();
		factory.setApplicationContext(context);
		factory.setDefaultTargetDataSource(druidDataSource());
		return new TransactionAwareDataSourceProxy(factory.getObject());
	}


	@Bean
	@Primary
	@ConditionalOnMissingBean(JdbcOperations.class)
	public JdbcTemplate jdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	@Bean
	@Primary
	@ConditionalOnMissingBean(NamedParameterJdbcOperations.class)
	public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource dataSource) {
		return new NamedParameterJdbcTemplate(dataSource);
	}


}
