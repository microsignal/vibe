package io.microvibe.booster.core.base.mybatis.type;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes(Float[].class)
@MappedJdbcTypes({JdbcType.VARCHAR,JdbcType.CHAR})
public class FloatArrayTypeHandler extends StringTokenizerTypeHandler<Float> {
	public FloatArrayTypeHandler() {
		super(Float.class);
	}

	@Override
	Float parseString(String value) {
		return Float.valueOf(value);
	}
}
