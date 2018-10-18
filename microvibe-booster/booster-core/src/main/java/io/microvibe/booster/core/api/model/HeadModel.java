package io.microvibe.booster.core.api.model;

import java.util.Map;

public interface HeadModel extends java.io.Serializable, IJSONObject, Map<String, Object> {

	/**
	 * 返回本对象的克隆体
	 *
	 * @return 克隆体
	 */
	HeadModel clone();

	/**
	 * 返回消息代码
	 *
	 * @return 消息代码
	 */
	String getCode();

	/**
	 * 返回是否成功标志
	 *
	 * @return 是否成功
	 */
	boolean isSuccess();

	/**
	 * 返回消息内容
	 *
	 * @return 消息内容
	 */
	String getMessage();

	/**
	 * 设置消息代码
	 *
	 * @param code 消息代码
	 */
	void setCode(String code);

	/**
	 * 设置消息内容
	 *
	 * @param message 消息内容
	 */
	void setMessage(String message);

	/**
	 * 设置成功标志
	 *
	 * @param success 成功标志
	 */
	void setSuccess(boolean success);

	String getTxnCode();

	void setTxnCode(String txnCode);

}
