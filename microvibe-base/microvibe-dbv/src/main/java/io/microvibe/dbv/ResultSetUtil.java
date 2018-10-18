package io.microvibe.dbv;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.microvibe.dbv.annotation.ColumnCustomerHandler;
import io.microvibe.dbv.annotation.ColumnName;
import io.microvibe.dbv.handler.CustomerTypeHandler;
import io.microvibe.util.collection.IgnoreCaseLinkedHashMap;

public class ResultSetUtil {
	public static Map<String, Object> fetch(final ResultSet rs) throws SQLException {
		final ResultSetMetaData meta = rs.getMetaData();
		final int cnt = meta.getColumnCount();
		final Map<String, Object> map = new IgnoreCaseLinkedHashMap<String, Object>();
		for (int i = 1; i <= cnt; i++) {
			map.put(meta.getColumnLabel(i).toUpperCase(), rs.getObject(i));
		}
		return map;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object fetch(final ResultSet rs, final Object obj) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		Set<String> colSet = new HashSet<String>();
		for (int i = 1; i <= columnCount; i++) {
			colSet.add(rsmd.getColumnName(i).toUpperCase());
		}

		final Field[] fields = obj.getClass().getDeclaredFields();
		for (final Field field : fields) {
			field.setAccessible(true);
			final ColumnName columnName = field.getAnnotation(ColumnName.class);
			if (columnName == null) {
				continue;
			}
			String columnNameValue = columnName.value().toUpperCase();
			if (!colSet.contains(columnNameValue)) {
				continue;
			}
			final ColumnCustomerHandler columnCustomerHandler = field
					.getAnnotation(ColumnCustomerHandler.class);
			if (columnCustomerHandler != null && !"".equals(columnCustomerHandler.value().trim())) {
				try {
					Class<CustomerTypeHandler> clazz = (Class<CustomerTypeHandler>) Class
							.forName(columnCustomerHandler.value());
					CustomerTypeHandler customerTypeHandler = clazz.newInstance();
					field.set(obj, customerTypeHandler.getColumnValue(rs, columnNameValue));
				} catch (Exception e) {
				}
			} else {
				try {
					final Class<?> type = field.getType();
					if (String.class == type) {
						field.set(obj, rs.getString(columnNameValue));
					} else if (int.class == type) {
						field.setInt(obj, rs.getInt(columnNameValue));
					} else if (long.class == type) {
						field.setLong(obj, rs.getLong(columnNameValue));
					} else if (boolean.class == type) {
						field.setBoolean(obj, rs.getBoolean(columnNameValue));
					} else if (double.class == type) {
						field.setDouble(obj, rs.getDouble(columnNameValue));
					} else if (float.class == type) {
						field.setFloat(obj, rs.getFloat(columnNameValue));
					} else if (byte.class == type) {
						field.setByte(obj, rs.getByte(columnNameValue));
					} else if (short.class == type) {
						field.setShort(obj, rs.getShort(columnNameValue));
					} else if (char.class == type) {
						field.setChar(obj, rs.getString(columnNameValue).charAt(0));
					} else if (Integer.class == type) {
						field.set(obj, Integer.valueOf(rs.getInt(columnNameValue)));
					} else if (Long.class == type) {
						field.set(obj, Long.valueOf(rs.getLong(columnNameValue)));
					} else if (Boolean.class == type) {
						field.set(obj, Boolean.valueOf(rs.getBoolean(columnNameValue)));
					} else if (Double.class == type) {
						field.set(obj, Double.valueOf(rs.getDouble(columnNameValue)));
					} else if (Float.class == type) {
						field.set(obj, Float.valueOf(rs.getFloat(columnNameValue)));
					} else if (Byte.class == type) {
						field.set(obj, Byte.valueOf(rs.getByte(columnNameValue)));
					} else if (Short.class == type) {
						field.set(obj, Short.valueOf(rs.getShort(columnNameValue)));
					} else if (Character.class == type) {
						field.set(obj, Character.valueOf(rs.getString(columnNameValue).charAt(0)));
					} else if (java.util.Date.class == type || java.sql.Date.class == type) {
						field.set(obj, rs.getDate(columnNameValue));
					} else if (java.sql.Timestamp.class == type) {
						field.set(obj, rs.getTimestamp(columnNameValue));
					} else if (java.sql.Time.class == type) {
						field.set(obj, rs.getTime(columnNameValue));
					} else if (java.math.BigDecimal.class == type) {
						field.set(obj, rs.getBigDecimal(columnNameValue));
					} else {
						field.set(obj, rs.getObject(columnNameValue));
					}
					// System.out.println(columnName.value()+" : "+rs.getObject(columnName.value()));
				} catch (final Exception e) {
					// System.err.println(e.getMessage() + " " + columnName.value());
					// e.printStackTrace();
				}
			}
		}
		return obj;
	}

	public static List<Map<String, Object>> fetchList(final ResultSet rs)
			throws SQLException, InstantiationException, IllegalAccessException {
		final List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		while (rs.next()) {
			list.add(ResultSetUtil.fetch(rs));
		}
		return list;
	}

	public static <T> List<T> fetchList(final ResultSet rs, final Class<T> clazz)
			throws SQLException, InstantiationException, IllegalAccessException {
		final List<T> list = new ArrayList<T>();
		while (rs.next()) {
			final T object = clazz.newInstance();
			ResultSetUtil.fetch(rs, object);
			list.add(object);
		}
		return list;
	}
}
