package io.microvibe.booster.core.base.mybatis.configuration;

import io.microvibe.booster.core.base.mybatis.mapping.EntityMapper;
import io.microvibe.booster.core.base.mybatis.builder.MapperBuilders;
import io.microvibe.booster.core.base.mybatis.MybatisConstants;
import io.microvibe.booster.core.base.mybatis.annotation.AutoMapper;
import io.microvibe.booster.core.base.mybatis.annotation.AutoStatement;
import io.microvibe.booster.core.base.mybatis.statement.StatementBuilder;
import io.microvibe.booster.core.base.mybatis.statement.BuilderType;
import io.microvibe.booster.core.base.mybatis.statement.StatementBuilders;
import io.microvibe.booster.core.base.persistence.EntityMetaData;
import io.microvibe.booster.core.base.persistence.PersistentRecognizer;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.BaseBuilder;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.session.Configuration;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;

/**
 * Persistent Mapper Enhancer(增强器)</br>
 * <p>
 * 由Mybatis负责创建Mapper接口的代理</br>
 * 本类负责:</br>
 * 1.解析Method,创建并注册Mybatis MappedStatement
 */
@Slf4j
public class PersistentMapperEnhancer extends BaseBuilder {

	/**
	 * mybatis
	 */
	protected MapperBuilderAssistant assistant;

	/**
	 * mybatis mapper接口类型
	 */
	protected Class<?> mapper;

	/**
	 * 持久化Entity类型
	 */
	protected Class<?> entityClass;

	/**
	 * 持久化Entity元数据
	 */
	protected EntityMetaData entityMetaData;

	private String resource;

	/**
	 * 容器中configuration唯一,必须初始化
	 *
	 * @param configuration mybatis configuration{@see Configuration}
	 * @param mapper        mybatis mapper接口类型
	 */
	public PersistentMapperEnhancer(Configuration configuration, Class<?> mapper) {
		super(configuration);
		// String resource = mapper.getName().replace(".", "/") + ".java (auto mapper)";
		this.resource = "auto-mapper:" + mapper.getName();
		this.assistant = new MapperBuilderAssistant(configuration, resource);
		this.mapper = mapper;

		// 获取注解对象
		AutoMapper autoMapper = AnnotationUtils.findAnnotation(mapper, AutoMapper.class);
		if (autoMapper != null) {
			// Entity type
			this.entityClass = autoMapper.entityClass();
		}
		if (this.entityClass == null || this.entityClass.equals(void.class) || this.entityClass.equals(Object.class)) {
			if (EntityMapper.class.isAssignableFrom(mapper)) {
				Class<?>[] mapperGenerics = MapperBuilders.getMapperGenerics(mapper);
				if (mapperGenerics != null && mapperGenerics.length > 0) {
					this.entityClass = mapperGenerics[0];
				}
			}
		}
		if (this.entityClass == null || this.entityClass.equals(void.class) || this.entityClass.equals(Object.class)) {
			// Entity元数据
			this.entityMetaData = null;
		} else {
			// Entity元数据
			this.entityMetaData = PersistentRecognizer.entityMetaData(entityClass);
		}
	}


	/**
	 * mapper增强方法入口
	 */
	public void enhance() {
//		String resource = mapper.toString();
		if (!configuration.isResourceLoaded(resource)) {
			configuration.addLoadedResource(resource);
		}
		assistant.setCurrentNamespace(mapper.getName());

		/*if (!mapper.isAnnotationPresent(AutoEntityMapper.class)) {
			return;
		}*/

		for (Method method : mapper.getMethods()) {
			EntityMetaData entityMetaData = this.entityMetaData;
			AutoStatement autoStatement = AnnotationUtils.findAnnotation(method, AutoStatement.class);
			if (autoStatement != null) {
				// 方法上声明的实体类型可覆盖接口类型上声明的实体类型
				AutoMapper autoMapper = AnnotationUtils.findAnnotation(method, AutoMapper.class);
				if (autoMapper != null) {
					Class<?> entityClass = autoMapper.entityClass();
					if (!entityClass.equals(void.class) && !this.entityClass.equals(Object.class)) {
						entityMetaData = PersistentRecognizer.entityMetaData(entityClass);
					}
				}
				if (entityMetaData != null) {
					BuilderType builderType = autoStatement.value();

					if (builderType == BuilderType.NOOP) {
						// 方法名推断
						String methodName = method.getName();
						if (methodName.startsWith(MybatisConstants.GET)) {
							builderType = BuilderType.GET_BY_ID;
						} else if (methodName.startsWith(MybatisConstants.EXISTS)) {
							builderType = BuilderType.EXISTS_BY_ID;
						} else if (methodName.startsWith(MybatisConstants.DELETE)) {
							builderType = BuilderType.DELETE_BY_ID;
						} else if (methodName.startsWith(MybatisConstants.UPDATE_SELECTIVE)) {
							builderType = BuilderType.UPDATE_SELECTIVE_BY_ID;
						} else if (methodName.startsWith(MybatisConstants.UPDATE)) {
							builderType = BuilderType.UPDATE_BY_ID;
						} else if (methodName.startsWith(MybatisConstants.INSERT_SELECTIVE)) {
							builderType = BuilderType.INSERT_SELECTIVE;
						} else if (methodName.startsWith(MybatisConstants.INSERT_BATCH)) {
							builderType = BuilderType.INSERT_BATCH;
						} else if (methodName.startsWith(MybatisConstants.INSERT)) {
							builderType = BuilderType.INSERT;
						} else if (methodName.startsWith(MybatisConstants.SELECT)) {
							// FIXME 可加入方法命名式条件,参考JPA规则
							builderType = BuilderType.SELECT_BY_MAP;
						}
					}

					StatementBuilder statementBuilder = StatementBuilders.get(builderType);
					if (statementBuilder != null) {
						log.debug("method: {}", method);
						statementBuilder.parseStatement(this.assistant, entityMetaData, this.mapper, method);
					}
				}
			}
		}
	}

}
