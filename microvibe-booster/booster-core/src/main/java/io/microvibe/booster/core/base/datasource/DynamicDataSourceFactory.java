package io.microvibe.booster.core.base.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import io.microvibe.booster.commons.crypto.RSA;
import io.microvibe.booster.commons.utils.StringUtils;
import io.microvibe.booster.core.base.utils.SpringPropertiesConfigurations;
import com.atomikos.jdbc.nonxa.AtomikosNonXADataSourceBean;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.bind.RelaxedDataBinder;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Qt
 * @since Aug 13, 2018
 */
@Slf4j
public class DynamicDataSourceFactory implements ApplicationContextAware, EnvironmentAware, FactoryBean<DynamicDataSource> {
	@Getter
	@Setter
	private DataSource defaultTargetDataSource;
	@Getter
	private Map<String, DataSource> targetDataSources = new HashMap<>();
	private ApplicationContext applicationContext;
	private Environment environment;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
		this.environment = applicationContext.getEnvironment();
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	void init() {
		targetDataSourcesInit();
		defaultTargetDataSourceInit();
	}

	@Override
	public DynamicDataSource getObject() throws Exception {
		init();

		DynamicDataSource dynamicDataSource = new DynamicDataSource();
		dynamicDataSource.setTargetDataSources(new HashMap<>(this.targetDataSources));
		dynamicDataSource.setDefaultTargetDataSource(defaultTargetDataSource);
		dynamicDataSource.afterPropertiesSet();
		return dynamicDataSource;
	}

	/**
	 * 读取主数据源, 将主数据源添加到更多数据源中
	 */
	private void defaultTargetDataSourceInit() {
		// 读取主数据源
		if (defaultTargetDataSource == null) {
			try {
				if (applicationContext != null) {
					DataSource primaryDataSource = applicationContext.getBean(DataSource.class);
					defaultTargetDataSource = primaryDataSource;
				}
			} catch (BeansException e) {
			}
		}
		if (defaultTargetDataSource == null) {
			defaultTargetDataSource = buildDataSource("spring.datasource");
		}
		// 将主数据源添加到更多数据源中
		/*if (!targetDataSources.containsKey("dataSource")) {
			targetDataSources.put("dataSource", defaultTargetDataSource);
			DynamicDataSourceContextHolder.add("dataSource");
		}*/
		if (!targetDataSources.containsKey(TargetDataSource.DEFAULT)) {
			targetDataSources.put(TargetDataSource.DEFAULT, defaultTargetDataSource);
			DynamicDataSourceContextHolder.add(TargetDataSource.DEFAULT);
		}
	}

	/**
	 * 初始化更多数据源
	 */
	private void targetDataSourcesInit() {
		// 读取配置文件获取更多数据源，也可以通过 defaultTargetDataSource 读取数据库获取更多数据源
		RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(environment, "spring.datasource.dynamic.");
		String names = environment.getProperty("spring.datasource.dynamic.names");
		if (names != null) {
			// 多个数据源
			for (String name : names.split(",")) {
				name = name.trim();
				DataSource ds = buildDataSource("spring.datasource." + name);
				bindExtendedValues(ds);
				targetDataSources.put(name, ds);
				DynamicDataSourceContextHolder.add(name);
			}
		}
		String defaultName = propertyResolver.getProperty("default");
		if (defaultTargetDataSource == null && defaultName != null && targetDataSources.containsKey(defaultName)) {
			defaultTargetDataSource = targetDataSources.get(defaultName);
		}
		/*if (!targetDataSources.containsKey("memory") && applicationContext != null) {
			try {
				DataSource memoryDataSource = applicationContext.getBean("memoryDataSource", DataSource.class);
				targetDataSources.put("memory", memoryDataSource);
			} catch (BeansException e) {
			}
		}*/
	}

