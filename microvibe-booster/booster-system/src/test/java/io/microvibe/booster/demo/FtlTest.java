package io.microvibe.booster.demo;

import io.microvibe.booster.core.base.controller.aspect.ErrorHolder;
import io.microvibe.booster.core.lang.freemarker.FreemarkerTemplate;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * @author Qt
 * @since Aug 18, 2018
 */
public class FtlTest {

	@Test
	public void test001() {
		Model model = new ExtendedModelMap();
		ErrorHolder error = new ErrorHolder(new Throwable());
		model.addAttribute("error", error);
		model.addAttribute("message", error.getMessage());
		String s = FreemarkerTemplate.instance().evalForPath(model, "/ftl/error/error.ftl");
		System.out.println(s);
	}
}
