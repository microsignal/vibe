package io.microvibe.booster.core.api.tools;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.microvibe.booster.commons.spring.MessageResources;
import io.microvibe.booster.core.api.ApiException;
import io.microvibe.booster.core.api.ReplyCode;
import io.microvibe.booster.core.api.model.*;
import io.microvibe.booster.core.api.model.impl.JsonLimitModel;
import io.microvibe.booster.core.base.mybatis.example.Builder;
import io.microvibe.booster.core.base.mybatis.example.Example;
import io.microvibe.booster.core.search.SearchKey;
import io.microvibe.booster.core.search.Searches;
import io.microvibe.booster.core.validation.Validations;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@SuppressWarnings("rawtypes")
@Slf4j
public class DataKit {

	private DataKit() {
	}

	/**
	 * 调用Spring上下文中的校验器,验证对象是否合法
	 *
	 * @param o 待验证对象
	 */
	public static <T> void validate(T o) {
		Validations.validate(o);
		/*Set<ConstraintViolation<T>> set = ValidatorAccessor.getValidator().validate(o);
		if (set.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (ConstraintViolation<T> cv : set) {
				sb.append(cv.getMessage()).append(";");
			}
			throw new ApiException(ReplyCode.RequestParamError, sb.substring(0, sb.length() - 1));
		}*/
	}

	public static Pageable toPageable(Data data) {
		return data.getBody().getPageable();
	}

	public static Sort toSort(Data data) {
		return data.getBody().getSort();
	}

	public static Map<String, Object> toSearchParam(Data data) {
		Map<String, Object> param = new HashMap<>();
		BodyModel dataModel = data.getBody();
		List<SearchModel> searchModels = dataModel.getSearches();
		for (SearchModel model : searchModels) {
			SearchKey key = Searches.toSearchKey(model.getKey(), model.getOp());
			param.put(key.toString(), key.getSymbol().repair(model.getVal()));
		}
		return param;
	}

	public static <T> Example<T> toSimpleSearchExample(Data data, Class<T> entityClass) {
		Builder<T> builder = Example.of(entityClass);
		List<SearchModel> searches = data.getBody().getSearches();
		buildSimpleSearchExample(builder, searches);
		return builder.build();
	}

	public static <T> void buildSimpleSearchExample(Builder<T> builder, List<SearchModel> searches) {
		for (SearchModel search : searches) {
			List<SearchModel> subSearches = search.getSearches();
			if (subSearches != null && subSearches.size() > 0) {
				SearchModel.Conj conj = search.getConj();
				if (conj == SearchModel.Conj.OR) {
					builder.or();
				} else {
					builder.and();
				}
				buildSimpleSearchExample(builder, subSearches);
				builder.end();
			} else {
				builder.where(search.getKey(), search.getOp(), search.getOp().repair(search.getVal()));
			}
		}
	}


	public static String fetchRequestData(HttpServletRequest request) {
		return fetchRequestData(request, null, null, null);
	}

	public static String fetchRequestData(HttpServletRequest request, String paramName) {
		return fetchRequestData(request, paramName, null, null);
	}

	public static String fetchRequestData(HttpServletRequest request, String paramName, String defaultValue) {
		return fetchRequestData(request, paramName, null, defaultValue);
	}

