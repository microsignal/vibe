package io.microvibe.booster.core.api.support;

import io.microvibe.booster.commons.spring.ApplicationContextHolder;
import io.microvibe.booster.core.api.model.RequestData;
import io.microvibe.booster.core.api.model.ResponseData;

/**
 * 提供对ApiService交易接口的静态方法调用方式
 *
 * @author Qt
 * @since Oct 14, 2017
 */
public class ApiServiceSupports {

	public static ResponseData doExecute(RequestData request) {
		ApiServiceSupport apiServiceSupport = ApplicationContextHolder.getBean(ApiServiceSupport.class);
		return apiServiceSupport.execute(request);
	}

	public static ResponseData doJson(String request) {
		ApiServiceSupport apiServiceSupport = ApplicationContextHolder.getBean(ApiServiceSupport.class);
		return apiServiceSupport.doJson(request);
	}

	public static ResponseData doXml(String request) {
		ApiServiceSupport apiServiceSupport = ApplicationContextHolder.getBean(ApiServiceSupport.class);
		return apiServiceSupport.doXml(request);
	}

	public static String doJsonAsString(String request) {
		ApiServiceSupport apiServiceSupport = ApplicationContextHolder.getBean(ApiServiceSupport.class);
		return apiServiceSupport.doJsonAsString(request);
	}

	public static String doXmlAsString(String request) {
		ApiServiceSupport apiServiceSupport = ApplicationContextHolder.getBean(ApiServiceSupport.class);
		return apiServiceSupport.doXmlAsString(request);
	}

}
