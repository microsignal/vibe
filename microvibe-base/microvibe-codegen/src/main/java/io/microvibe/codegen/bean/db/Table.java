package io.microvibe.codegen.bean.db;

import java.util.ArrayList;
import java.util.List;

import io.microvibe.codegen.bean.Property;
import io.microvibe.util.StringUtil;
import io.microvibe.util.castor.annotation.XComplexType;
import io.microvibe.util.castor.annotation.XName;
import io.microvibe.util.castor.annotation.XSerializable;

public class Table {

	String name; // 表名
	String comment; // 注释
	//String tablespace;
	@XName("column")
	@XComplexType(Column.class)
	List<Column> columns = new ArrayList<Column>(); // 所有列
	@XSerializable(false)
	List<Column> pkColumns = new ArrayList<Column>(); // 主键列
	@XSerializable(false)
	List<Column> normalColumns = new ArrayList<Column>(); // 非主键列

	String javaPackageName = ""; // 代码生成时对应的包名
	// for java
	String javaClassName; // 表名映射后的类名
	String javaVariableName; // 表名映射后的类的实例变量名

	private String xmlName;
	private String classify;
	@XName("property")
	@XComplexType(Property.class)
	private Property property;

	/**
	 * 预处理,对类名、变量名、主键列、非主键列等的处理
	 */
	public void prepare4Java() {
		for (Column col : columns) {
			col.prepare4Java();
		}
		if (pkColumns.isEmpty()) {
			for (Column col : columns) {
				if (col.isPrimary) {
					pkColumns.add(col);
				}
			}
		} else {
			for (Column col : pkColumns) {
				col.prepare4Java();
			}
		}
		if (normalColumns.isEmpty()) {
			for (Column col : columns) {
				if (!col.isPrimary) {
					normalColumns.add(col);
				}
			}
		} else {
			for (Column col : normalColumns) {
				col.prepare4Java();
			}
		}
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
		xmlName = name.toLowerCase().replace("_", "-");
	}

	public List<Column> getColumns() {
		return columns;
	}

	public String getComment() {
		return comment;
	}

	public String getJavaClassName() {
		return javaClassName;
	}

	public String getJavaPackageName() {
		return javaPackageName;
	}

	public String getJavaVariableName() {
		return javaVariableName;
	}

	public String getName() {
		return name;
	}

	public List<Column> getNormalColumns() {
		return normalColumns;
	}

	public List<Column> getPkColumns() {
		return pkColumns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setJavaClassName(String javaClassName) {
		this.javaClassName = javaClassName;
	}

	public void setJavaPackageName(String javaPackageName) {
		this.javaPackageName = javaPackageName;
	}

	public void setJavaVariableName(String javaVariableName) {
		this.javaVariableName = javaVariableName;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNormalColumns(List<Column> normalColumns) {
		this.normalColumns = normalColumns;
	}

	public void setPkColumns(List<Column> pkColumns) {
		this.pkColumns = pkColumns;
	}

	@Override
	public String toString() {
		return "[" + name + "]";
	}

	public String getClassify() {
		return classify;
	}

	public void setClassify(String classify) {
		this.classify = classify;
	}

	public Property getProperty() {
		return property;
	}

	public void setProperty(Property property) {
		this.property = property;
	}

	public String getXmlName() {
		return xmlName;
	}

	public void setXmlName(String xmlName) {
		this.xmlName = xmlName;
	}
}
