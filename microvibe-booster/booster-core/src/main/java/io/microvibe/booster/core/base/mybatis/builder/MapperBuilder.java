package io.microvibe.booster.core.base.mybatis.builder;

import io.microvibe.booster.core.base.persistence.EntityMetaData;
import io.microvibe.booster.core.base.persistence.PersistentRecognizer;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import static io.microvibe.booster.core.base.mybatis.builder.MapperBuilders.*;

public class MapperBuilder {

	private static final Map<Method, String> sqlSources
		= Collections.synchronizedMap(new WeakHashMap<>());

	private static String __build__(ProviderContext context, Object params, EntityBuilder entityBuilder) {
		//Map<String, Object>
		Method mapperMethod = context.getMapperMethod();
		String source = sqlSources.get(mapperMethod);
		if (source != null) {
			return source;
		}

		synchronized (mapperMethod) {
			source = sqlSources.get(mapperMethod);
			if (source != null) {
				return source;
			}
			Class<?> mapperType = context.getMapperType();
			Class<?>[] generics = MapperBuilders.getMapperGenerics(mapperType);
			Class<?> entityClass = generics[0];

			EntityMetaData entityMetaData = PersistentRecognizer.entityMetaData(entityClass);
			source = entityBuilder.build(entityMetaData);
//			source = ScriptWrapper.quoteScript(source);

			sqlSources.put(mapperMethod, source);
			return source;
		}
	}

	public static String build(ProviderContext context, Object params) {
		String methodName = context.getMapperMethod().getName();
		Method method = ReflectionUtils.findMethod(MapperBuilder.class, methodName, ProviderContext.class, Object.class);
		return (String) ReflectionUtils.invokeMethod(method, null, context, params);
	}

	public static String hasOne(ProviderContext context, Object params) {
		return __build__(context, params, entityMetaData -> {
			return buildExistsById(entityMetaData);
		});
	}

	public static String findOne(ProviderContext context, Object params) {
		return __build__(context, params, entityMetaData -> {
			return buildGetById(entityMetaData, entityMetaData.getTableAlias(), true);
		});
	}

	public static String doRemove(ProviderContext context, Object params) {
		return __build__(context, params, entityMetaData -> {
			return buildDeleteById(entityMetaData);
		});
	}

	public static String doModify(ProviderContext context, Object params) {
		return __build__(context, params, entityMetaData -> {
			return buildUpdateById(entityMetaData);
		});
	}

	public static String doAdd(ProviderContext context, Object params) {
		return __build__(context, params, entityMetaData -> {
			return buildInsert(entityMetaData, false);
		});
	}

	public static interface EntityBuilder {
		String build(EntityMetaData entityMetaData);
	}
}
