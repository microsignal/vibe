package io.microvibe.castor;

public interface Castor<T> {

	/**
	 * 将参数对象转换为指定的类型
	 *
	 * @param orig
	 *            原类型对象
	 * @return 新类型对象
	 */
	T cast(Object orig);

}
