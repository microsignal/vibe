package io.microvibe.booster.config.database;

import io.microvibe.booster.commons.spring.AfterApplicationContextHolder;
import io.microvibe.booster.config.task.AfterTaskConfig;
import io.microvibe.booster.core.base.repository.support.SimpleBaseRepositoryFactoryBean;
import io.microvibe.booster.core.env.BootConstants;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan({BootConstants.ENTITY_PACKAGES_TO_SCAN})
@EnableJpaRepositories(
	basePackages = BootConstants.BASE_PACKAGE_REPOSITORY,
	repositoryImplementationPostfix = "Impl",
	repositoryFactoryBeanClass = SimpleBaseRepositoryFactoryBean.class,
	entityManagerFactoryRef = "entityManagerFactory",
	transactionManagerRef = "transactionManager"
)
public class JpaRepositoriesConfig {
/*
<jpa:repositories
	base-package="com.antengine.**.repository"
	repository-impl-postfix="Impl"
	factory-class="io.microvibe.booster.core.base.repository.support.SimpleBaseRepositoryFactoryBean"
	entity-manager-factory-ref="entityManagerFactory"
	transaction-manager-ref="transactionManager">
</jpa:repositories>
*/
}
