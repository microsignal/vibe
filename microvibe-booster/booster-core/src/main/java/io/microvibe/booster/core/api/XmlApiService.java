package io.microvibe.booster.core.api;

import io.microvibe.booster.core.api.model.ResponseData;

public interface XmlApiService {

	/**
	 * 接受XML格式的请求报文,返回XML格式的响应报文
	 *
	 * @param request  请求报文
	 * @param response 响应报文
	 * @return
	 */
	ResponseData doXml(String requestStr);

	/**
	 * @return
	 * @see #doXml(String)
	 */
	String doXmlAsString(String requestStr);
}
