package io.microvibe.util.castor.support;

import io.microvibe.util.castor.Castor;

public abstract class AbstractCastor<T> implements Castor<T> {
	protected Class<T> type;

	public AbstractCastor(Class<T> type) {
		this.type = type;
	}

	/**
	 * 将参数对象转换为指定的类型
	 *
	 * @param orig
	 *            原类型对象
	 * @return 新类型对象
	 */
	public abstract T cast(Object orig);

}
