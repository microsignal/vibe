package io.microvibe.booster.demo;

import io.microvibe.booster.core.api.model.RequestData;
import io.microvibe.booster.core.api.tools.DataKit;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Qt
 * @since Aug 28, 2018
 */
public class DataTest {
	public static void main(String[] args) {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("app_id", "test1234");
		request.addParameter("scope", "test1234");
		request.addParameter("auth_code", "test1234");
		request.addHeader("alipayApiType", "sdk");

		RequestData requestData = DataKit.buildRequest("test", request);
		System.out.println(requestData);
	}
}
