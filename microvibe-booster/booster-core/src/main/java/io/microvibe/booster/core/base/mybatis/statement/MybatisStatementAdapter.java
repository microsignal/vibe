package io.microvibe.booster.core.base.mybatis.statement;

import io.microvibe.booster.core.base.mybatis.lang.ScriptWrapper;
import io.microvibe.booster.core.base.mybatis.lang.VelocityLangDriver;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.reflection.TypeParameterResolver;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.*;
import java.util.Collection;
import java.util.Map;

@Setter
@Getter
@Slf4j
public class MybatisStatementAdapter {

	private final MapperBuilderAssistant assistant;//mybatis assistant
	private final Class<?> mapper;
	private final Configuration configuration;
	private Method method; //方法
	private boolean duplicated = false;
	private String resultMapId;//resultMap default : currentNamespace + "." + methodName
	private LanguageDriver languageDriver;
	private Class<?> parameterTypeClass;//参数类型 mybatis dao 只能有一个参数,多个参数请使用map封装
	private String sqlScript;//sql表达式,动态sql需使用<script>标签装饰:<script>dynamicSql</script>
	private boolean dynamicSql = true;
	private Integer fetchSize;
	private Integer timeout;
	private StatementType statementType = StatementType.PREPARED;
	private ResultSetType resultSetType = ResultSetType.FORWARD_ONLY;
	private SqlCommandType sqlCommandType;//insert / update / delete /select
	private boolean generated = false;
	private KeyGenerator keyGenerator;//主键策略
	private String keyProperty;
	private String keyColumn;
	private Class<?> resultType;//方法返回值类型


	public MybatisStatementAdapter(MapperBuilderAssistant assistant, Class<?> mapper) {
		this.assistant = assistant;
		this.mapper = mapper;
		this.configuration = assistant.getConfiguration();
		this.reset();
	}

	public void reset() {
		this.languageDriver = assistant.getLanguageDriver(null);
		this.statementType = StatementType.PREPARED;
		this.resultSetType = ResultSetType.FORWARD_ONLY;
		this.generated = false;
		this.keyGenerator = NoKeyGenerator.INSTANCE;
	}

	/*public void buildParameterType(Method method) {
		this.parameterTypeClass = getParameterType(method);
	}

	private Class<?> getParameterType(Method method) {
		Class<?> parameterType = null;
		Class<?>[] parameterTypes = method.getParameterTypes();
		for (Class<?> currentParameterType : parameterTypes) {
			if (!RowBounds.class.isAssignableFrom(currentParameterType) && !ResultHandler.class.isAssignableFrom(currentParameterType)) {
				if (parameterType == null) {
					parameterType = currentParameterType;
				} else {
					parameterType = MapperMethod.ParamMap.class;
				}
			}
		}
		return parameterType;
	}*/

	public void buildLanguageDriver(Method method) {
		this.languageDriver = getLanguageDriver(method);
	}

	private LanguageDriver getLanguageDriver(Method method) {
		Lang lang = method.getAnnotation(Lang.class);
		Class<?> langClass = null;
		if (lang != null) {
			langClass = lang.value();
		}else{
			langClass = VelocityLangDriver.class;
		}
		return assistant.getLanguageDriver(langClass);
	}

	public void buildReturnType(Method method) {
		this.resultType = getReturnType(method);
	}

