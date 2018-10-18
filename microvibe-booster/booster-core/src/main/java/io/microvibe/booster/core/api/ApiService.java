package io.microvibe.booster.core.api;

import io.microvibe.booster.core.api.model.RequestData;
import io.microvibe.booster.core.api.model.ResponseData;

public interface ApiService {

	/**
	 * 对外系统提供的接口方法,接受请求报文,处理业务逻辑后返回响应报文
	 *
	 * @param request  请求报文
	 * @return 响应报文
	 */
	ResponseData execute(RequestData request);
}
