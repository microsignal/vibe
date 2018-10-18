package io.microvibe.booster.core.base.mybatis.configuration;

import io.microvibe.booster.core.base.mybatis.annotation.AutoMapper;
import io.microvibe.booster.core.base.mybatis.annotation.SuperMapper;
import io.microvibe.booster.core.base.mybatis.mapping.AutoEntityMapper;
import io.microvibe.booster.core.env.BootConstants;
import lombok.Setter;
import org.apache.ibatis.builder.IncompleteElementException;
import org.apache.ibatis.builder.annotation.MethodResolver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.data.domain.Persistable;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class PersistentEnhancerScanner implements ApplicationListener<ApplicationEvent> {
	public static final String BEAN_ID = "persistentEnhancerScaner";

	/**
	 * 初始化参数:mapper package base place
	 */
	@Setter
	private String mapperPackage = BootConstants.BASE_PACKAGE_MAPPER;

	/**
	 * 初始化参数:entity package base place
	 */
	@Setter
	private String entityPackage = BootConstants.ENTITY_PACKAGES_TO_SCAN;

	/**
	 * 初始化参数:sqlSessionFactory
	 */
	@Setter
	private SqlSessionFactory sqlSessionFactory;

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (!(event instanceof ContextRefreshedEvent)) {
			return;
		}
		// root application context 没有parent.
		if (((ContextRefreshedEvent) event).getApplicationContext().getParent() != null) {
			return;
		}

		// mybatis configuration
		Configuration configuration = this.sqlSessionFactory.getConfiguration();


		/** scan entity **/
		SpringClassScanner entityScanner = new SpringClassScanner.Builder().scanPackage(this.entityPackage)
			.typeFilter(new AnnotationTypeFilter(Entity.class, false))
			.typeFilter(new AnnotationTypeFilter(Table.class, false))
			.typeFilter(new AssignableTypeFilter(Persistable.class))
			.filterAll(false).build();
//			.typeFilter(new AnnotationTypeFilter(Entity.class, false)).build();

		try {
			Set<Class<?>> entitySet = entityScanner.scan();
			if (entitySet != null && !entitySet.isEmpty()) {
				for (Class<?> entity : entitySet) {
					if (entity.isInterface() || Modifier.isAbstract(entity.getModifiers())
						|| entity.isAnnotationPresent(MappedSuperclass.class)) {
						continue;
					}
					// build
					PersistentEnhancerBuilder.buildResultMap(configuration, entity);
					PersistentEnhancerBuilder.buildResultMap(configuration, entity.getName(), entity);

					// resultMap enhance
					PersistentResultMapEnhancer resultMapEnhancer =
						new PersistentResultMapEnhancer(configuration, entity);
					resultMapEnhancer.enhance();
				}
				// parsePendingMethods(configuration);
			}
		} catch (ClassNotFoundException | IOException e) {
			// log or throw
			throw new RuntimeException(e);
		} catch (RuntimeException e) {
			throw e;
		}

		/** scan **/
		SpringClassScanner scanner = new SpringClassScanner.Builder().scanPackage(this.mapperPackage)
			.typeFilter(new AnnotationTypeFilter(AutoMapper.class, false))
			.typeFilter(new AssignableTypeFilter(AutoEntityMapper.class))
			.filterAll(false).build();
		try {
			Set<Class<?>> mapperSet = scanner.scan();
			if (mapperSet != null && !mapperSet.isEmpty()) {
				for (Class<?> mapper : mapperSet) {
					if (mapper.isAnnotationPresent(SuperMapper.class)) {
						continue;
					}
					// mapper enhance
					PersistentMapperEnhancer mapperEnhancer =
						new PersistentMapperEnhancer(configuration, mapper);
					mapperEnhancer.enhance();
				}
				parsePendingMethods(configuration);
			}
		} catch (ClassNotFoundException | IOException e) {
			// log or throw runTimeExp
			throw new RuntimeException(e);
		} catch (RuntimeException e) {
			throw e;
		}


	}


	private void parsePendingMethods(Configuration configuration) {
		Collection<MethodResolver> incompleteMethods = configuration.getIncompleteMethods();
		synchronized (incompleteMethods) {
			Iterator<MethodResolver> iter = incompleteMethods.iterator();
			while (iter.hasNext()) {
				try {
					iter.next().resolve();
					iter.remove();
				} catch (IncompleteElementException e) {
					// This method is still missing a resource
				}
			}
		}
	}

}
