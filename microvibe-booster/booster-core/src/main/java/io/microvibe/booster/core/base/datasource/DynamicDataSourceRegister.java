package io.microvibe.booster.core.base.datasource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 动态数据源注册<br/>
 * 启动动态数据源请在启动类中（如@SpringBootApplication）
 * 添加 @Import(DynamicDataSourceRegister.class)
 */
@Slf4j
@Deprecated
public class DynamicDataSourceRegister
	implements ApplicationContextAware, EnvironmentAware, ImportBeanDefinitionRegistrar, BeanDefinitionRegistryPostProcessor {

	private ApplicationContext applicationContext;
	private Environment environment;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	private void init(BeanDefinitionRegistry registry) {
		DynamicDataSourceFactory factory = new DynamicDataSourceFactory();
		if (applicationContext != null) {
			factory.setApplicationContext(applicationContext);
		}
		if (environment != null) {
			factory.setEnvironment(environment);
		}
		factory.init();

		// 创建DynamicDataSource
		GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
		beanDefinition.setBeanClass(DynamicDataSource.class);
		beanDefinition.setSynthetic(true);
		beanDefinition.setPrimary(true);
		MutablePropertyValues mpv = beanDefinition.getPropertyValues();
		mpv.addPropertyValue("defaultTargetDataSource", factory.getDefaultTargetDataSource());
		mpv.addPropertyValue("targetDataSources", factory.getTargetDataSources());
		registry.registerBeanDefinition("dataSource", beanDefinition);

		log.info("Dynamic DataSource Registry");
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		init(registry);
	}

	/**
	 * ImportBeanDefinitionRegistrar 接口实现
	 */
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		init(registry);
	}

}
