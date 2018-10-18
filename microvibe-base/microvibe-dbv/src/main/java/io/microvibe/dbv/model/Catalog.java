package io.microvibe.dbv.model;

import io.microvibe.dbv.annotation.ColumnName;

public class Catalog {

	@ColumnName("TABLE_CAT")
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Catalog [value=" + value + "]";
	}
}
