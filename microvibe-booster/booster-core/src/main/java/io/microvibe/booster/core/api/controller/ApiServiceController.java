package io.microvibe.booster.core.api.controller;

import io.microvibe.booster.core.api.ApiConstants;
import io.microvibe.booster.core.api.model.JsonData;
import io.microvibe.booster.core.api.model.RequestData;
import io.microvibe.booster.core.api.model.XmlData;
import io.microvibe.booster.core.api.tools.DataKit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@Slf4j
@SuppressWarnings("ALL")
public class ApiServiceController extends AbstractApiServiceController {

	@RequestMapping({"/openapi", "/openapi/json"})
	@ResponseBody
	public void api4json(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String data = DataKit.fetchRequestData(request, ApiConstants.HTTP_PARAM_NAME, ApiConstants.DEFAULT_JSON_ROOT);
		doJson(data, request, response);
	}

	@RequestMapping({"/openapi/{apiName}", "/openapi/json/{apiName}"})
	@ResponseBody
	public void jsonApiSpecified(@PathVariable("apiName") String apiName,
		HttpServletRequest request, HttpServletResponse response) throws IOException {
		String data = DataKit.fetchRequestData(request, ApiConstants.HTTP_PARAM_NAME, ApiConstants.DEFAULT_JSON_BODY);
		RequestData apiRequest = JsonData.buildByTxnCode(apiName, data);
		doExec(apiRequest, request, response);
	}

	@RequestMapping("/openapi/xml")
	@ResponseBody
	public void api4xml(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String data = DataKit.fetchRequestData(request, ApiConstants.HTTP_PARAM_NAME, ApiConstants.DEFAULT_XML_ROOT);
		doXml(data, request, response);
	}

	@RequestMapping("/openapi/xml/{apiName}")
	@ResponseBody
	public void xmlApiSpecified(@PathVariable("apiName") String apiName,
		HttpServletRequest request, HttpServletResponse response) throws IOException {
		String data = DataKit.fetchRequestData(request, ApiConstants.HTTP_PARAM_NAME, ApiConstants.DEFAULT_XML_BODY);
		RequestData apiRequest = XmlData.buildByTxnCode(apiName, data);
		doExec(apiRequest, request, response);
	}

}
