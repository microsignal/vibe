package io.microvibe.booster.core.base.mybatis.type;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.math.BigDecimal;

@MappedTypes(BigDecimal[].class)
@MappedJdbcTypes({JdbcType.VARCHAR,JdbcType.CHAR})
public class BigDecimalArrayTypeHandler extends StringTokenizerTypeHandler<BigDecimal> {
	public BigDecimalArrayTypeHandler() {
		super(BigDecimal.class);
	}

	@Override
	BigDecimal parseString(String value) {
		return new BigDecimal(value);
	}
}
