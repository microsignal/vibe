package io.microvibe.booster.system.controller;

import io.microvibe.booster.commons.spring.MessageResources;
import io.microvibe.booster.core.accessor.CacheAccessor;
import io.microvibe.booster.core.api.model.BodyModel;
import io.microvibe.booster.core.api.model.RequestData;
import io.microvibe.booster.core.api.model.ResponseData;
import io.microvibe.booster.core.api.tools.DataKit;
import io.microvibe.booster.core.base.controller.AbstractBaseController;
import io.microvibe.booster.core.base.web.captcha.VerifyCode;
import io.microvibe.booster.core.base.web.utils.HttpSpy;
import io.microvibe.booster.core.env.ShiroEnv;
import io.microvibe.booster.core.log.Log;
import io.microvibe.booster.system.service.SysMenuService;
import io.microvibe.booster.system.service.SysUserService;
import io.microvibe.booster.system.toolkit.Users;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录、退出页面
 */
@Controller
@Slf4j
public class BaseLoginController extends AbstractBaseController {
	public static final String SYSTEM = "system";
	public static final String MESSAGE = "message";//消息key
	@Autowired
	private ApplicationContext context;
	@Autowired
	private MessageSource messageSource;
	@Autowired
	private SysUserService userService;
	@Autowired
	private SysMenuService menuService;

	@GetMapping("/logout")
	public String logout(Model model, HttpServletRequest request) {
		Subject subject = SecurityUtils.getSubject();
		subject.logout();
		model.addAttribute(MESSAGE, messageSource.getMessage("user.logout.success", null, null));
		return "/login.ftl";
	}

	@GetMapping({"/login", "/{login:login;?.*}"})
	public String getLogin(Model model, HttpServletRequest request) {
		if (!StringUtils.isEmpty(request.getParameter(MESSAGE))) {
			model.addAttribute(MESSAGE, request.getParameter(MESSAGE));
		} else {
			// 表示退出
			if (!StringUtils.isEmpty(request.getParameter("logout"))) {
				model.addAttribute(MESSAGE, MessageResources.getMessage("user.logout.success"));
			}

			// 表示用户删除了 @see org.apache.shiro.web.filter.user.SysUserFilter
			if (!StringUtils.isEmpty(request.getParameter("notfound"))) {
				model.addAttribute(MESSAGE, MessageResources.getMessage("user.notfound"));
			}

			// 表示用户被管理员强制退出
			if (!StringUtils.isEmpty(request.getParameter("forcelogout"))) {
				model.addAttribute(MESSAGE, MessageResources.getMessage("user.forcelogout"));
			}

			// 表示用户输入的验证码错误
			if (!StringUtils.isEmpty(request.getParameter("jcaptchaError"))) {
				model.addAttribute(MESSAGE, MessageResources.getMessage("jcaptcha.validate.error"));
			}

			if (!StringUtils.isEmpty(request.getParameter("unknown"))) {
				model.addAttribute(MESSAGE, MessageResources.getMessage("user.unknown.error"));
			}

			// 登录失败了 提取错误消息
			Exception shiroLoginFailureEx = (Exception) request
				.getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
			if (shiroLoginFailureEx != null) {
				model.addAttribute(MESSAGE, shiroLoginFailureEx.getMessage());
			}
		}

		Subject subject = SecurityUtils.getSubject();
		if (subject.isAuthenticated()) {
			subject.logout();
		}
		return "/login.ftl";
	}

	/**
	 * 登录动作, 使用此方法时不可被 shiro:authc 拦截
	 *
	 * @param model
	 * @return
	 */
	@PostMapping("/login")
	@Log(module = SYSTEM, content = "用户登录", pointcut = {Log.Pointcut.AfterReturning})
	public String postLogin(Model model, HttpServletRequest request) {
		return doPostLogin(model, request);
	}

	@RequestMapping("/doLogin")
	@Log(module = SYSTEM, content = "用户登录", pointcut = {Log.Pointcut.AfterReturning})
	public String doPostLogin(Model model, HttpServletRequest request) {
		Subject subject = SecurityUtils.getSubject();
		ShiroEnv shiroEnv = context.getBean(ShiroEnv.class);

		try {
			String username = WebUtils.getCleanParam(request, shiroEnv.getUsernameParam());
			String password = WebUtils.getCleanParam(request, shiroEnv.getPasswordParam());
			Boolean rememberMe = WebUtils.isTrue(request, shiroEnv.getRememberMeParam());
			AuthenticationToken token = new UsernamePasswordToken(username, password.toCharArray(), rememberMe);
			subject.login(token);
			Users.clearCurrentCache();
			return "redirect:/index";
		} catch (AuthenticationException e) {
			String message = e.getMessage();
			log.error(message, e);
			model.addAttribute(MESSAGE, message);
			return "redirect:/login";
		}
	}

	@PostMapping("/api/login")
	@ResponseBody
	@Log(module = SYSTEM, content = "用户通过API登录", pointcut = {Log.Pointcut.AfterReturning})
	public ResponseData apiLoginPost(RequestData requestData, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		BodyModel data = requestData.getBody();
		Users.login(data);
		return buildLoginSuccessResponse(requestData, httpServletRequest, httpServletResponse);
	}

	private ResponseData buildLoginSuccessResponse(RequestData reqApiData, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {
		// 登录成功后,返回会话令牌, 即后续接口的Http请求头Access-Token的值
		String token = HttpSpy.buildAccessTokenHeader(httpServletResponse, httpServletRequest);
		ResponseData responseData = DataKit.buildSuccessResponse();
		responseData.setBody("token", token);
		onLoginSuccess(httpServletRequest);
		return responseData;
	}

	private void onLoginSuccess(HttpServletRequest request) {
		// todo 登录成功后踢出同用户的其他会话
		Session session = SecurityUtils.getSubject().getSession(false);
		if (session == null) {
			return;
		}
		String sessionId = String.valueOf(session.getId());
		Long currentUserId = Users.getCurrentUserId();
	}


	@GetMapping(value = "/api/login/captcha")
	public void captcha(HttpServletResponse response, HttpServletRequest request) {
		VerifyCode.output(response, verifyCode -> {
			Cache captchaCache = CacheAccessor.getCaptchaCache();
			String id = StringUtils.trimToNull(request.getParameter("id"));
			if (id == null) {
				id = request.getSession(true).getId();
			}
			captchaCache.put(id, verifyCode.toLowerCase());
		});
	}


}
