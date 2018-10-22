package io.microvibe.booster.core.base.controller.aspect;

import io.microvibe.booster.commons.spring.MessageResources;
import io.microvibe.booster.core.api.ApiException;
import io.microvibe.booster.core.api.ReplyCode;
import io.microvibe.booster.core.api.tools.DataKit;
import io.microvibe.booster.core.base.web.utils.HttpSpy;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.AuthorizationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.List;
import java.util.Set;

@ControllerAdvice
@ResponseBody
public class ExceptionAdvice {
	static Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);
	@Value("${spring.mvc.error.page:error/error.ftl}")
	private String errorPage;


	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public Object handleMissingServletRequestParameterException(MissingServletRequestParameterException e, HttpServletRequest request) {
		logger.error("缺少请求参数", e);
		if (!HttpSpy.isAjaxRequest(request)) {
			return asModelAndView(e);
		}
		return DataKit.buildResponse(ReplyCode.Error,
			MessageResources.getMessage("mvc.missing_request_parameter"));
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public Object handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request) {
		logger.error("参数解析失败", e);
		if (!HttpSpy.isAjaxRequest(request)) {
			return asModelAndView(e);
		}
		return DataKit.buildResponse(ReplyCode.Error, MessageResources.getMessage("mvc.http_message_not_readable"));
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
		logger.error("参数验证失败", e);
		if (!HttpSpy.isAjaxRequest(request)) {
			return asModelAndView(e);
		}
		BindingResult result = e.getBindingResult();
		FieldError error = result.getFieldError();
		if (error == null) {
			return DataKit.buildResponse(ReplyCode.Error, result.getObjectName() + "验证不通过");
		}
		String field = error.getField();
		String code = error.getDefaultMessage();
		String message = String.format("%s:%s", field, code);
		return DataKit.buildResponse(ReplyCode.Error, message);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(BindException.class)
	public Object handleBindException(BindException e, HttpServletRequest request) {
		logger.error("参数绑定失败", e);
		if (!HttpSpy.isAjaxRequest(request)) {
			return asModelAndView(e);
		}
		BindingResult result = e.getBindingResult();
		FieldError error = result.getFieldError();
		if (error != null) {
			String field = error.getField();
			String code = error.getDefaultMessage();
			String message = String.format("%s:%s", field, code);
			return DataKit.buildResponse(ReplyCode.Error, message);
		} else {
			List<ObjectError> list = e.getAllErrors();
			if (list.size() < 1) {
				return DataKit.buildResponse(ReplyCode.Error, "验证失败");
			}
			return DataKit.buildResponse(ReplyCode.Error, list.get(0).getDefaultMessage());
		}
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(ConstraintViolationException.class)
	public Object handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
		logger.error("参数验证失败", e);
		if (!HttpSpy.isAjaxRequest(request)) {
			return asModelAndView(e);
		}
		Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
		ConstraintViolation<?> violation = violations.iterator().next();
		String message = violation.getMessage();
		return DataKit.buildResponse(ReplyCode.Error, "parameter:" + message);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(ValidationException.class)
	public Object handleValidationException(ValidationException e, HttpServletRequest request) {
		logger.error("参数验证失败", e);
		if (!HttpSpy.isAjaxRequest(request)) {
			return asModelAndView(e);
		}
		return DataKit.buildResponse(ReplyCode.Error, "mvc.constraint_violation");
	}

	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ExceptionHandler(AuthenticationException.class)
	public Object handleAuthenticationException(AuthenticationException e, HttpServletRequest request) {
		logger.error("会话认证失败", e);
		if (!HttpSpy.isAjaxRequest(request)) {
			return asModelAndView(e);
		}
		return DataKit.buildResponse(ReplyCode.TxnSessionUnauthenticated, e.getMessage());
	}

	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ExceptionHandler(AuthorizationException.class)
	public Object handleAuthorizationException(AuthorizationException e, HttpServletRequest request) {
		logger.error("权限验证失败", e);
		if (!HttpSpy.isAjaxRequest(request)) {
			return asModelAndView(e);
		}
		return DataKit.buildResponse(ReplyCode.TxnUnauthorizedError, e.getMessage());
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(NoHandlerFoundException.class)
	public Object handleNoFoundException(NoHandlerFoundException e, HttpServletRequest request) {
		logger.error("请求不存在", e);
		if (!HttpSpy.isAjaxRequest(request)) {
			return asModelAndView(e);
		}
		return DataKit.buildResponse(ReplyCode.Error, "mvc.handler_notfound");
	}

	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public Object handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
		logger.error("不支持当前请求方法", e);
		if (!HttpSpy.isAjaxRequest(request)) {
			return asModelAndView(e);
		}
		return DataKit.buildResponse(ReplyCode.Error, "mvc.request_method_not_supported");
	}

	@ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public Object handleHttpMediaTypeNotSupportedException(Exception e, HttpServletRequest request) {
		logger.error("不支持当前媒体类型", e);
		if (!HttpSpy.isAjaxRequest(request)) {
			return asModelAndView(e);
		}
		return DataKit.buildResponse(ReplyCode.Error, "mvc.content_type_not_supported");
	}

	/**
	 * 301 永远重定向 chrome会将该状态码缓冲到本地
	 * 302 临时重定向
	 */
//	@ResponseStatus(HttpStatus.MOVED_PERMANENTLY)
	@ResponseStatus(HttpStatus.FOUND)
	@ExceptionHandler(RedirectException.class)
	public ModelAndView handleRedirectException(RedirectException e, HttpServletRequest request) {
		logger.error("通用异常", e);
		return asModelAndView(e);
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	public Object handleException(Exception e, HttpServletRequest request) {
		logger.error("通用异常", e);
		if (!HttpSpy.isAjaxRequest(request)) {
			return asModelAndView(e);
		}
		return DataKit.buildResponse(e);
	}

	// @ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(ApiException.class)
	public Object handleApiException(ApiException e, HttpServletRequest request) {
		logger.error("自定义接口异常", e);
		if (!HttpSpy.isAjaxRequest(request)) {
			return asModelAndView(e);
		}
		return DataKit.buildResponse(e);
	}

	private ModelAndView asModelAndView(RedirectException e) {
		logger.error(e.getMessage(), e);
		ModelAndView mv = new ModelAndView();
		ErrorHolder error = new ErrorHolder(e.getCause());
		mv.addObject("error", error);
		mv.addObject("message", error.getMessage());
		mv.setViewName(e.getRedirectUri());
		return mv;
	}

	private ModelAndView asModelAndView(Exception e) {
		ModelAndView mv = new ModelAndView();
		ErrorHolder error = new ErrorHolder(e);
		mv.addObject("error", error);
		mv.addObject("message", error.getMessage());
		mv.setViewName(errorPage);
		return mv;
	}

}
