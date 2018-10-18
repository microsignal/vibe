package io.microvibe.booster.core.base.mybatis.type;


import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes(Boolean[].class)
@MappedJdbcTypes({JdbcType.VARCHAR,JdbcType.CHAR})
public class BooleanArrayTypeHandler extends StringTokenizerTypeHandler<Boolean> {
	public BooleanArrayTypeHandler() {
		super(Boolean.class);
	}

	@Override
	Boolean parseString(String value) {
		return Boolean.valueOf(value);
	}
}
