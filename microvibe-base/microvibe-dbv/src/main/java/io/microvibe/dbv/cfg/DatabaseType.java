package io.microvibe.dbv.cfg;

public enum DatabaseType {
	oracle("/oracle.xml"),
	informix("/informix.xml"),
	mysql("/mysql.xml"),
	dameng("/dameng.xml"), ;

	private String cfgFile;

	private DatabaseType(String cfgFile) {
		this.cfgFile = cfgFile;
	}

	public String getCfgFile() {
		return cfgFile;
	}

}