	private Class<?> getReturnType(Method method) {
		Class<?> returnType = method.getReturnType();
		Type resolvedReturnType = TypeParameterResolver.resolveReturnType(method, this.mapper);
		if (resolvedReturnType instanceof Class) {
			returnType = (Class<?>) resolvedReturnType;
			if (returnType.isArray()) {
				returnType = returnType.getComponentType();
			}
			// gcode issue #508
			if (void.class.equals(returnType)) {
				ResultType rt = method.getAnnotation(ResultType.class);
				if (rt != null) {
					returnType = rt.value();
				}
			}
		} else if (resolvedReturnType instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) resolvedReturnType;
			Class<?> rawType = (Class<?>) parameterizedType.getRawType();
			if (Collection.class.isAssignableFrom(rawType) || Cursor.class.isAssignableFrom(rawType)) {
				Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
				if (actualTypeArguments != null && actualTypeArguments.length == 1) {
					Type returnTypeParameter = actualTypeArguments[0];
					if (returnTypeParameter instanceof Class<?>) {
						returnType = (Class<?>) returnTypeParameter;
					} else if (returnTypeParameter instanceof ParameterizedType) {
						// (gcode issue #443) actual type can be a also a parameterized type
						returnType = (Class<?>) ((ParameterizedType) returnTypeParameter).getRawType();
					} else if (returnTypeParameter instanceof GenericArrayType) {
						Class<?> componentType = (Class<?>) ((GenericArrayType) returnTypeParameter).getGenericComponentType();
						// (gcode issue #525) support List<byte[]>
						returnType = Array.newInstance(componentType, 0).getClass();
					}
				}
			} else if (method.isAnnotationPresent(MapKey.class) && Map.class.isAssignableFrom(rawType)) {
				// (gcode issue 504) Do not look into Maps if there is not MapKey annotation
				Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
				if (actualTypeArguments != null && actualTypeArguments.length == 2) {
					Type returnTypeParameter = actualTypeArguments[1];
					if (returnTypeParameter instanceof Class<?>) {
						returnType = (Class<?>) returnTypeParameter;
					} else if (returnTypeParameter instanceof ParameterizedType) {
						// (gcode issue 443) actual type can be a also a parameterized type
						returnType = (Class<?>) ((ParameterizedType) returnTypeParameter).getRawType();
					}
				}
			}
		}

