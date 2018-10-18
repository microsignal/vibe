package io.microvibe.booster.core.base.entity;

public interface EntryableAdaptor extends Entryable {

	@Override
	default String key() {
		return name();
	}

	@Override
	default String value() {
		return String.valueOf(getInfo());
	}

	/**
	 * 枚举的固有方法
	 *
	 * @return
	 */
	String name();

	default Object getInfo() {
		return name();
	}

}