	public static String fetchRequestData(HttpServletRequest request, String paramName, String defaultParamName, String defaultValue) {
		String paramValue = null;
		// 尝试读取指定参数
		{
			paramName = StringUtils.trimToNull(paramName);
			if (paramName != null) {
				paramValue = StringUtils.trimToNull(request.getParameter(paramName));
			}
			if (paramValue == null) {
				defaultParamName = StringUtils.trimToNull(defaultParamName);
				/*if (defaultParamName == null) {
					defaultParamName = ApiConstants.HTTP_PARAM_NAME;
				}*/
				if (defaultParamName != null && !defaultParamName.equals(paramName)) {
					paramValue = StringUtils.trimToNull(request.getParameter(paramName));
				}
			}
		}
		// 尝试从请求体获取内容, 需要注意重复读问题 @see HttpRequestWrapperFilter
		if (paramValue == null) {
			try {
				ServletInputStream in = request.getInputStream();
				if (in != null) {
					String content = IOUtils.toString(in, request.getCharacterEncoding());
					paramValue = StringUtils.trimToNull(content);
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		// 尝试从唯一参数体中获取
		if (paramValue == null) {
			Map<String, String[]> parameterMap = request.getParameterMap();
			Set<Map.Entry<String, String[]>> entries = parameterMap.entrySet();
			if (parameterMap.size() == 1) {
				Map.Entry<String, String[]> uniqueEntry = entries.iterator().next();
				String[] uniqueVal = uniqueEntry.getValue();
				if (uniqueVal == null || uniqueVal.length == 0 ||
					uniqueVal.length == 1 && StringUtils.isBlank(uniqueVal[0])) {
					paramValue = StringUtils.trimToNull(uniqueEntry.getKey());
				}
			}
		}

		if (paramValue == null) {
			paramValue = StringUtils.trimToNull(defaultValue);
		}

		return paramValue;
	}

	public static RequestData buildRequest(HttpServletRequest request, String paramName, String defaultParamName, String defaultValue) {
		String data = fetchRequestData(request, paramName, defaultParamName, defaultValue);
		if (data != null) {
			switch (ApiDataType.current()) {
				case XML:
					return XmlData.build(data);
				default:
					return JsonData.build(data);
			}
		} else {
			RequestData requestData;
			switch (ApiDataType.current()) {
				case XML:
					requestData = XmlData.build();
					break;
				default:
					requestData = JsonData.build();
					break;
			}
			fillBodyWithHttpParameters(requestData, request);
			return requestData;
		}
	}

	public static RequestData buildRequest(HttpServletRequest request) {
		return buildRequest(request, null, null, null);
	}

	public static RequestData buildRequest(HttpServletRequest request, String paramName) {
		return buildRequest(request, paramName, null, null);
	}

	public static RequestData buildRequest(HttpServletRequest request, String paramName, String defaultValue) {
		return buildRequest(request, paramName, null, defaultValue);
	}

	public static RequestData buildRequestBody(HttpServletRequest request, String paramName, String defaultParamName, String defaultValue) {
		return buildRequest(null, request, null, null, null);
	}

	public static RequestData buildRequestBody(HttpServletRequest request) {
		return buildRequestBody(request, null, null, null);
	}

	public static RequestData buildRequestBody(HttpServletRequest request, String paramName) {
		return buildRequestBody(request, paramName, null, null);
	}

	public static RequestData buildRequestBody(HttpServletRequest request, String paramName, String defaultValue) {
		return buildRequestBody(request, paramName, null, defaultValue);
	}

	public static RequestData buildRequest(String txnCode, HttpServletRequest request, String paramName, String defaultParamName, String defaultValue) {
		String data = fetchRequestData(request, paramName, defaultParamName, defaultValue);
		RequestData requestData;
		switch (ApiDataType.current()) {
			case XML:
				requestData = XmlData.buildByTxnCode(txnCode, data);
				break;
			default:
				requestData = JsonData.buildByTxnCode(txnCode, data);
				break;
		}
		if (data == null) {
			fillHeadWithHttpHeaders(requestData, request);
			fillBodyWithHttpParameters(requestData, request);
		}
		return requestData;
	}

	public static void fillHeadWithHttpHeaders(RequestData requestData, HttpServletRequest request) {
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String name = headerNames.nextElement();
			String header = request.getHeader(name);
			if (!requestData.getHead().containsKey(name)) {
				requestData.setHead(name, header);
			}
		}
	}

	public static void fillBodyWithHttpParameters(RequestData requestData, HttpServletRequest request) {
		Map<String, String[]> parameterMap = request.getParameterMap();
		Set<Map.Entry<String, String[]>> entries = parameterMap.entrySet();
		if (parameterMap.size() > 0) {
			for (Map.Entry<String, String[]> entry : entries) {
				String[] value = entry.getValue();
				if (value.length == 1) {
					requestData.setBody(entry.getKey(), value[0]);
				} else {
					requestData.setBody(entry.getKey(), new JSONArray(Arrays.asList(value)));
				}
			}
		}
	}

	public static RequestData buildRequest(String txnCode, HttpServletRequest request) {
		return buildRequest(txnCode, request, null, null, null);
	}

	public static RequestData buildRequest(String txnCode, HttpServletRequest request, String paramName) {
		return buildRequest(txnCode, request, paramName, null, null);
	}

	public static RequestData buildRequest(String txnCode, HttpServletRequest request, String paramName, String defaultValue) {
		return buildRequest(txnCode, request, paramName, null, defaultValue);
	}

	/**
	 * 创建请求数据对象
	 *
	 * @return 请求数据对象
	 */
	public static RequestData buildRequest() {
		switch (ApiDataType.current()) {
			case XML:
				return XmlData.build();
			default:
				return JsonData.build();
		}
	}

	/**
	 * 创建请求数据对象,以JSON格式对象作为数据源
	 *
	 * @return 请求数据对象
	 */
	public static RequestData buildRequest(JSONObject json) {
		switch (ApiDataType.current()) {
			case XML:
				return XmlData.build(json);
			default:
				return JsonData.build(json);
		}
	}

	/**
	 * 创建请求数据对象,以JSON格式串作为数据源
	 *
	 * @return 请求数据对象
	 */
	public static RequestData buildRequest(String json) {
		switch (ApiDataType.current()) {
			case XML:
				return XmlData.build(json);
			default:
				return JsonData.build(json);
		}
	}

	/**
	 * 创建请求数据对象,以JSON格式字符串符分别作为请求体
	 *
	 * @return 请求数据对象
	 */
	public static RequestData buildRequestBody(String body) {
		switch (ApiDataType.current()) {
			case XML:
				return XmlData.buildByBody(body);
			default:
				return JsonData.buildByBody(body);
		}
	}

	/**
	 * 创建请求数据对象,以JSON格式对象分别作为请求头与体
	 *
	 * @return 请求数据对象
	 */
	public static RequestData buildRequest(JSONObject head, JSONObject body) {
		switch (ApiDataType.current()) {
			case XML:
				return XmlData.build(head, body);
			default:
				return JsonData.build(head, body);
		}
	}

	/**
	 * 创建请求数据对象,以JSON格式对象分别作为请求体
	 *
	 * @return 请求数据对象
	 */
	public static RequestData buildRequestBody(JSONObject body) {
		switch (ApiDataType.current()) {
			case XML:
				return XmlData.buildByBody(body);
			default:
				return JsonData.buildByBody(body);
		}
	}

	/**
	 * 创建请求数据对象,以JSON格式字符串符分别作为请求头与体
	 *
	 * @return 请求数据对象
	 */
	public static RequestData buildRequest(String head, String body) {
		switch (ApiDataType.current()) {
			case XML:
				return XmlData.build(head, body);
			default:
				return JsonData.build(head, body);
		}
	}

	private static String message(ReplyCode replyCode, String message) {
		message = StringUtils.trimToNull(message);
		if (message != null) {
			message = MessageResources.getMessage(message);
		}
		String codeMsg = replyCode.getMessage();
		return (message == null || message.equals(codeMsg) ? codeMsg : codeMsg + ". " + message);
	}

	private static ResponseData _buildResponse(ReplyCode replyCode, String message) {
		ResponseData resp;// = JsonData.build();
		switch (ApiDataType.current()) {
			case XML:
				resp = XmlData.build();
				break;
			default:
				resp = JsonData.build();
				break;
		}
		HeadModel head = resp.getHead();

		head.setSuccess(replyCode == ReplyCode.Success);
		head.setCode(replyCode.getCode());
		head.setMessage(message);
		return resp;
	}

	/**
	 * 通过指定的响应代码与消息,创建响应数据对象
	 *
	 * @return 响应数据对象
	 */
	public static ResponseData buildResponse(ReplyCode replyCode) {
		return buildResponse(replyCode, null);
	}

	/**
	 * 创建包装异常对象后的响应数据对象
	 *
	 * @return 响应数据对象
	 */
	public static ResponseData buildResponse(Throwable e) {
		if (e instanceof ApiException) {
			return buildResponse((ApiException) e);
		} else if (e instanceof AuthenticationException) {
			return buildResponse(new ApiException(ReplyCode.TxnSessionUnauthenticated, e));
		} else if (e instanceof AuthorizationException) {
			return buildResponse(new ApiException(ReplyCode.TxnUnauthorizedError, e));
		}
		StringBuilder sb = new StringBuilder();
		for (Throwable cause = e.getCause(); cause != null; cause = cause.getCause()) {
			String msg = StringUtils.trimToNull(cause.getMessage());
			if (msg != null) {
				sb.append(cause.getClass().getName()).append(": ").append(msg).append(";\n");
			}
		}
		String message;
		if (sb.length() > 0) {
			message = sb.toString();
		} else {
			message = StringUtils.trimToNull(e.getMessage());
			if (message == null) {
				message = String.format("%s: %s", e.getClass().getName(), e.getStackTrace()[0]);
			}
		}
		return buildResponse(ReplyCode.Error, message);
	}

	/**
	 * 创建包装异常对象后的响应数据对象
	 *
	 * @return 响应数据对象
	 */
	public static ResponseData buildResponse(ApiException e) {
		return _buildResponse(e.getReplyCode(), e.getMessage());
	}

	/**
	 * 通过指定的响应代码与消息,创建响应数据对象
	 *
	 * @return 响应数据对象
	 */
	public static ResponseData buildResponse(ReplyCode replyCode, String message) {
		message = message(replyCode, message);
		return _buildResponse(replyCode, message);
	}

	/**
	 * 创建表示程序运行成功的响应数据对象
	 *
	 * @return 响应数据对象
	 */
	public static ResponseData buildSuccessResponse() {
		return buildResponse(ReplyCode.Success);
	}

	/**
	 * 创建表示程序运行成功的响应数据对象
	 *
	 * @return 响应数据对象
	 */
	public static ResponseData buildSuccessResponse(String message) {
		return buildResponse(ReplyCode.Success, message);
	}

	/**
	 * 创建表示程序运行失败的响应数据对象
	 *
	 * @return 响应数据对象
	 */
	public static ResponseData buildErrorResponse() {
		return buildResponse(ReplyCode.Error);
	}

	/**
	 * 创建表示程序运行失败的响应数据对象
	 *
	 * @return 响应数据对象
	 */
	public static ResponseData buildErrorResponse(String message) {
		return buildResponse(ReplyCode.Error, message);
	}

	public static ResponseData buildResponse(String key, Object value) {
		ResponseData resp = buildSuccessResponse();
		BodyModel body = resp.getBody();
		body.put(key, value);
		return resp;
	}

	public static ResponseData buildResponse(String[] keys, Object[] values) {
		ResponseData resp = buildSuccessResponse();
		BodyModel body = resp.getBody();
		for (int i = 0; i < keys.length; i++) {
			body.put(keys[i], values[i]);
		}
		return resp;
	}

	public static ResponseData buildResponse(Map<String, Object> param) {
		ResponseData resp = buildSuccessResponse();
		BodyModel body = resp.getBody();
		body.putAll(param);
		return resp;
	}

	/**
	 * 创建响应数据对象<br>
	 * 响应含成功消息,分页数据列表及总数,可排序字段列表,分页查询参数
	 *
	 * @param page     分页数据列表及总数
	 * @param sortable 可排序字段列表
	 * @param limit    分页查询参数
	 * @return 响应数据对象
	 */
	public static ResponseData buildResponse(Page page,
		List<String> sortable, LimitModel limit) {
		return buildResponse(page.getContent(), page.getTotalElements(), sortable, limit);
	}

	/**
	 * 创建响应数据对象<br>
	 * 响应含成功消息,分页数据列表及总数,可排序字段列表
	 *
	 * @param page     分页数据列表及总数
	 * @param sortable 可排序字段列表
	 * @return 响应数据对象
	 */
	public static ResponseData buildResponse(Page page,
		List<String> sortable) {
		LimitModel limit = buildLimitModel(page);
		return buildResponse(page.getContent(), page.getTotalElements(), sortable, limit);
	}

	/**
	 * 创建响应数据对象<br>
	 * 响应含成功消息,分页数据列表及总数
	 *
	 * @param page 分页数据列表及总数
	 * @return 响应数据对象
	 */
	public static ResponseData buildResponse(Page page) {
		LimitModel limit = buildLimitModel(page);
		return buildResponse(page.getContent(), page.getTotalElements(), null, limit);
	}

	/**
	 * 创建响应数据对象<br>
	 * 响应含成功消息,分页数据列表及总数,可排序字段列表,分页查询参数
	 *
	 * @param dataList  数据列表
	 * @param dataTotal 数据总数
	 * @param sortable  可排序字段列表
	 * @param limit     分页查询参数
	 * @return 响应数据对象
	 */
	@SuppressWarnings("unchecked")
	public static ResponseData buildResponse(List dataList, long dataTotal,
		List<String> sortable, LimitModel limit) {
		ResponseData resp = buildSuccessResponse();

		BodyModel body = resp.getBody();
		if (dataList != null) {
			body.setDataList(dataList);
		}
		if (dataTotal >= 0) {
			body.setDataTotal(dataTotal);
		}
		if (sortable != null) {
			body.setSortable(sortable);
		}
		if (limit != null) {
			body.setLimit(limit);
		}

		return resp;
	}

	/**
	 * 创建响应数据对象<br>
	 * 响应含成功消息,分页数据列表及总数,可排序字段列表
	 *
	 * @param dataList  数据列表
	 * @param dataTotal 数据总数
	 * @param sortable  可排序字段列表
	 * @return 响应数据对象
	 */
	public static ResponseData buildResponse(List dataList, long dataTotal,
		List<String> sortable) {
		return buildResponse(dataList, dataTotal, sortable, null);
	}

	/**
	 * 创建响应数据对象<br>
	 * 响应含成功消息,分页数据列表及总数
	 *
	 * @param dataList  数据列表
	 * @param dataTotal 数据总数
	 * @return 响应数据对象
	 */
	public static ResponseData buildResponse(List dataList, long dataTotal) {
		return buildResponse(dataList, dataTotal, null, null);
	}

	/**
	 * 创建响应数据对象<br>
	 * 响应含成功消息,分页数据列表
	 *
	 * @param dataList 数据列表
	 * @return 响应数据对象
	 */
	public static ResponseData buildResponse(List dataList) {
		return buildResponse(dataList, -1, null, null);
	}

	/**
	 * 转换 {@linkplain Page} 类型对象为分页查询参数对象<br>
	 *
	 * @param page 分页对象
	 * @return 分页查询参数
	 */
	public static LimitModel buildLimitModel(Page page) {
		LimitModel limit = new JsonLimitModel();
		limit.setPageNumber(page.getNumber() + 1);
		limit.setPageSize(page.getSize());
		return limit;
	}
}
