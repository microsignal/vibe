package io.microvibe.booster.demo.controller;

import io.microvibe.booster.core.base.resource.annotation.ResourceIdentity;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Qt
 * @since Jun 04, 2018
 */
@Controller
@RequestMapping("/demo")
@ResourceIdentity("demo")
@Description("功能演示")
public class SystemDemoController {

	public static final String PATH = "/demo/";

	@RequestMapping({"/{path:.+}", "/**"})
	public String ui(HttpServletRequest request) {
		String path = request.getRequestURI();
		path = path.substring(request.getContextPath().length());
		path = path.substring(PATH.length());
		path = path.replaceFirst("\\.\\w+$", "");
		return path + ".jsp";
	}

}
