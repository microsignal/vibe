package io.microvibe.booster.core.base.entity;

import java.io.Serializable;
import java.util.List;

public class ChartEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<String> categories;

	private List<Object> series;

	public List<String> getCategories() {
		return categories;
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	public List<Object> getSeries() {
		return series;
	}

	public void setSeries(List<Object> series) {
		this.series = series;
	}

}
