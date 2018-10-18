package io.microvibe.codegen.bean;

import io.microvibe.util.castor.annotation.XComplexType;
import io.microvibe.util.castor.annotation.XName;

public class CodeTemplate  {
	private String path;
	private String outdir;
	private String filename;
	@XName("property")
	@XComplexType(Property.class)
	private Property property;

	public String getPath() {
		return path;
	}

	public String getOutdir() {
		return outdir;
	}

	public String getFilename() {
		return filename;
	}

	public Property getProperty() {
		return property;
	}
}
