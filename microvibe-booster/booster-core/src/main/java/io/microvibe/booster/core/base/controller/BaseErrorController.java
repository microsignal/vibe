package io.microvibe.booster.core.base.controller;

import io.microvibe.booster.core.api.model.ResponseData;
import io.microvibe.booster.core.api.tools.DataKit;
import io.microvibe.booster.core.base.controller.aspect.ErrorHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Locale;

// org.springframework.boot.autoconfigure.web.BasicErrorController
@Controller
@RequestMapping("${server.error.path:/error}")
@EnableConfigurationProperties(ServerProperties.class)
public class BaseErrorController extends AbstractErrorController {
	private final ErrorProperties errorProperties;
	private final ErrorAttributes errorAttributes;
	@Value("${spring.mvc.error.page:error/error.ftl}")
	private String errorPage;

	public BaseErrorController(@Autowired() ErrorAttributes errorAttributes, @Autowired() ServerProperties serverProperties) {
		super(errorAttributes, Collections.<ErrorViewResolver>emptyList());
		this.errorProperties = serverProperties.getError();
		this.errorAttributes = errorAttributes;
	}

	protected boolean isIncludeStackTrace(HttpServletRequest request, MediaType produces) {
		ErrorProperties.IncludeStacktrace include = this.errorProperties.getIncludeStacktrace();
		if (include == ErrorProperties.IncludeStacktrace.ALWAYS) {
			return true;
		}
		if (include == ErrorProperties.IncludeStacktrace.ON_TRACE_PARAM) {
			return getTraceParameter(request);
		}
		return true;
	}

	@Override
	protected boolean getTraceParameter(HttpServletRequest request) {
		String parameter = request.getParameter("trace");
		if (parameter == null) {
			return true;
		}
		return !"false".equals(parameter.toLowerCase(Locale.ENGLISH));
	}

	@Override
	public String getErrorPath() {
		return this.errorProperties.getPath();
	}

	@RequestMapping(path = {"500", ""}, produces = {"text/plain", "text/html"})
	public String errorHtml(Model model, HttpServletRequest request, HttpServletResponse response) throws NoHandlerFoundException {
		HttpStatus status = getStatus(request);
		ErrorHolder error = new ErrorHolder(request, errorAttributes);
		if (HttpStatus.NOT_FOUND == status) {
			throw new NoHandlerFoundException(request.getMethod(), error.getPath(), new HttpHeaders());
		}
		model.addAttribute("error", error);
		model.addAttribute("message", error.getMessage());
		response.setStatus(status.value());
		return errorPage;
	}

	@RequestMapping(path = {"500", ""})
	@ResponseBody
	public ResponseEntity error(HttpServletRequest request) throws NoHandlerFoundException {
		/*Map<String, Object> body = getErrorAttributes(request,
			isIncludeStackTrace(request, MediaType.ALL));*/
		HttpStatus status = getStatus(request);
		ResponseData body = DataKit.buildErrorResponse();
		ErrorHolder error = new ErrorHolder(request, errorAttributes);
		if (HttpStatus.NOT_FOUND == status) {
			throw new NoHandlerFoundException(request.getMethod(), error.getPath(), new HttpHeaders());
		}
		body.setBody("error", error);
		body.setBody("message", error.getMessage());
		return new ResponseEntity<>(body, status);
	}


	@RequestMapping(path = "{status:\\d+}", produces = {"text/plain", "text/html"})
	public String common(@PathVariable("status") String status, String message, Model model) {
		if (!StringUtils.isEmpty(message)) {
			model.addAttribute("message", message);
		}
		return "error/" + status + ".ftl";
	}

}
