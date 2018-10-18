package io.microvibe.codegen.bean;

import java.util.List;

import io.microvibe.util.castor.annotation.XComplexType;
import io.microvibe.util.castor.annotation.XName;

public class CodeEnv  {
	private String outdir;

	@XName("property")
	@XComplexType(Property.class)
	private Property property;

	@XName("group")
	@XComplexType(CodeGroup.class)
	private List<CodeGroup> groups;

	public String getOutdir() {
		return outdir;
	}

	public void setOutdir(String outdir) {
		this.outdir = outdir;
	}

	public Property getProperty() {
		return property;
	}

	public void setProperty(Property property) {
		this.property = property;
	}

	public List<CodeGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<CodeGroup> groups) {
		this.groups = groups;
	}

}
