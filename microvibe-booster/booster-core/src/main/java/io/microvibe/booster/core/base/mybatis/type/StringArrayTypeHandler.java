package io.microvibe.booster.core.base.mybatis.type;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes(String[].class)
@MappedJdbcTypes({JdbcType.VARCHAR,JdbcType.CHAR})
public class StringArrayTypeHandler extends StringTokenizerTypeHandler<String> {
	public StringArrayTypeHandler() {
		super(String.class);
	}

	@Override
	String parseString(String value) {
		return String.valueOf(value);
	}
}
