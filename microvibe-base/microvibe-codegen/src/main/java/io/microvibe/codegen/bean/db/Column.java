package io.microvibe.codegen.bean.db;

import java.sql.Types;

import io.microvibe.util.StringUtil;
import io.microvibe.util.castor.Castors;

public class Column {
	String name; // 列名
	Integer type; // 列类型
	String comment; // 注释
	String defaultValue; // 默认值
	boolean isPrimary = false; // 是否主键列
	boolean nullable = false; // 是否可为空值

	// for java
	String javaClassName; // 列的映射类名,一般列只作为类的字段,不会用到此值
	String javaVariableName; // 列的映射变量名
	String javaClassType; // 列的映射类型
	String javaJdbcType; // 列的JDBC类型

	String xmlName;

	/**
	 * 预处理,对列的映射变量名、映射类型等的处理
	 */
	public void prepare4Java() {
		char[] nameChars = name.toCharArray();
		StringBuilder sb = new StringBuilder();
		boolean flag = false;
		for (int i = 1; i < nameChars.length; i++) {
			if (nameChars[i] == '_') {
				flag = true;
			} else {
				if (flag) {
					sb.append(Character.toUpperCase(nameChars[i]));
					flag = false;
				} else {
					sb.append(Character.toLowerCase(nameChars[i]));
				}
			}
		}
		if (StringUtil.isEmpty(javaClassName)) {
			javaClassName = Character.toUpperCase(nameChars[0]) + sb.toString();
		}
		if (StringUtil.isEmpty(javaVariableName)) {
			javaVariableName = Character.toLowerCase(nameChars[0]) + sb.toString();
		}
		if (type != null) {
			if (StringUtil.isEmpty(javaJdbcType)) {
				javaJdbcType = javaJdbcType(type);
			}
			if (StringUtil.isEmpty(javaClassType)) {
				javaClassType = javaClassType(type);
			}
		} else {
			if (StringUtil.isNotEmpty(javaJdbcType)) {
				try {
					type = (Integer) Castors.cast(Integer.class,
							java.sql.Types.class.getField(javaJdbcType.toUpperCase()).get(null));
				} catch (Exception e) {
				}
			}
		}
		xmlName = name.toLowerCase().replace("_", "-");
		/*
		String type = this.type.toUpperCase();
		if (type.startsWith("NUMBER")) {
			javaClassType = java.math.BigDecimal.class.getName();
			javaJdbcType = "DECIMAL";
		} else if (type.equals("DATE")) {
			// javaClassType = java.util.Date.class.getName();
			// javaJdbcType = "DATE";
			javaClassType = java.sql.Timestamp.class.getName();
			javaJdbcType = "TIMESTAMP";
		} else {
			javaClassType = String.class.getName();
			javaJdbcType = "CHAR";
		}*/
	}

	static String javaClassType(int dataType) {
		switch (dataType) {
		case Types.BIT:
//			return boolean.class.getSimpleName();
			return Boolean.class.getSimpleName();
		case Types.TINYINT:
//			return byte.class.getSimpleName();
			return Byte.class.getSimpleName();
		case Types.SMALLINT:
//			return short.class.getSimpleName();
			return Short.class.getSimpleName();
		case Types.INTEGER:
//			return int.class.getSimpleName();
			return Integer.class.getSimpleName();
		case Types.BIGINT:
//			return long.class.getSimpleName();
			return Long.class.getSimpleName();
		case Types.FLOAT:
//			return double.class.getSimpleName();
			return Double.class.getSimpleName();
		case Types.REAL:
//			return float.class.getSimpleName();
			return Float.class.getSimpleName();
		case Types.DOUBLE:
//			return double.class.getSimpleName();
			return Double.class.getSimpleName();
		case Types.NUMERIC:
			return java.math.BigDecimal.class.getName();
		case Types.DECIMAL:
			return java.math.BigDecimal.class.getName();
		case Types.CHAR:
			return String.class.getSimpleName();
		case Types.VARCHAR:
			return String.class.getSimpleName();
		case Types.LONGVARCHAR:
			return String.class.getSimpleName();
		case Types.DATE:
			return java.util.Date.class.getName();
		case Types.TIME:
			return java.sql.Timestamp.class.getName();
		case Types.TIMESTAMP:
			return java.sql.Timestamp.class.getName();
		case Types.BINARY:
			return byte[].class.getSimpleName();
		case Types.VARBINARY:
			return byte[].class.getSimpleName();
		case Types.LONGVARBINARY:
			return byte[].class.getSimpleName();
		case Types.NULL:
			throw new IllegalArgumentException("" + dataType);
		case Types.OTHER:
			throw new IllegalArgumentException("" + dataType);
		case Types.JAVA_OBJECT:
			throw new IllegalArgumentException("" + dataType);
		case Types.DISTINCT:
			throw new IllegalArgumentException("" + dataType);
		case Types.STRUCT:
			throw new IllegalArgumentException("" + dataType);
		case Types.ARRAY:
			throw new IllegalArgumentException("" + dataType);
		case Types.BLOB:
			return byte[].class.getSimpleName();
		case Types.CLOB:
			return String.class.getSimpleName();
		case Types.REF:
			throw new IllegalArgumentException("" + dataType);
		case Types.DATALINK:
			throw new IllegalArgumentException("" + dataType);
		case Types.BOOLEAN:
//			return boolean.class.getSimpleName();
			return Boolean.class.getSimpleName();
		case Types.ROWID:
			throw new IllegalArgumentException("" + dataType);
		case Types.NCHAR:
			return String.class.getSimpleName();
		case Types.NVARCHAR:
			return String.class.getSimpleName();
		case Types.LONGNVARCHAR:
			return String.class.getSimpleName();
		case Types.NCLOB:
			return String.class.getSimpleName();
		case Types.SQLXML:
			throw new IllegalArgumentException("" + dataType);
		default:
			return null;
		}
	}

