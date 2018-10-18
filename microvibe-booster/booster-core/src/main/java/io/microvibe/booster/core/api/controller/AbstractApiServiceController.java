package io.microvibe.booster.core.api.controller;

import io.microvibe.booster.core.api.ApiConstants;
import io.microvibe.booster.core.api.model.*;
import io.microvibe.booster.core.api.support.ApiServiceSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public abstract class AbstractApiServiceController {

	@Autowired
	protected ApiServiceSupport apiServiceSupport;

	protected void doExec(RequestData data, HttpServletRequest request, HttpServletResponse response)
		throws IOException {
		exec(data, response);
	}

	protected final void exec(RequestData data, HttpServletResponse response) throws IOException {
		ResponseData responseData = apiServiceSupport.execute(data);
		writeAndFlush(response, responseData);
	}

	protected void doJson(String data, HttpServletRequest request, HttpServletResponse response) throws IOException {
		ResponseData responseData = apiServiceSupport.doJson(data);
		writeAndFlush(response, responseData);
	}

	protected void doXml(String data, HttpServletRequest request, HttpServletResponse response) throws IOException {
		ResponseData responseData = apiServiceSupport.doXml(data);
		writeAndFlush(response, responseData);
	}

	protected void writeAndFlush(HttpServletResponse response, ResponseData data) throws IOException {
		response.setCharacterEncoding("utf-8");
		if (data instanceof JsonData) {
			response.setContentType("text/json;charset=utf-8");
		} else if (data instanceof XmlData) {
			response.setContentType("text/xml;charset=utf-8");
		} else {
			response.setContentType("text/html;charset=utf-8");
		}
		// 是否合并到http响应头
		if (data.getHead().getBooleanValue(ApiConstants.HTTP_HEADER_MERGED)) {
			HeadModel head = data.getHead();
			head.forEach((key, val) -> response.setHeader(key, String.valueOf(val)));
			writeAndFlush(response, data.getBody().toString());
		} else {
			writeAndFlush(response, data.toString());
		}
	}

	protected void writeAndFlush(HttpServletResponse response, String resp) throws IOException {
		PrintWriter writer = response.getWriter();
		writer.write(resp);
		writer.flush();
	}
}
