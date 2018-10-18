package io.microvibe.booster.config.database;

import io.microvibe.booster.commons.spring.AfterApplicationContextHolder;
import io.microvibe.booster.config.task.AfterTaskConfig;
import io.microvibe.booster.core.base.datasource.EntityManagerFactoryProxyBuilder;
import io.microvibe.booster.core.base.utils.SpringPropertiesConfigurations;
import io.microvibe.booster.core.env.BootConstants;
import io.microvibe.booster.core.env.HibernateEnv;
import io.microvibe.booster.core.env.SpringJpaEnv;
import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.*;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.sql.DataSource;
import javax.transaction.UserTransaction;
import javax.validation.Validator;
import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("ALL")
@Configuration
public class JpaConfig {

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

	@Bean
	public PersistenceProvider persistenceProvider() {
		return new HibernatePersistenceProvider();
	}

	@Bean
	public JpaDialect jpaDialect() {
		return new HibernateJpaDialect();
	}

	@Bean
	@ConfigurationProperties("spring.jpa")
	public JpaVendorAdapter jpaVendorAdapter() {
		return new HibernateJpaVendorAdapter();
	}

	@Bean
	@ConfigurationProperties("spring.jpa")
	public JpaProperties jpaProperties() {
		return new JpaProperties();
	}

	@Bean
	@AfterApplicationContextHolder
	@AfterTaskConfig
	public LocalContainerEntityManagerFactoryBean defaultEntityManagerFactory() {
		LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
		factoryBean.setDataSource(context.getBean(DataSource.class));
		factoryBean.setPackagesToScan(BootConstants.ENTITY_PACKAGES_TO_SCAN);
		factoryBean.setPersistenceUnitName(springJpaEnv.getPersistenceUnitName());
		factoryBean.setPersistenceProvider(persistenceProvider());
		factoryBean.setJpaVendorAdapter(jpaVendorAdapter());
		factoryBean.setJpaDialect(jpaDialect());
		Map<String, Object> jpaPropertyMap = createJpaPropertyMap();
		JpaProperties jpaProperties = jpaProperties();
		jpaPropertyMap.putAll(jpaProperties.getHibernateProperties(context.getBean(DataSource.class)));
		factoryBean.setJpaPropertyMap(jpaPropertyMap);
		factoryBean.afterPropertiesSet();
		return factoryBean;
	}

	private JpaVendorAdapter createJpaVendorAdapter(String prefix) {
		JpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
		SpringPropertiesConfigurations.bindProperties(context, "spring.jpa", jpaVendorAdapter);
		SpringPropertiesConfigurations.bindProperties(context, prefix, jpaVendorAdapter);
		return jpaVendorAdapter;
	}

	private JpaProperties createJpaProperties(String prefix) {
		JpaProperties jpaProperties = new JpaProperties();
		SpringPropertiesConfigurations.bindProperties(context, "spring.jpa", jpaProperties);
		SpringPropertiesConfigurations.bindProperties(context, prefix, jpaProperties);
		return jpaProperties;
	}

	private EntityManagerFactory createEntityManagerFactory(String prefix) {
		LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
		factoryBean.setDataSource(context.getBean(DataSource.class));
		factoryBean.setPackagesToScan(BootConstants.ENTITY_PACKAGES_TO_SCAN);
		factoryBean.setPersistenceUnitName(springJpaEnv.getPersistenceUnitName());
		factoryBean.setPersistenceProvider(persistenceProvider());
		factoryBean.setJpaDialect(jpaDialect());
		factoryBean.setJpaVendorAdapter(createJpaVendorAdapter(prefix));
		Map<String, Object> jpaPropertyMap = createJpaPropertyMap();
		JpaProperties jpaProperties = createJpaProperties(prefix);
		jpaPropertyMap.putAll(jpaProperties.getHibernateProperties(context.getBean(DataSource.class)));
		factoryBean.setJpaPropertyMap(jpaPropertyMap);
		factoryBean.afterPropertiesSet();
		return factoryBean.getObject();
	}

	@Primary
	@Bean("entityManagerFactory")
	@AfterApplicationContextHolder
	@AfterTaskConfig
	public EntityManagerFactory entityManagerFactory() {
		Boolean dsDynamic = env.getProperty("spring.datasource.dynamic.enabled", Boolean.class, false);
		Boolean jpaDynamic = env.getProperty("spring.jpa.dynamic.enabled", Boolean.class, false);
		if (dsDynamic.booleanValue() && jpaDynamic.booleanValue()) {
			EntityManagerFactoryProxyBuilder builder = new EntityManagerFactoryProxyBuilder();
			builder.withDefault(defaultEntityManagerFactory().getObject());
			String names = env.getProperty("spring.datasource.dynamic.names");
			if (names != null) {
				// 多个数据源
				for (String name : names.split(",")) {
					name = name.trim();
					EntityManagerFactory entityManagerFactory = createEntityManagerFactory("spring.jpa." + name);
					builder.withTarget(name, entityManagerFactory);
				}
			}
			return builder.build();
		} else {
			return defaultEntityManagerFactory().getObject();
		}
	}