	private DataSource buildDataSource(String propertiesPrefix) {
		try {
			Map<String, Object> props = new RelaxedPropertyResolver(environment, propertiesPrefix)
				.getSubProperties(".");

			String typeName = StringUtils.trimToNull((String) props.get("type"));
			String url = StringUtils.trimToNull((String) props.get("url"));
			String username = StringUtils.trimToNull((String) props.get("username"));
			String password = StringUtils.trimToNull((String) props.get("password"));
			String driverClassName = StringUtils.trimToNull((String) props.get("driverClassName"));
			if (driverClassName == null) {
				driverClassName = StringUtils.trimToNull((String) props.get("driver-class-name"));
			}

			if (!"spring.datasource".equalsIgnoreCase(propertiesPrefix)) {
				if (typeName == null) {
					typeName = StringUtils.trimToNull(environment.getProperty("spring.datasource.type"));
				}
				if (url == null) {
					url = StringUtils.trimToNull(environment.getProperty("spring.datasource.url"));
				}
				if (username == null) {
					username = StringUtils.trimToNull(environment.getProperty("spring.datasource.username"));
				}
				if (password == null) {
					password = StringUtils.trimToNull(environment.getProperty("spring.datasource.password"));
				}
				if (driverClassName == null) {
					driverClassName = StringUtils.trimToNull(environment.getProperty("spring.datasource.driverClassName"));
				}
				if (driverClassName == null) {
					driverClassName = StringUtils.trimToNull(environment.getProperty("spring.datasource.driver-class-name"));
				}
			}

			url = environment.resolvePlaceholders(url);
			username = environment.resolvePlaceholders(username);
			password = environment.resolvePlaceholders(password);
			driverClassName = environment.resolvePlaceholders(driverClassName);

			// type
			Class<? extends DataSource> type = null;
			if (typeName == null) {
				if (environment.getProperty("spring.datasource.dynamic.jta", Boolean.class, false)) {
					type = AtomikosNonXADataSourceBean.class;
				} else {
					type = DruidDataSource.class;
				}
			} else {
				typeName = environment.resolvePlaceholders(typeName);
				type = (Class<? extends DataSource>) Class.forName(environment.resolvePlaceholders(typeName));
			}

			// password
			password = environment.resolvePlaceholders(password);
			if (Boolean.parseBoolean(environment.resolvePlaceholders((String) props.getOrDefault("password.encrypt", "false")))) {
				if (props.containsKey("password.publickey")) {
					password = RSA.decrypt(RSA.getPublicKey(environment.resolvePlaceholders((String) props.get("password.publickey"))), password);
				} else if (props.containsKey("password.privatekey")) {
					password = RSA.decrypt(RSA.getPrivateKey(environment.resolvePlaceholders((String) props.get("password.privatekey"))), password);
				} else {
					throw new IllegalArgumentException(propertiesPrefix + ".password.encrypt");
				}
			}
			DataSource dataSource = DataSourceBuilder.create()
				.driverClassName(driverClassName).url(url)
				.username(username).password(password).type(type).build();
			SpringPropertiesConfigurations.bindProperties(applicationContext, propertiesPrefix, dataSource);
			return dataSource;
		} catch (BeansException e) {
			throw e;
		} catch (Exception e) {
			throw new BeanCreationException(e.getMessage(), e);
		}
	}


	/**
	 * 为DataSource绑定更多数据
	 */
	private void bindExtendedValues(DataSource dataSource) {
		Map<String, Object> values = new HashMap<>();
		// add property values
		{
			List<String> prefixes = Arrays.asList("spring.datasource",
				"spring.datasource.dynamic.default");
			for (String prefix : prefixes) {
				RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(environment, prefix);
				Map<String, Object> props = propertyResolver.getSubProperties(".");
				values.putAll(props);
			}
			// 排除已经设置的属性
			values.remove("type");
			values.remove("driver-class-name");
			values.remove("driverClassName");
			values.remove("url");
			values.remove("username");
			values.remove("password");
		}
		RelaxedDataBinder dataBinder = new RelaxedDataBinder(dataSource);
		dataBinder.setConversionService(SpringPropertiesConfigurations.getConversionService(applicationContext));
		dataBinder.setIgnoreNestedProperties(false);
		dataBinder.setIgnoreInvalidFields(false);
		dataBinder.setIgnoreUnknownFields(true);
		dataBinder.setDisallowedFields("type", "driverClassName", "url", "username", "password");
		dataBinder.bind(new MutablePropertyValues(values));
	}

	@Override
	public Class<?> getObjectType() {
		return DynamicDataSource.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
