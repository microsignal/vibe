package io.microvibe.booster.core.api.model;

import io.microvibe.booster.core.api.validation.ApiValidator;

public interface Data extends java.io.Serializable {

	/**
	 * 获取数据验证器
	 *
	 * @return
	 */
	ApiValidator getApiValidator();

	/**
	 * 制造本对象的一个克隆对象
	 *
	 * @return 克隆体
	 */
	Data clone();

	/**
	 * 获取数据报文头
	 *
	 * @return 报文头对象
	 */
	HeadModel getHead();

	/**
	 * 设置报文头
	 *
	 * @param key   键
	 * @param value 值
	 */
	void setHead(String key, Object value);

	/**
	 * 获取报文头值
	 *
	 * @param key 键
	 * @return 值
	 */
	Object getHead(String key);

	/**
	 * 获取报文头值
	 *
	 * @param key   键
	 * @param clazz 值类型
	 * @return 值
	 */
	<T> T getHead(String key, Class<T> clazz);

	/**
	 * 以字符串格式设置报文体
	 *
	 * @param data 报文体字符串
	 */
	void setBodyAsString(String data);

	/**
	 * 以字符串格式获取报文体
	 *
	 * @return 报文体字符串
	 */
	String getBodyAsString();

	/**
	 * 获取报文体对象
	 *
	 * @return 报文体对象
	 */
	BodyModel getBody();

	/**
	 * 获取报文体对象
	 *
	 * @param clazz 值类型
	 * @return 报文体对象
	 */
	<T> T getBody(Class<T> clazz);

	/**
	 * 获取报文体对象,并校验对象有效性
	 *
	 * @param clazz 值类型
	 * @return 值
	 */
	<T> T getValidBody(Class<T> clazz);

	/**
	 * 设置报文体
	 *
	 * @param key   键
	 * @param value 值
	 */
	void setBody(String key, Object value);

	/**
	 * 获取报文体
	 *
	 * @param key 键
	 * @return 值
	 */
	Object getBody(String key);

	/**
	 * 获取报文体
	 *
	 * @param key   键
	 * @param clazz 值类型
	 * @return 值
	 */
	<T> T getBody(String key, Class<T> clazz);

	/**
	 * 以字符串格式获取报文头
	 *
	 * @param key 键
	 * @return 报文头字符串
	 */
	String getHeadAsString(String key);

	/**
	 * 以字符串格式获取报文体
	 *
	 * @param key 键
	 * @return 报文体字符串
	 */
	String getBodyAsString(String key);

	/**
	 * 获取报文头对象,并校验对象有效性
	 *
	 * @param key   键
	 * @param clazz 值类型
	 * @return 值
	 */
	<T> T getValidHead(String key, Class<T> clazz);

	/**
	 * 获取报文体对象,并校验对象有效性
	 *
	 * @param key   键
	 * @param clazz 值类型
	 * @return 值
	 */
	<T> T getValidBody(String key, Class<T> clazz);

	String toPrettyString();
}
