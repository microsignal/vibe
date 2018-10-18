package io.microvibe.booster.core.api.model;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

public interface IJSONObject extends Map<String, Object> {

	void putAll(Object o);

	/**
	 * 通过级联的key值表达式,获取对应的value值
	 *
	 * @param path 如:<code>data.users[0].id</code>
	 * @return
	 */
	Object getByKeyPath(String path);

	/**
	 * 通过级联的key值表达式,获取对应的value值
	 *
	 * @param path 如:<code>data.users[0]</code>
	 * @return
	 */
	<T> T getByKeyPath(String path, Class<T> clazz);

	/**
	 * 尝试转换为指定类型对象,并对之作有效性校验,失败时抛出异常
	 *
	 * @param clazz
	 * @return
	 */
	<T> T toValidJavaObject(Class<T> clazz);

	/**
	 * 尝试转换为指定类型对象,并对之作有效性校验,失败时抛出异常
	 */
	<T> T toValidJavaObject(String key, Class<T> clazz);

	/**
	 * 获取指定类型对象,并对之作有效性校验,失败时抛出异常
	 */
	<T> T getValidObject(String key, Class<T> clazz);

	/**
	 * 尝试转换为指定类型对象,失败时抛出异常
	 */
	<T> T toJavaObject(Class<T> clazz);

	/**
	 * 尝试转换为指定类型对象,失败时抛出异常
	 */
	<T> T toJavaObject(String key, Class<T> clazz);

	/**
	 * 获取指定类型对象,失败时抛出异常
	 */
	<T> T getObject(String key, Class<T> clazz);

	JSONObject getJSONObject(String key);

	JSONArray getJSONArray(String key);

	Boolean getBoolean(String key);

	byte[] getBytes(String key);

	boolean getBooleanValue(String key);

	Byte getByte(String key);

	byte getByteValue(String key);

	Short getShort(String key);

	short getShortValue(String key);

	Integer getInteger(String key);

	int getIntValue(String key);

	Long getLong(String key);

	long getLongValue(String key);

	Float getFloat(String key);

	float getFloatValue(String key);

	Double getDouble(String key);

	double getDoubleValue(String key);

	BigDecimal getBigDecimal(String key);

	BigInteger getBigInteger(String key);

	String getString(String key);

	Date getDate(String key);

	java.sql.Date getSqlDate(String key);

	java.sql.Timestamp getTimestamp(String key);

}
