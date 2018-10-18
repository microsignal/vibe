package io.microvibe.booster.core.base.mybatis.type;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.*;

import java.sql.*;
import java.util.Date;

/**
 * @author Qt
 * @see DateTypeHandler
 * @see LocalDateTypeHandler
 * @since Sep 08, 2018
 */
@Slf4j
@MappedTypes(Time.class)
@MappedJdbcTypes({JdbcType.DATE, JdbcType.TIMESTAMP})
public class DynamicTimeTypeHandler extends BaseTypeHandler<Object> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType)
		throws SQLException {
		if (parameter == null) {
			ps.setNull(i, Types.TIMESTAMP);
			return;
		}
		if (parameter instanceof Time) {
			ps.setTime(i, (Time) parameter);
		} else if (parameter instanceof Date) {
			ps.setTime(i, new Time(((Date) parameter).getTime()));
		} else if (parameter instanceof Number) {
			ps.setTime(i, new Time(((Number) parameter).longValue()));
		} else {
			Date date = com.alibaba.fastjson.util.TypeUtils.castToDate(parameter);
			ps.setTime(i, new Time(date.getTime()));
		}
	}

	@Override
	public Time getNullableResult(ResultSet rs, String columnName)
		throws SQLException {
		Time sqlTime = rs.getTime(columnName);
		return sqlTime;
	}

	@Override
	public Time getNullableResult(ResultSet rs, int columnIndex)
		throws SQLException {
		Time sqlTime = rs.getTime(columnIndex);
		return sqlTime;
	}

	@Override
	public Time getNullableResult(CallableStatement cs, int columnIndex)
		throws SQLException {
		Time sqlTime = cs.getTime(columnIndex);
		return sqlTime;
	}
}
