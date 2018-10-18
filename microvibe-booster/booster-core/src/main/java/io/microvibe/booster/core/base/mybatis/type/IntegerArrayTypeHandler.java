package io.microvibe.booster.core.base.mybatis.type;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes(Integer[].class)
@MappedJdbcTypes({JdbcType.VARCHAR,JdbcType.CHAR})
public class IntegerArrayTypeHandler extends StringTokenizerTypeHandler<Integer> {
	public IntegerArrayTypeHandler() {
		super(Integer.class);
	}

	@Override
	Integer parseString(String value) {
		return Integer.valueOf(value);
	}
}
