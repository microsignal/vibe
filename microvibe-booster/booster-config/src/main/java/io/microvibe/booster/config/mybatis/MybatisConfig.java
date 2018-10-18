package io.microvibe.booster.config.mybatis;

import io.microvibe.booster.core.base.mybatis.configuration.EntityPersistentRecognizerScanner;
import io.microvibe.booster.core.base.mybatis.configuration.PersistentEnhancerScanner;
import io.microvibe.booster.core.base.mybatis.lang.VelocityLangDriver;
import io.microvibe.booster.core.base.mybatis.type.BlankableEnumOrdinalTypeHandler;
import io.microvibe.booster.core.base.mybatis.type.BlankableEnumTypeHandler;
import io.microvibe.booster.core.env.BootConstants;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@EnableConfigurationProperties(MybatisProperties.class)
public class MybatisConfig {
	private final MybatisProperties properties;

	private final Interceptor[] interceptors;

	private final ResourceLoader resourceLoader;

	private final DatabaseIdProvider databaseIdProvider;

	private final List<ConfigurationCustomizer> configurationCustomizers;

	public MybatisConfig(MybatisProperties properties,
						 ObjectProvider<Interceptor[]> interceptorsProvider,
						 ResourceLoader resourceLoader,
						 ObjectProvider<DatabaseIdProvider> databaseIdProvider,
						 ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider) {
		this.properties = properties;
		this.interceptors = interceptorsProvider.getIfAvailable();
		this.resourceLoader = resourceLoader;
		this.databaseIdProvider = databaseIdProvider.getIfAvailable();
		this.configurationCustomizers = configurationCustomizersProvider.getIfAvailable();
	}

	@Bean
	@ConditionalOnProperty("rebel.env.ide")
	@SuppressWarnings({"SpringJavaInjectionPointsAutowiringInspection"})
	public SqlSessionFactory sqlSessionFactory(@Autowired DataSource dataSource) throws Exception {
		SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
		factory.setDataSource(dataSource);
		factory.setVfs(SpringBootVFS.class);
		if (StringUtils.hasText(this.properties.getConfigLocation())) {
			factory.setConfigLocation(this.resourceLoader.getResource(this.properties.getConfigLocation()));
		}
		org.apache.ibatis.session.Configuration configuration = this.properties.getConfiguration();
		if (configuration == null && !StringUtils.hasText(this.properties.getConfigLocation())) {
			configuration = new org.apache.ibatis.session.Configuration();
		}
		if (configuration != null && !CollectionUtils.isEmpty(this.configurationCustomizers)) {
			for (ConfigurationCustomizer customizer : this.configurationCustomizers) {
				customizer.customize(configuration);
			}
		}
		factory.setConfiguration(configuration);
		if (this.properties.getConfigurationProperties() != null) {
			factory.setConfigurationProperties(this.properties.getConfigurationProperties());
		}
		if (!ObjectUtils.isEmpty(this.interceptors)) {
			factory.setPlugins(this.interceptors);
		}
		if (this.databaseIdProvider != null) {
			factory.setDatabaseIdProvider(this.databaseIdProvider);
		}
		if (StringUtils.hasLength(this.properties.getTypeAliasesPackage())) {
			factory.setTypeAliasesPackage(this.properties.getTypeAliasesPackage());
		}
		if (StringUtils.hasLength(this.properties.getTypeHandlersPackage())) {
			factory.setTypeHandlersPackage(this.properties.getTypeHandlersPackage());
		}
		Resource[] resources = this.properties.resolveMapperLocations();
		if (!ObjectUtils.isEmpty(resources)) {
			Set<String> set = new HashSet<>();
			List<Resource> list = new ArrayList<>();
			for (Resource resource : resources) {
				File file = resource.getFile();
				if (file != null) {
					String key = file.getName() + ":" + file.length();
					if (set.contains(key)) {
						continue;// jrebel-duplicated
					}
					set.add(key);
				}
				list.add(resource);
			}

			factory.setMapperLocations(list.toArray(new Resource[list.size()]));
		}

		return factory.getObject();
	}

	@Bean(EntityPersistentRecognizerScanner.BEAN_ID)
	public EntityPersistentRecognizerScanner entityPersistentRecognizerScanner(@Autowired ApplicationContext context) {
		EntityPersistentRecognizerScanner bean = new EntityPersistentRecognizerScanner();
		bean.setEntityPackage(BootConstants.ENTITY_PACKAGES_TO_SCAN);
		bean.setApplicationContext(context);
		return bean;
	}

	@Bean(PersistentEnhancerScanner.BEAN_ID)
	public PersistentEnhancerScanner PersistentEnhancerScaner(@Autowired SqlSessionFactory sqlSessionFactory) {
		addRegistry(sqlSessionFactory);
		PersistentEnhancerScanner bean = new PersistentEnhancerScanner();
		bean.setEntityPackage(BootConstants.ENTITY_PACKAGES_TO_SCAN);
		bean.setMapperPackage(BootConstants.BASE_PACKAGE_MAPPER);
		bean.setSqlSessionFactory(sqlSessionFactory);
		return bean;
	}

	private void addRegistry(@Autowired SqlSessionFactory sqlSessionFactory) {
		org.apache.ibatis.session.Configuration configuration = sqlSessionFactory.getConfiguration();
		configuration.getLanguageRegistry().register(VelocityLangDriver.class);
		configuration.getTypeAliasRegistry().registerAlias(VelocityLangDriver.class);
		configuration.getTypeAliasRegistry().registerAlias(BlankableEnumTypeHandler.class);
		configuration.getTypeAliasRegistry().registerAlias(BlankableEnumOrdinalTypeHandler.class);

		/*TypeHandlerRegistry typeHandlerRegistry = sqlSessionFactory.getConfiguration().getTypeHandlerRegistry();
		typeHandlerRegistry.register(BigDecimalArrayTypeHandler.class);
		typeHandlerRegistry.register(BooleanArrayTypeHandler.class);
		typeHandlerRegistry.register(CharacterArrayTypeHandler.class);
		typeHandlerRegistry.register(FloatArrayTypeHandler.class);
		typeHandlerRegistry.register(IntegerArrayTypeHandler.class);
		typeHandlerRegistry.register(LongArrayTypeHandler.class);
		typeHandlerRegistry.register(ShortArrayTypeHandler.class);
		typeHandlerRegistry.register(StringArrayTypeHandler.class);*/
	}
}
