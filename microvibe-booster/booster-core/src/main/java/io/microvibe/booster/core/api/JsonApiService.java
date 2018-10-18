package io.microvibe.booster.core.api;

import io.microvibe.booster.core.api.model.ResponseData;

public interface JsonApiService {

	/**
	 * 接受JSON格式的请求报文,返回JSON格式的响应报文
	 *
	 * @param request  请求报文
	 * @param response 响应报文
	 * @return
	 */
	ResponseData doJson(String requestStr);

	/**
	 * @return
	 * @see #doJson(String)
	 */
	String doJsonAsString(String requestStr);
}
