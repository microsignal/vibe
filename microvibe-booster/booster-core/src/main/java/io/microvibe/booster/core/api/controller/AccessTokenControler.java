package io.microvibe.booster.core.api.controller;

import io.microvibe.booster.core.api.ApiConstants;
import io.microvibe.booster.core.api.model.JsonData;
import io.microvibe.booster.core.api.model.RequestData;
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
public class AccessTokenControler extends AbstractApiServiceController {

	static final Logger logger = LoggerFactory.getLogger(AccessTokenControler.class);
	@Autowired
	ApplicationContext context;

	@RequestMapping({"/openapi/getAccessToken"})
	@ResponseBody
	public void api4json(String appId, String appSecret, HttpServletRequest request, HttpServletResponse response)
		throws IOException {
		RequestData apiData = JsonData.buildByTxnCode("getAccessToken");
		apiData.setBody(ApiConstants.APP_ID, appId);
		apiData.setBody(ApiConstants.APP_SECRET, appSecret);

		doExec(apiData, request, response);
	}

	/*
	 * http://localhost:8080/openapi/getAccessToken?appId=dc442a24488b131e33fa&appSecret=1b351a8be11c1911c76d3d8cc6bf8e92127b4596
	 */

}
