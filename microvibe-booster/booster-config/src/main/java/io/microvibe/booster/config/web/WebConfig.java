package io.microvibe.booster.config.web;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import io.microvibe.booster.core.base.web.captcha.JCaptchaFilter;
import io.microvibe.booster.core.base.web.filter.CrossFilter;
import io.microvibe.booster.core.base.web.filter.HttpRequestWrapperFilter;
import io.microvibe.booster.core.base.web.filter.RestfulApiFilter;
import io.microvibe.booster.core.base.web.filter.ResubmitFilter;
import io.microvibe.booster.core.env.CorsFilterEnv;
import io.microvibe.booster.core.env.SystemEnv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.support.ErrorPageFilter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;

@SuppressWarnings({"ALL", "AlibabaRemoveCommentedCode"})
@Configuration
public class WebConfig {

	@Autowired
	ApplicationContext context;
	@Autowired
	CorsFilterEnv corsFilterEnv;
	@Autowired
	SystemEnv systemEnv;

	// region disable spring-boot error-page-filter
	@Bean
	public ErrorPageFilter errorPageFilter() {
		return new ErrorPageFilter();
	}
	// endregion

	@Bean
	public FilterRegistrationBean disableSpringBootErrorFilter(ErrorPageFilter filter) {
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
		filterRegistrationBean.setFilter(filter);
		filterRegistrationBean.setEnabled(false);
		return filterRegistrationBean;
	}

	@Bean
	public RequestContextListener requestContextListener() {
		return new RequestContextListener();
	}

	@Bean
	public CrossFilter crossFilter() {
		CrossFilter filter = new CrossFilter();
		return filter;
	}

	@Bean
	public FilterRegistrationBean crossFilterRegistration() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		CrossFilter filter = crossFilter();
		registration.setFilter(filter);
		registration.setAsyncSupported(true);
		registration.addUrlPatterns("/*");
		registration.setDispatcherTypes(DispatcherType.REQUEST);

		registration.addInitParameter("cors.allowed.origins", corsFilterEnv.getAllowOrigin());
		registration.addInitParameter("cors.allowed.methods", corsFilterEnv.getAllowMethod());
		registration.addInitParameter("cors.allowed.headers", corsFilterEnv.getAllowHeaders());
		registration.addInitParameter("cors.exposed.headers", corsFilterEnv.getExposedHeaders());
		registration.addInitParameter("cors.support.credentials", String.valueOf(corsFilterEnv.isSupportCredentials()));
		registration.addInitParameter("cors.preflight.maxage", String.valueOf(corsFilterEnv.getPreflightMaxage()));
		registration.addInitParameter("cors.request.decorate", String.valueOf(corsFilterEnv.isRequestDecorate()));

		return registration;
	}

	/*
	// 使用 org.springframework.boot.autoconfigure.web.HttpEncodingAutoConfiguration 替代
	@Bean
	public CharacterEncodingFilter characterEncodingFilter() {
		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
		characterEncodingFilter.setEncoding("UTF-8");
		characterEncodingFilter.setForceEncoding(true);
		return characterEncodingFilter;
	}
	*/

	@Bean
	public FilterRegistrationBean encodingFilterRegistration(CharacterEncodingFilter encodingFilter) {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(encodingFilter);
		registration.setAsyncSupported(true);
		registration.addUrlPatterns("/*");
		return registration;
	}

	@Bean
	public HttpRequestWrapperFilter httpRequestWrapperFilter() {
		return new HttpRequestWrapperFilter();
	}

	@Bean
	public FilterRegistrationBean httpRequestWrapperFilterRegistration() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(httpRequestWrapperFilter());
		registration.setAsyncSupported(true);
		registration.addUrlPatterns("/*");
		return registration;
	}

	@Bean
	public RestfulApiFilter restfulApiFilter() {
		return new RestfulApiFilter();
	}

	@Bean
	public FilterRegistrationBean restfulApiFilterRegistration() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(restfulApiFilter());
		registration.setAsyncSupported(true);
		registration.addUrlPatterns("/*");
		return registration;
	}

	@Bean
	public ResubmitFilter resubmitFilter() {
		return new ResubmitFilter();
	}

	@Bean
	public FilterRegistrationBean resubmitFilterRegistration() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(resubmitFilter());
		registration.setAsyncSupported(true);
		registration.addUrlPatterns("/*");
		registration.setDispatcherTypes(DispatcherType.REQUEST);
		registration.addInitParameter("minInterval", String.valueOf(systemEnv.getResubmitMinInterval()));// 最小重复提交间隔
		registration.addInitParameter("methods", systemEnv.getResubmitMethods());// 需要过滤的请求方法
		registration.addInitParameter("enabled", String.valueOf(systemEnv.isResubmitEnabled()));
		registration.addInitParameter("whiteList", systemEnv.getResubmitWhiteList());// 不需要过滤的uri
		registration.addInitParameter("uriList", systemEnv.getResubmitUriList());// 需要过滤的uri
		return registration;
	}


