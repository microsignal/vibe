package io.microvibe.codegen.bean;

import io.microvibe.util.castor.annotation.XComplexType;
import io.microvibe.util.castor.annotation.XName;

public class CodeConfig {
	private String table;
	private String catalog;
	private String schema;
	@XName("package")
	private String javaPackageName;

	private String classify;
	@XName("property")
	@XComplexType(Property.class)
	private Property property;

	public String getTable() {
		return table;
	}

	public String getCatalog() {
		return catalog;
	}

	public String getSchema() {
		return schema;
	}

	public String getJavaPackageName() {
		return javaPackageName;
	}

	public String getClassify() {
		return classify;
	}

	public Property getProperty() {
		return property;
	}
}
