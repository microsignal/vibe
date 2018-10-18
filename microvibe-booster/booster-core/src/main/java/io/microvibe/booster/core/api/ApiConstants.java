package io.microvibe.booster.core.api;

@SuppressWarnings("AlibabaAvoidCommentBehindStatement")
public interface ApiConstants {
	String HTTP_PARAM_NAME = "data";
	String HTTP_HEADER_MERGED = "httpMerged"; // 是否合并到http响应头

	String DEFAULT_JSON_ROOT = "{}";
	String DEFAULT_JSON_BODY = "{}";
	String DEFAULT_XML_ROOT = "<" + ApiConstants.ROOT + "></" + ApiConstants.ROOT + ">";
	String DEFAULT_XML_BODY = "<" + ApiConstants.BODY + "></" + ApiConstants.BODY + ">";

	String ROOT = "root";
	String HEAD = "head";
	String BODY = "body";
//	String HEAD = "meta";
//	String BODY = "data";
	/**
	 * 响应成功标志
	 */
	String HEAD_SUCCESS = "success";
	/**
	 * 响应代码
	 */
	String HEAD_CODE = "code";
	/**
	 * 响应描述信息
	 */
	String HEAD_MESSAGE = "message";

	String BODY_SEARCH = "search";
	String BODY_SEARCH_KEY = "key";
	String BODY_SEARCH_OP = "op";
	String BODY_SEARCH_VALUE = "val";
	String BODY_SEARCH_CONJ = "conj";

	String BODY_LIMIT = "limit";
	String BODY_LIMIT_NUM = "pn";
	String BODY_LIMIT_SIZE = "ps";

	String BODY_SORT = "sort";
	String BODY_SORT_KEY = "key";
	String BODY_SORT_DIRECTION = "dir";

	String BODY_LIST = "list";
	String BODY_TOTAL = "total";
	String BODY_SORTABLE = "sortable";

	/**
	 * 交易代码
	 */
	String TXN_CODE = "txnCode";
	/**
	 * 交易日期
	 */
	String TXN_DATE = "txnDate";
	/**
	 * 交易时间
	 */
	String TXN_TIME = "txnTime";
	/**
	 * 交易序列号
	 */
	String TXN_SERIAL = "txnSerial";

	String APP_NAME = "appName";
	String APP_ID = "appId";
	String APP_SECRET = "appSecret";
	String ACCESS_TOKEN = "accessToken";
	String SHIRO_SESSION_ID = "shiroSid";

}