	static String javaJdbcType(int dataType) {
		switch (dataType) {
		case Types.BIT:
			return "BIT";
		case Types.TINYINT:
			return "TINYINT";
		case Types.SMALLINT:
			return "SMALLINT";
		case Types.INTEGER:
			return "INTEGER";
		case Types.BIGINT:
			return "BIGINT";
		case Types.FLOAT:
			return "FLOAT";
		case Types.REAL:
			return "REAL";
		case Types.DOUBLE:
			return "DOUBLE";
		case Types.NUMERIC:
			return "NUMERIC";
		case Types.DECIMAL:
			return "DECIMAL";
		case Types.CHAR:
			return "CHAR";
		case Types.VARCHAR:
			return "VARCHAR";
		case Types.LONGVARCHAR:
			return "LONGVARCHAR";
		case Types.DATE:
			return "DATE";
		case Types.TIME:
			return "TIME";
		case Types.TIMESTAMP:
			return "TIMESTAMP";
		case Types.BINARY:
			return "BINARY";
		case Types.VARBINARY:
			return "VARBINARY";
		case Types.LONGVARBINARY:
			return "LONGVARBINARY";
		case Types.NULL:
			return "NULL";
		case Types.OTHER:
			return "OTHER";
		case Types.JAVA_OBJECT:
			return "JAVA_OBJECT";
		case Types.DISTINCT:
			return "DISTINCT";
		case Types.STRUCT:
			return "STRUCT";
		case Types.ARRAY:
			return "ARRAY";
		case Types.BLOB:
			return "BLOB";
		case Types.CLOB:
			return "CLOB";
		case Types.REF:
			return "REF";
		case Types.DATALINK:
			return "DATALINK";
		case Types.BOOLEAN:
			return "BOOLEAN";
		case Types.ROWID:
			return "ROWID";
		case Types.NCHAR:
			return "NCHAR";
		case Types.NVARCHAR:
			return "NVARCHAR";
		case Types.LONGNVARCHAR:
			return "LONGNVARCHAR";
		case Types.NCLOB:
			return "NCLOB";
		case Types.SQLXML:
			return "SQLXML";
		default:
			return null;
		}
	}

	@Override
	public String toString() {
		return "[" + name + " " + type + "]";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public boolean isPrimary() {
		return isPrimary;
	}

	public void setPrimary(boolean isPrimary) {
		this.isPrimary = isPrimary;
	}

	public String getJavaClassName() {
		return javaClassName;
	}

	public void setJavaClassName(String javaClassName) {
		this.javaClassName = javaClassName;
	}

	public String getJavaVariableName() {
		return javaVariableName;
	}

	public void setJavaVariableName(String javaVariableName) {
		this.javaVariableName = javaVariableName;
	}

	public String getJavaClassType() {
		return javaClassType;
	}

	public void setJavaClassType(String javaClassType) {
		this.javaClassType = javaClassType;
	}

	public String getJavaJdbcType() {
		return javaJdbcType;
	}

	public void setJavaJdbcType(String javaJdbcType) {
		this.javaJdbcType = javaJdbcType;
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public String getXmlName() {
		return xmlName;
	}

	public void setXmlName(String xmlName) {
		this.xmlName = xmlName;
	}

}
