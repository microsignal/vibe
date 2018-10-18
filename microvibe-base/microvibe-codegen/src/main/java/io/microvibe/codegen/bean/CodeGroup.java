package io.microvibe.codegen.bean;

import java.util.ArrayList;
import java.util.List;

import io.microvibe.util.castor.annotation.XComplexType;
import io.microvibe.util.castor.annotation.XName;

public class CodeGroup {

	@XName("template")
	@XComplexType(CodeTemplate.class)
	private List<CodeTemplate> templates = new ArrayList<CodeTemplate>();
	@XName("config")
	@XComplexType(CodeConfig.class)
	private List<CodeConfig> configs = new ArrayList<CodeConfig>();
	@XName("property")
	@XComplexType(Property.class)
	private Property property;

	public List<CodeConfig> getConfigs() {
		return configs;
	}

	public List<CodeTemplate> getTemplates() {
		return templates;
	}

	public Property getProperty() {
		return property;
	}
}
