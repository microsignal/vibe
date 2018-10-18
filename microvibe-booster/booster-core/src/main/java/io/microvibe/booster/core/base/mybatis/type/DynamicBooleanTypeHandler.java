package io.microvibe.booster.core.base.mybatis.type;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.Alias;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.BooleanTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Collection;
import java.util.Map;

/**
 * @author Qt
 * @see org.apache.ibatis.type.BooleanTypeHandler
 * @since Sep 08, 2018
 */
@Slf4j
@Alias("dynamicBooleanTypeHandler")
public class DynamicBooleanTypeHandler extends BaseTypeHandler<Object> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType)
		throws SQLException {
		Boolean val = null;
		if (parameter != null) {
			if(parameter instanceof  Boolean){
				val = (Boolean) parameter;
			}else if(parameter instanceof String){
				String s = (String) parameter;
				val = "true".equalsIgnoreCase(s) || "1".equalsIgnoreCase(s) || "yes".equalsIgnoreCase(s);
			}else if(parameter instanceof Number){
				val = ((Number) parameter).intValue() != 0;
			}else if(parameter instanceof Collection){
				val = ((Collection) parameter).size()>0;
			}else if(parameter instanceof Map){
				val = ((Map) parameter).size()>0;
			}else if(parameter.getClass().isArray()){
				val = Array.getLength(parameter)>0;
			}
		}
		if (val == null) {
			if (jdbcType == null) {
				ps.setNull(i, Types.VARCHAR);
			} else {
				ps.setNull(i, jdbcType.TYPE_CODE);
			}
		} else {
			if (jdbcType == null) {
				ps.setBoolean(i, val);
			} else {
				ps.setObject(i, val, jdbcType.TYPE_CODE);
			}
		}
	}

	public void setNonNullParameter(PreparedStatement ps, int i, Boolean parameter, JdbcType jdbcType)
		throws SQLException {
		ps.setBoolean(i, parameter);
	}

	@Override
	public Boolean getNullableResult(ResultSet rs, String columnName)
		throws SQLException {
		return rs.getBoolean(columnName);
	}

	@Override
	public Boolean getNullableResult(ResultSet rs, int columnIndex)
		throws SQLException {
		return rs.getBoolean(columnIndex);
	}

	@Override
	public Boolean getNullableResult(CallableStatement cs, int columnIndex)
		throws SQLException {
		return cs.getBoolean(columnIndex);
	}

}
