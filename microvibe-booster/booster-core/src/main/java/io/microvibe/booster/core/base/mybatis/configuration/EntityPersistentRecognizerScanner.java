package io.microvibe.booster.core.base.mybatis.configuration;

import io.microvibe.booster.core.base.persistence.PersistentRecognizer;
import io.microvibe.booster.core.env.BootConstants;
import lombok.Setter;
import org.springframework.context.ApplicationContext;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.data.domain.Persistable;

import javax.annotation.PostConstruct;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Set;

/**
 * @author Qt
 * @since Jun 20, 2018
 */
public class EntityPersistentRecognizerScanner {
	public static final String BEAN_ID = "entityPersistentRecognizerScanner";


	/**
	 * 初始化参数:entity package base place
	 */
	@Setter
	private String entityPackage = BootConstants.ENTITY_PACKAGES_TO_SCAN;

	@Setter
	private  ApplicationContext applicationContext;

	public EntityPersistentRecognizerScanner(){
	}


	@PostConstruct
	public final void init(){
		/** scan entity **/
		SpringClassScanner entityScanner = new SpringClassScanner.Builder().scanPackage(this.entityPackage)
			.typeFilter(new AnnotationTypeFilter(Entity.class, false))
			.typeFilter(new AnnotationTypeFilter(Table.class, false))
			.typeFilter(new AssignableTypeFilter(Persistable.class))
			.filterAll(false).build();
		/*SpringClassScanner entityScanner = new SpringClassScanner.Builder().scanPackage(this.entityPackage)
			.typeFilter(new AnnotationTypeFilter(Entity.class, false)).build();*/
		try {
			Set<Class<?>> entitySet = entityScanner.scan();
			if (entitySet != null && !entitySet.isEmpty()) {
				for (Class<?> entity : entitySet) {
					if (entity.isInterface() || Modifier.isAbstract(entity.getModifiers())
						|| entity.isAnnotationPresent(MappedSuperclass.class)) {
						continue;
					}
					PersistentRecognizer.entityMetaData(entity);
				}
			}
		} catch (ClassNotFoundException | IOException e) {
			// log or throw
			throw new RuntimeException(e);
		}
	}
}
