package io.microvibe.util.tools;

public class PackageChangeMapping {

	private String srcPackage;
	private String destPackage;

	public PackageChangeMapping(final String srcPackage, final String destPackage) {
		super();
		this.srcPackage = srcPackage;
		this.destPackage = destPackage;
	}

	public String getDestPackage() {
		return destPackage;
	}

	public String getSrcPackage() {
		return srcPackage;
	}

}
