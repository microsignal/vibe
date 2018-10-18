package io.microvibe.booster.core.base.entity;

/**
 * 启用标志类实体
 *
 * @author Qt
 * @since Aug 07, 2018
 */
public interface EnabledRecordable {

	Boolean getEnabled();

	void setEnabled(Boolean enabled);

	default void enable() {
		setEnabled(Boolean.TRUE);
	}

	default boolean enabled() {
		return Boolean.TRUE.equals(getEnabled());
	}

}
