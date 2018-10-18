package io.microvibe.dbv.cfg;

import io.microvibe.util.castor.annotation.XName;

public class SqlCfg {

	@XName("id")
	private String id;
	@XName("value")
	private String sql;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

}
