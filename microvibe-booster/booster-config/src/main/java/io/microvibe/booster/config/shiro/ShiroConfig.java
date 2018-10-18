package io.microvibe.booster.config.shiro;

import io.microvibe.booster.commons.spring.AfterApplicationContextHolder;
import io.microvibe.booster.commons.utils.StringUtils;
import io.microvibe.booster.config.task.AfterTaskConfig;
import io.microvibe.booster.core.base.shiro.authc.IShiroAuthcService;
import io.microvibe.booster.core.base.shiro.authc.impl.NoopShiroAuthcService;
import io.microvibe.booster.core.base.shiro.cache.SpringCacheManagerWrapper;
import io.microvibe.booster.core.base.shiro.filter.CustomFormAuthenticationFilter;
import io.microvibe.booster.core.base.shiro.filter.CustomerAnonymousFilter;
import io.microvibe.booster.core.base.shiro.filter.CustomerLogoutFilter;
import io.microvibe.booster.core.base.shiro.filter.JCaptchaValidateFilter;
import io.microvibe.booster.core.base.shiro.realm.UserRealm;
import io.microvibe.booster.core.base.shiro.session.*;
import io.microvibe.booster.core.base.shiro.session.scheduler.SpringSessionValidationScheduler;
import io.microvibe.booster.core.env.ShiroEnv;
import io.microvibe.booster.core.env.SystemEnv;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("ALL")
@Configuration
@Slf4j
public class ShiroConfig {

	@Autowired
	ApplicationContext context;
	@Autowired
	ShiroEnv shiroEnv;
	@Autowired
	SystemEnv systemEnv;