//    @Bean
//    public FilterRegistrationBean restApiFilter() {
//        FilterRegistrationBean registration = new FilterRegistrationBean();
//        RestApiFilter filter = new RestApiFilter();
//        registration.setFilter(filter);
//        registration.setAsyncSupported(true);
//        filter.setXmlPrefixes("/openapi/xml/");
//        registration.addUrlPatterns("/openapi/*");
//        registration.setDispatcherTypes(DispatcherType.REQUEST);
//        return registration;
//    }


	@Bean
	@ConditionalOnBean(name = "shiroFilter")
	public FilterRegistrationBean shiroFilterRegistration() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		Filter shiroFilter = context.getBean("shiroFilter", Filter.class);
		DelegatingFilterProxy proxy = new DelegatingFilterProxy(shiroFilter);
		registration.setFilter(proxy);
		registration.setAsyncSupported(true);
		registration.addInitParameter("targetFilterLifecycle", "true");
		registration.addUrlPatterns("/*");
		registration.setDispatcherTypes(DispatcherType.REQUEST);
		return registration;
	}

	@Bean
	public FilterRegistrationBean jCaptchaFilter() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		JCaptchaFilter filter = new JCaptchaFilter();
		registration.setFilter(filter);
		registration.setAsyncSupported(true);
		registration.addUrlPatterns("/jcaptcha.jpg");
		return registration;
	}

	@Bean
	public FilterRegistrationBean DruidWebStatFilter() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		WebStatFilter filter = new WebStatFilter();
		registration.setFilter(filter);
		registration.addInitParameter("exclusions", "/static/*,*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
		registration.addInitParameter("principalSessionName", "username");
		registration.setAsyncSupported(true);
		registration.addUrlPatterns("/*");
		return registration;
	}

	@Bean
	public ServletRegistrationBean DruidStatView() {
		ServletRegistrationBean registration = new ServletRegistrationBean();
		StatViewServlet servlet = new StatViewServlet();
		registration.setServlet(servlet);
		registration.setAsyncSupported(true);
		registration.addUrlMappings("/druid/*");
		return registration;
	}


	@Bean(DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_BEAN_NAME)
	public DispatcherServlet dispatcherServlet() {
		DispatcherServlet dispatcherServlet = new DispatcherServlet();
		return dispatcherServlet;
	}

	@Bean
	public ServletRegistrationBean dispatcherServletRegistration() {
		DispatcherServlet dispatcherServlet = dispatcherServlet();
		ServletRegistrationBean registration = new ServletRegistrationBean();
		registration.setName(DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_BEAN_NAME);
		registration.setServlet(dispatcherServlet);
//		registration.addInitParameter("contextConfigLocation", "classpath:spring-mvc.xml");
		registration.addInitParameter("contextConfigLocation", MvcAdvanceConfig.class.getName());
		registration.setLoadOnStartup(1);
		registration.setAsyncSupported(true);
		registration.addUrlMappings("/", "*.do");
		return registration;
	}

}
