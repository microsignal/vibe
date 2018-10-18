package io.microvibe.booster.core.base.mybatis.type;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes(Double[].class)
@MappedJdbcTypes({JdbcType.VARCHAR,JdbcType.CHAR})
public class DoubleArrayTypeHandler extends StringTokenizerTypeHandler<Double> {
	public DoubleArrayTypeHandler() {
		super(Double.class);
	}

	@Override
	Double parseString(String value) {
		return Double.valueOf(value);
	}
}