	@Bean("entityManager")
	public EntityManager entityManager() {
		EntityManagerFactory entityManagerFactory = entityManagerFactory();
		EntityManager entityManager = SharedEntityManagerCreator.createSharedEntityManager(entityManagerFactory);
		return entityManager;
	}

	@Bean(name = "userTransactionManager", destroyMethod = "close", initMethod = "init")
	@ConditionalOnProperty(name = "spring.datasource.dynamic.jta", havingValue = "true")
	public UserTransactionManager userTransactionManager() {
		UserTransactionManager userTransactionManager = new UserTransactionManager();
		userTransactionManager.setForceShutdown(false);
		return userTransactionManager;
	}

	@Bean(name = "userTransaction")
	@ConditionalOnProperty(name = "spring.datasource.dynamic.jta", havingValue = "true")
	public UserTransaction userTransaction() throws Throwable {
		UserTransactionImp userTransactionImp = new UserTransactionImp();
		userTransactionImp.setTransactionTimeout(10000);
		return userTransactionImp;
	}

	@Bean("transactionManager")
	@ConditionalOnProperty(name = "spring.datasource.dynamic.jta", havingValue = "true")
	public JtaTransactionManager jtaTransactionManager() throws Throwable {
		JtaTransactionManager manager = new JtaTransactionManager(userTransaction(), userTransactionManager());
		manager.setAllowCustomIsolationLevels(true);
		return manager;
	}

	@Bean("transactionManager")
	@ConditionalOnProperty(name = "spring.datasource.dynamic.jta", havingValue = "false", matchIfMissing = true)
	public PlatformTransactionManager transactionManager() {
		EntityManagerFactory entityManagerFactory = entityManagerFactory();
		JpaTransactionManager transactionManager = new JpaTransactionManager(entityManagerFactory);
		return transactionManager;
	}

	private Map<String, Object> createJpaPropertyMap() {
		Map<String, Object> jpaPropertyMap = new LinkedHashMap<>();
		// 使用自定义的validator进行jsr303验证
		jpaPropertyMap.put("javax.persistence.validation.factory", validator);
		// jsr303验证模式 因为其要么验证 要么不验证 不能按照规则走 所以此处禁用
		// http://docs.jboss.org/hibernate/entitymanager/3.6/reference/en/html/configuration.html
		jpaPropertyMap.put("javax.persistence.validation.mode", "NONE");
		// 只扫描class文件，不扫描hbm，默认两个都搜索
		jpaPropertyMap.put("hibernate.archive.autodetection", "class");
		// 不检查@NamedQuery
		jpaPropertyMap.put("hibernate.archive.query.startup_check", false);
		jpaPropertyMap.put("hibernate.query.substitutions", hibernateEnv.getQuerySubstitutions());
		jpaPropertyMap.put("hibernate.default_batch_fetch_size", hibernateEnv.getDefaultBatchFetchSize());
		jpaPropertyMap.put("hibernate.max_fetch_depth", hibernateEnv.getMaxFetchDepth());
		jpaPropertyMap.put("hibernate.generate_statistics", hibernateEnv.isGenerateStatistics());
		jpaPropertyMap.put("hibernate.bytecode.use_reflection_optimizer", hibernateEnv.isBytecodeUseReflectionOptimizer());
		jpaPropertyMap.put("hibernate.cache.use_second_level_cache", hibernateEnv.isUseSecondLevelCache());
		jpaPropertyMap.put("hibernate.cache.use_query_cache", hibernateEnv.isUseQueryCache());
		jpaPropertyMap.put("hibernate.cache.use_structured_entries", hibernateEnv.isCacheUseStructuredEntries());
		jpaPropertyMap.put("hibernate.implicit_naming_strategy", springJpaEnv.getHibernateNamingImplicitStrategy());
		jpaPropertyMap.put("hibernate.physical_naming_strategy", springJpaEnv.getHibernateNamingPhysicalStrategy());
		jpaPropertyMap.put("hibernate.cache.region.factory_class", hibernateEnv.getCacheRegionFactoryClass());
		return jpaPropertyMap;
	}


}
