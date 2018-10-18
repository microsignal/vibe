package io.microvibe.codegen.bean.db;

import java.util.Map;

import io.microvibe.util.castor.annotation.XComplexKey;
import io.microvibe.util.castor.annotation.XComplexType;
import io.microvibe.util.castor.annotation.XName;
import io.microvibe.util.collection.IgnoreCaseLinkedHashMap;

public class Catalog {

	String name;
	@XName("schema")
	@XComplexType(Schema.class)
	@XComplexKey("name")
	Map<String, Schema> schemas = new IgnoreCaseLinkedHashMap<String, Schema>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, Schema> getSchemas() {
		return schemas;
	}

	public void setSchemas(Map<String, Schema> schemas) {
		this.schemas = schemas;
	}

}
