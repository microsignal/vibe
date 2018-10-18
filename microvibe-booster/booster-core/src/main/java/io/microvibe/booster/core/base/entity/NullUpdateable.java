package io.microvibe.booster.core.base.entity;

import java.util.Collection;
import java.util.Set;

public interface NullUpdateable {

	Set<String> getUpdatingNullFields();

	void setUpdatingNullFields(Set<String> fields);

	void addUpdatingNullField(String field);

	void removeUpdatingNullField(String field);
}
