package io.microvibe.booster.core.base.mybatis.statement;

import io.microvibe.booster.core.base.mybatis.statement.builder.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StatementBuilders {
	private static Map<BuilderType, StatementBuilder>
		builderMap = new java.util.EnumMap<>(BuilderType.class);


	static {
		builderMap.put(BuilderType.NOOP, new NoopStatementBuilder());

		builderMap.put(BuilderType.SELECT_BY_EXAMPLE, new SelectByExampleBuilder());
		builderMap.put(BuilderType.SELECT_BY_ENTITY, new SelectByEntityBuilder());
		builderMap.put(BuilderType.SELECT_BY_MAP, new SelectByMapBuilder());
		builderMap.put(BuilderType.COUNT_BY_EXAMPLE, new CountByExampleBuilder());
		builderMap.put(BuilderType.COUNT_BY_ENTITY, new CountByEntityBuilder());
		builderMap.put(BuilderType.COUNT_BY_MAP, new CountByMapBuilder());

		builderMap.put(BuilderType.EXISTS_BY_EXAMPLE, new ExistsByExampleBuilder());
		builderMap.put(BuilderType.EXISTS_BY_ENTITY, new ExistsByEntityBuilder());
		builderMap.put(BuilderType.EXISTS_BY_MAP, new ExistsByMapBuilder());
		builderMap.put(BuilderType.EXISTS_BY_ID, new ExistsByIdBuilder());
		builderMap.put(BuilderType.GET_BY_ID, new GetByIdBuilder());

		builderMap.put(BuilderType.DELETE_BY_ID, new DeleteByIdBuilder());

		builderMap.put(BuilderType.UPDATE_BY_ID, new UpdateByIdBuilder());
		builderMap.put(BuilderType.UPDATE_SELECTIVE_BY_ID, new UpdateSelectiveByIdBuilder());

		builderMap.put(BuilderType.INSERT, new InsertBuilder());
		builderMap.put(BuilderType.INSERT_SELECTIVE, new InsertSelectiveBuilder());
		builderMap.put(BuilderType.INSERT_BATCH, new InsertBatchBuilder());

	}


	public static StatementBuilder get(BuilderType builderType) {
		return builderMap.get(builderType);
	}


}
