package io.microvibe.booster.core.base.mybatis.type;

import io.microvibe.booster.commons.utils.json.JsonUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(Object.class)
@MappedJdbcTypes({JdbcType.VARCHAR,JdbcType.CHAR})
public class JsonTypeHandler extends BaseTypeHandler<Object> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Object parameter,
									JdbcType jdbcType) throws SQLException {

		ps.setString(i, JsonUtil.toJson(parameter));
	}

	@Override
	public Object getNullableResult(ResultSet rs, String columnName)
		throws SQLException {

		return JsonUtil.toJavaObject(Object.class, rs.getString(columnName));
	}

	@Override
	public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {

		return JsonUtil.toJavaObject(Object.class, rs.getString(columnIndex));
	}

	@Override
	public Object getNullableResult(CallableStatement cs, int columnIndex)
		throws SQLException {

		return JsonUtil.toJavaObject(Object.class, cs.getString(columnIndex));
	}
}
