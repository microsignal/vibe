package io.microvibe.codegen.bean;

import java.util.Map;

import io.microvibe.codegen.bean.db.Catalog;
import io.microvibe.util.castor.annotation.XComplexKey;
import io.microvibe.util.castor.annotation.XComplexType;
import io.microvibe.util.castor.annotation.XName;
import io.microvibe.util.castor.annotation.XRootName;
import io.microvibe.util.collection.IgnoreCaseHashMap;

@XRootName("tables")
public class Tables  {

	@XName("catalog")
	@XComplexType(Catalog.class)
	@XComplexKey("name")
	Map<String, Catalog> catalogs = new IgnoreCaseHashMap<String, Catalog>();

	public Map<String, Catalog> getCatalogs() {
		return catalogs;
	}

	public void setCatalogs(Map<String, Catalog> catalogs) {
		this.catalogs = catalogs;
	}
}