	@Bean
	IShiroAuthcService shiroAuthcService()
		throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		String shiroAuthcServiceClass = shiroEnv.getShiroAuthcServiceClass();
		IShiroAuthcService bean = null;
		if (StringUtils.isNotBlank(shiroAuthcServiceClass)) {
			try {
				bean = (IShiroAuthcService) Class.forName(shiroAuthcServiceClass.trim()).newInstance();
			} catch (Exception e) {
				log.error("无法初始化: {}", shiroAuthcServiceClass);
			}
		}
		if (bean == null) {
			bean = new NoopShiroAuthcService();
		}
		return bean;
	}

	@Bean
	public UserRealm userRealm() {
		UserRealm userRealm = new UserRealm();
		userRealm.setAuthenticationCachingEnabled(false);// 清理缓存需要主动调用退出接口,或超时
		userRealm.setAuthorizationCachingEnabled(true);
		userRealm.setAuthenticationCacheName(shiroEnv.getAuthenticationCacheName());
		userRealm.setAuthorizationCacheName(shiroEnv.getAuthorizationCacheName());
		userRealm.setCacheManager(shiroCacheManager());
		return userRealm;
	}

	@Bean
	public SessionIdGenerator sessionIdGenerator() {
		return new JavaUuidSessionIdGenerator();
	}

	@Bean
	public Cookie sessionIdCookie() {
		return new SimpleCookie(shiroEnv.getUidCookieName());
	}

	@Bean
	public Cookie rememberMeCookie() {
		return new SimpleCookie(shiroEnv.getRememeberMeCookieName());
	}

	@Bean
	@ConditionalOnMissingBean(IOnlineSessionDAOService.class)
	public IOnlineSessionDAOService onlineSessionDAOService() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		String onlineSessionDAOService = shiroEnv.getOnlineSessionDAOService();
		if (onlineSessionDAOService != null && onlineSessionDAOService.trim().length() > 0) {
			return (IOnlineSessionDAOService) Class.forName(onlineSessionDAOService).newInstance();
		} else {
			return new NoopOnlineSessionDAOService();
		}
	}

	@Bean
	public OnlineSessionDAO onlineSessionDAO() {
		OnlineSessionDAO bean = new OnlineSessionDAO();
		bean.setSessionIdGenerator(sessionIdGenerator());
		bean.setActiveSessionsCacheName(shiroEnv.getSessionCacheName());
		bean.setCacheManager(shiroCacheManager());
		return bean;
	}

	@Bean
	public OnlineSessionFactory onlineSessionFactory() {
		return new OnlineSessionFactory();
	}

	@Bean
	public RememberMeManager rememberMeManager() {
		CookieRememberMeManager bean = new CookieRememberMeManager();
		bean.setCipherKey(Base64.decode(shiroEnv.getRememeberMeCookieCipherKey()));
		// Cookie rememberMeCookie = context.getBean("rememberMeCookie", SimpleCookie.class);
		bean.setCookie(rememberMeCookie());
		return bean;
	}

	@Bean
	public SpringCacheManagerWrapper shiroCacheManager() {
		CacheManager cacheManager = context.getBean(CacheManager.class);
		SpringCacheManagerWrapper bean = new SpringCacheManagerWrapper();
		bean.setCacheManager(cacheManager);
		return bean;
	}

	@Bean // 会话管理器
	public OnlineWebSessionManager sessionManager() {
		OnlineWebSessionManager bean = new OnlineWebSessionManager();
		bean.setGlobalSessionTimeout(shiroEnv.getGlobalSessionTimeout());
		bean.setSessionFactory(onlineSessionFactory());
		bean.setSessionDAO(onlineSessionDAO());
		bean.setDeleteInvalidSessions(true);

		bean.setSessionValidationInterval(shiroEnv.getSessionValidationInterval());
		bean.setSessionValidationSchedulerEnabled(true);

		// 会话验证调度
		SpringSessionValidationScheduler sessionValidationScheduler = new SpringSessionValidationScheduler();
		sessionValidationScheduler.setSessionValidationInterval(shiroEnv.getSessionValidationInterval());
		sessionValidationScheduler.setSessionManager(bean);
		TaskScheduler scheduler = context.getBean(TaskScheduler.class);
		sessionValidationScheduler.setScheduler(scheduler);
		sessionValidationScheduler.enableSessionValidation();//启动调度

		bean.setSessionValidationScheduler(sessionValidationScheduler);

		bean.setCacheManager(shiroCacheManager());
		bean.setSessionIdCookieEnabled(true);
		bean.setSessionIdCookie(sessionIdCookie());
		return bean;
	}

	@Bean
	public WebSecurityManager securityManager() {
		DefaultWebSecurityManager bean = new DefaultWebSecurityManager();
		bean.setRealm(userRealm());
		bean.setSessionManager(sessionManager());
		bean.setRememberMeManager(rememberMeManager());
		return bean;
	}

	// shiro advisor
	@Bean
	AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
		SecurityManager securityManager = securityManager();
		AuthorizationAttributeSourceAdvisor bean = new AuthorizationAttributeSourceAdvisor();
		bean.setSecurityManager(securityManager);
		return bean;
	}

	@Bean
	ShiroFilterFactoryBean shiroFilter() {
		org.apache.shiro.SecurityUtils.setSecurityManager(securityManager());

		ShiroFilterFactoryBean factory = new ShiroFilterFactoryBean();
		factory.setSecurityManager(securityManager());
		factory.setLoginUrl(shiroEnv.getLoginUrl());
		factory.setUnauthorizedUrl(shiroEnv.getUnauthorizedUrl());

		// 覆盖 org.apache.shiro.web.filter.mgt.DefaultFilter
		Map<String, Filter> filters = new LinkedHashMap<>();
		filters.put("anon", anonFilter());
		filters.put("captcha", captchaFilter());
		filters.put("logout", logoutFilter());
		filters.put("authc", authcFilter());
//		filters.put("sysUser", sysUserFilter());
//		filters.put("onlineSession", onlineSessionFilter());
//		filters.put("syncOnlineSession", syncOnlineSessionFilter());
		factory.setFilters(filters);

		Map<String, String> filterChains = shiroEnv.getFilterMap();
		if (filterChains != null) {
			factory.setFilterChainDefinitionMap(filterChains);
		} else {
			filterChains = new LinkedHashMap<>();
			filterChains.put("/resources/**", "anon");
			filterChains.put("/static/**", "anon");
			filterChains.put("/public/**", "anon");
			filterChains.put("/webjars/**", "anon");
			filterChains.put("/plugin/**", "anon");
			filterChains.put("/ftl/**", "anon");
			filterChains.put("/favicon.ico", "anon");
			filterChains.put("/jcaptcha*", "anon");
			filterChains.put("/openapi/**", "anon");
			filterChains.put("/api/open/**", "anon");
			filterChains.put("/api/login/**", "anon");
			filterChains.put("/demo/**", "anon");

			filterChains.put("/logout", "logout");
			filterChains.put("/login", "captcha,authc");
			filterChains.put("/**", "anon");
//			filterChains.put("/**", "authc,anon");
//			filterChains.put("/**", "sysUser,onlineSession,authc,syncOnlineSession,anon");
//			filterChains.put("/**", "sysUser,onlineSession,user,syncOnlineSession,perms,roles");
		}

		factory.setFilterChainDefinitionMap(filterChains);
		return factory;
	}

	@Bean
	public CustomerAnonymousFilter anonFilter() {
		return new CustomerAnonymousFilter();
	}

	@Bean // 防止SpringBoot将Filter自动装配到Servlet上下文
	public FilterRegistrationBean anonFilterRegistration() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(anonFilter());
		registration.setEnabled(false);
		return registration;
	}

	@Bean
	public CustomerLogoutFilter logoutFilter() {
		return new CustomerLogoutFilter(shiroEnv.getLogoutSuccessUrl());
	}

	@Bean // 防止SpringBoot将Filter自动装配到Servlet上下文
	public FilterRegistrationBean logoutFilterRegistration() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(logoutFilter());
		registration.setEnabled(false);
		return registration;
	}

	@Bean
	public CustomFormAuthenticationFilter authcFilter() {
		CustomFormAuthenticationFilter bean = new CustomFormAuthenticationFilter();
		bean.setSuccessUrl(shiroEnv.getDefaultSuccessUrl());
//		bean.setAdminSuccessUrl(shiroEnv.getAdminSuccessUrl());
		bean.setUsernameParam(shiroEnv.getUsernameParam());
		bean.setPasswordParam(shiroEnv.getPasswordParam());
		bean.setRememberMeParam(shiroEnv.getRememberMeParam());
		bean.setFailureKeyAttribute(shiroEnv.getFailureKeyAttribute());
		return bean;
	}

	@Bean // 防止SpringBoot将Filter自动装配到Servlet上下文
	public FilterRegistrationBean authcFilterRegistration() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(authcFilter());
		registration.setEnabled(false);
		return registration;
	}

	@Bean
	public JCaptchaValidateFilter captchaFilter() {
		JCaptchaValidateFilter bean = new JCaptchaValidateFilter();
		bean.setJcaptchaEbabled(shiroEnv.isJcaptchaEnable());
		bean.setJcaptchaParam("captcha");
		bean.setJcaptchaKeyParam("captchaKey");
		bean.setJcapatchaErrorUrl(shiroEnv.getJcaptchaErrorUrl());
		return bean;
	}

	@Bean // 防止SpringBoot将Filter自动装配到Servlet上下文
	public FilterRegistrationBean captchaFilterRegistration() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(captchaFilter());
		registration.setEnabled(false);
		return registration;
	}
}
