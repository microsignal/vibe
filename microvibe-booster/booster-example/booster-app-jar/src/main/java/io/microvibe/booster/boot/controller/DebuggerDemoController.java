package io.microvibe.booster.boot.controller;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Qt
 * @since Aug 13, 2018
 */
@Controller
@RequestMapping("/demo/debug")
public class DebuggerDemoController {

	@RequestMapping("")
	@ResponseBody
	public Object demo(HttpServletRequest request,HttpServletResponse response) {
		JSONObject json = new JSONObject();

		return json;
	}
}
