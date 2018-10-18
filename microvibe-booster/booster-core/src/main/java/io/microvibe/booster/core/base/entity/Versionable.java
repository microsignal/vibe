package io.microvibe.booster.core.base.entity;

/**
 * 标识实体含有乐观锁
 */
public interface Versionable {

	String FIELD_NAME = "version";

	long getVersion();

	void setVersion(long version);

}
