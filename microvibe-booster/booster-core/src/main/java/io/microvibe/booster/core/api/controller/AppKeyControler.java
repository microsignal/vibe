package io.microvibe.booster.core.api.controller;

import io.microvibe.booster.core.api.ApiConstants;
import io.microvibe.booster.core.api.model.JsonData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class AppKeyControler extends AbstractApiServiceController {

	static final Logger logger = LoggerFactory.getLogger(AppKeyControler.class);
	@Autowired
	ApplicationContext context;

	@RequestMapping({"/openapi/getAppKey"})
	@ResponseBody
	public void api4json(HttpServletRequest request, HttpServletResponse response)
		throws IOException {
		// doJson("{head:{txnCode:'getAppKey'}}", request, response);
		JsonData apiData = JsonData.buildByTxnCode("getAppKey");
		apiData.setBody(ApiConstants.APP_ID, request.getParameter(ApiConstants.APP_ID));
		apiData.setBody(ApiConstants.APP_NAME, request.getParameter(ApiConstants.APP_NAME));
		doExec(apiData, request, response);
	}

}