		return returnType;
	}


	public void buildResultMapId(Method method) {
		resultMapId = null;
		ResultMap resultMapAnnotation = method.getAnnotation(ResultMap.class);
		if (resultMapAnnotation != null) {
			String[] resultMaps = resultMapAnnotation.value();
			StringBuilder sb = new StringBuilder();
			for (String resultMap : resultMaps) {
				if (sb.length() > 0) {
					sb.append(",");
				}
				sb.append(resultMap);
			}
			resultMapId = sb.toString();
		}
	}

	public void buildMethod(Method method) {
		this.method = method;// 方法名

		String mappedStatementId = getMappedStatementId();
		if (assistant.getConfiguration().hasStatement(mappedStatementId)) {
			log.warn("Mybatis MappedStatement exists: {}", mappedStatementId);
			this.duplicated = true;
			return;
		}
		// Lang
		buildLanguageDriver(method);
		// 参数类型
//		buildParameterType(method);
		// 返回值类型
		buildReturnType(method);
		// 主键策略
		setKeyGenerator(NoKeyGenerator.INSTANCE);
		setKeyProperty(null);
		setKeyColumn(null);
		buildResultMapId(method);
	}

	/**
	 * 创建mybatis statement,并向configuration中注册
	 */
	public final void parseStatement() {
		if (dynamicSql) {
			sqlScript = ScriptWrapper.quoteScript(sqlScript);
		}
		log.debug("parameterTypeClass: {}", parameterTypeClass);
		log.debug("sqlScript: \n{}", sqlScript);
		SqlSource sqlSource = this.buildSqlSource(sqlScript, parameterTypeClass);
		if (sqlSource != null) {

			// Options options = null;
			final String mappedStatementId = this.getMappedStatementId();

			boolean isSelect = sqlCommandType == SqlCommandType.SELECT;
			boolean flushCache = !isSelect;
			boolean useCache = isSelect;

			Options options = method.getAnnotation(Options.class);

			if (!generated) {
				keyGenerator = NoKeyGenerator.INSTANCE;
				keyProperty = null;
				keyColumn = null;
				if (SqlCommandType.INSERT.equals(sqlCommandType) || SqlCommandType.UPDATE.equals(sqlCommandType)) {
					// first check for SelectKey annotation - that overrides everything else
					SelectKey selectKey = method.getAnnotation(SelectKey.class);
					if (selectKey != null) {
						keyGenerator = handleSelectKeyAnnotation(selectKey, mappedStatementId, Map.class, languageDriver);
						keyProperty = selectKey.keyProperty();
					} else if (options == null) {
						keyGenerator = assistant.getConfiguration().isUseGeneratedKeys() ? Jdbc3KeyGenerator.INSTANCE : NoKeyGenerator.INSTANCE;
					} else {
						keyGenerator = options.useGeneratedKeys() ? Jdbc3KeyGenerator.INSTANCE : NoKeyGenerator.INSTANCE;
						keyProperty = options.keyProperty();
						keyColumn = options.keyColumn();
					}
				}
			}

			if (options != null) {
				if (Options.FlushCachePolicy.TRUE.equals(options.flushCache())) {
					flushCache = true;
				} else if (Options.FlushCachePolicy.FALSE.equals(options.flushCache())) {
					flushCache = false;
				}
				useCache = options.useCache();
				fetchSize = options.fetchSize() > -1 || options.fetchSize() == Integer.MIN_VALUE ? options.fetchSize() : null; //issue #348
				timeout = options.timeout() > -1 ? options.timeout() : null;
				statementType = options.statementType();
				resultSetType = options.resultSetType();
			}

			assistant.addMappedStatement(mappedStatementId, sqlSource, statementType, sqlCommandType, fetchSize,
				timeout,
				// ParameterMapID
				null, parameterTypeClass, resultMapId, resultType, resultSetType, flushCache, useCache,
				// TODO gcode issue #577
				false, keyGenerator, keyProperty, keyColumn,
				// DatabaseID
				null, languageDriver,
				// ResultSets
				null);
		}
	}

	private KeyGenerator handleSelectKeyAnnotation(SelectKey selectKeyAnnotation, String baseStatementId, Class<?> parameterTypeClass, LanguageDriver languageDriver) {
		String id = baseStatementId + SelectKeyGenerator.SELECT_KEY_SUFFIX;
		Class<?> resultTypeClass = selectKeyAnnotation.resultType();
		StatementType statementType = selectKeyAnnotation.statementType();
		String keyProperty = selectKeyAnnotation.keyProperty();
		String keyColumn = selectKeyAnnotation.keyColumn();
		boolean executeBefore = selectKeyAnnotation.before();

		// defaults
		boolean useCache = false;
		KeyGenerator keyGenerator = NoKeyGenerator.INSTANCE;
		Integer fetchSize = null;
		Integer timeout = null;
		boolean flushCache = false;
		String parameterMap = null;
		String resultMap = null;
		ResultSetType resultSetTypeEnum = null;

		SqlSource sqlSource = buildSqlSourceFromStrings(selectKeyAnnotation.statement(), parameterTypeClass, languageDriver);
		SqlCommandType sqlCommandType = SqlCommandType.SELECT;

		assistant.addMappedStatement(id, sqlSource, statementType, sqlCommandType, fetchSize, timeout, parameterMap, parameterTypeClass, resultMap, resultTypeClass, resultSetTypeEnum,
			flushCache, useCache, false,
			keyGenerator, keyProperty, keyColumn, null, languageDriver, null);

		id = assistant.applyCurrentNamespace(id, false);

		MappedStatement keyStatement = configuration.getMappedStatement(id, false);
		SelectKeyGenerator answer = new SelectKeyGenerator(keyStatement, executeBefore);
		configuration.addKeyGenerator(id, answer);
		return answer;
	}

	private SqlSource buildSqlSourceFromStrings(String[] strings, Class<?> parameterTypeClass, LanguageDriver languageDriver) {
		final StringBuilder sql = new StringBuilder();
		for (String fragment : strings) {
			sql.append(fragment);
			sql.append(" ");
		}
		return languageDriver.createSqlSource(configuration, sql.toString().trim(), parameterTypeClass);
	}

	/**
	 * 调用此方法前,需设置assistant.currentNamespace 和 methodName</br>
	 * 若以上两个参数有一个为空(null or empty),则throw RunTimeException
	 *
	 * @return currentNamespace + "." + methodName
	 */
	public String getMappedStatementId() {
		String currentNamespace = this.assistant.getCurrentNamespace();
		if (currentNamespace == null || currentNamespace.trim().equals("")) {
			throw new RuntimeException("currentNamespace is missing");
		}
		if (this.method.getName() == null || this.method.getName().trim().equals("")) {
			throw new RuntimeException("methodName is missing");
		}
		return currentNamespace + "." + this.method.getName();
	}

	/**
	 * 创建mybatis SqlSource
	 */
	private SqlSource buildSqlSource(String sqlScript, Class<?> parameterTypeClass, LanguageDriver languageDriver) {
		return languageDriver.createSqlSource(this.assistant.getConfiguration(), sqlScript, parameterTypeClass);
	}

	/**
	 * 创建mybatis SqlSource
	 */
	private SqlSource buildSqlSource(String sqlScript, Class<?> parameterTypeClass) {
		return buildSqlSource(sqlScript, parameterTypeClass, this.languageDriver);
	}
}
