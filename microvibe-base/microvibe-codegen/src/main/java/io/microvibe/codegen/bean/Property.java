package io.microvibe.codegen.bean;

import java.util.Map;

import io.microvibe.util.castor.annotation.XComplexKey;
import io.microvibe.util.castor.annotation.XComplexType;
import io.microvibe.util.castor.annotation.XComplexValue;
import io.microvibe.util.castor.annotation.XName;

public class Property {
	String key;
	String value;

	@XName("entry")
	@XComplexType(Property.class)
	@XComplexKey("key")
	@XComplexValue("value")
	private Map<String, String> properties;

	public Map<String, String> getProperties() {
		return properties;
	}

}
