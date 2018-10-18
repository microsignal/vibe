package io.microvibe.booster.core.base.mybatis;

import io.microvibe.booster.core.base.entity.Versionable;
import io.microvibe.booster.core.base.mybatis.mapping.AutoEntityMapper;

public interface MybatisConstants {

	String LANG_PREFIX = "{{";
	String LANG_SUFFIX = "}}";
	String LANG_PATTERN = "\\{\\{(.+?)\\}\\}";

	String AUTO_MAPPER_NAMESPACE = AutoEntityMapper.class.getName();
	String AUTO_ENTITY_RESULT_MAP= "ResultMap";
	String AUTO_ENTITY_ALIAS_RESULT_MAP = "AliasResultMap";
	String AUTO_ENTITY_RESULT_MAP_NO_JOIN = "ResultMapNoJoin";
	String AUTO_ENTITY_ALIAS_RESULT_MAP_NO_JOIN = "AliasResultMapNoJoin";

	String PARAM_ID = "id";
	String PARAM_VERSION = Versionable.FIELD_NAME;
	String PARAM_ENTITY = "entity";
	String PARAM_ORDER_BY = "orderByClause";
	String PARAM_WHERE_EXTENSION_CLAUSE = "whereExtClause";

	String SELECT = "select";
	String EXISTS = "exists";
	String GET = "get";
	String DELETE = "delete";
	String INSERT_SELECTIVE = "insertSelective";
	String INSERT = "insert";
	String INSERT_BATCH = "insertBatch";
	String UPDATE_SELECTIVE = "updateSelective";
	String UPDATE = "update";


}
