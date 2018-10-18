package io.microvibe.booster.config.web;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.alibaba.fastjson.support.spring.FastJsonJsonView;
import io.microvibe.booster.commons.spring.AfterApplicationContextHolder;
import io.microvibe.booster.core.base.controller.bind.DataArgumentResolver;
import io.microvibe.booster.core.env.BootConstants;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.web.MultipartProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewInterceptor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.MultipartConfigElement;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @author Qt
 * @since Aug 14, 2018
 */
@SuppressWarnings("ALL")
@Configuration
@ComponentScan(basePackages = BootConstants.BASE_PACKAGE, //
	useDefaultFilters = false, //
	includeFilters = {@ComponentScan.Filter(Controller.class),
		@ComponentScan.Filter(ControllerAdvice.class)}//
)
public class MvcAdvanceConfig extends WebMvcConfigurationSupport {
	@Autowired
	private ApplicationContext context;

	@Override
	protected ConfigurableWebBindingInitializer getConfigurableWebBindingInitializer() {
		return super.getConfigurableWebBindingInitializer();
	}


	@Override
	@Bean
	public FormattingConversionService mvcConversionService() {
		FormattingConversionService conversionService = context.getBean("conversionService",
			FormattingConversionService.class);
		addFormatters(conversionService);
		return conversionService;
	}

	@Override
	protected void configurePathMatch(PathMatchConfigurer configurer) {
		UrlPathHelper urlPathHelper = new UrlPathHelper();
		urlPathHelper.setRemoveSemicolonContent(false);
		configurer.setUrlPathHelper(urlPathHelper);
	}


	// static resources
	@SuppressWarnings("AlibabaCommentsMustBeJavadocFormat")
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// registry.addResourceHandler("/resources/**").addResourceLocations("/WEB-INF/resources/");
		registry.addResourceHandler("/static/**").addResourceLocations("/WEB-INF/static/");
		registry.addResourceHandler("/public/**").addResourceLocations("/WEB-INF/public/");
		registry.addResourceHandler("/jsp/**").addResourceLocations("/jsp/");
		// registry.addResourceHandler("/resources/**").addResourceLocations("classpath:/resources/");
		// registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
		// registry.addResourceHandler("/public/**").addResourceLocations("classpath:/public/");
		registry.addResourceHandler("/plugin/**").addResourceLocations("classpath:/plugin/");
		registry.addResourceHandler("/ftl/**").addResourceLocations("classpath:/ftl/");
	}


	// default servlet handler
	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();// 使 Spring MVC 对资源的处理与 Servlet 方式相同
	}


	@Override
	public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
		configurer.setDefaultTimeout(30000);
		AsyncTaskExecutor taskExecutor = context.getBean("executor", AsyncTaskExecutor.class);
		configurer.setTaskExecutor(taskExecutor);
		super.configureAsyncSupport(configurer);
	}

	@Bean
	public StringHttpMessageConverter stringHttpMessageConverter() {
		Charset UTF8 = Charset.forName("UTF-8");
		StringHttpMessageConverter bean = new StringHttpMessageConverter(UTF8);
		List<MediaType> supportedMediaTypes = new ArrayList<>();
		supportedMediaTypes.add(new MediaType("text", "plain", UTF8));
		supportedMediaTypes.add(new MediaType("*", "*", UTF8));
		supportedMediaTypes.add(MediaType.TEXT_PLAIN);
		supportedMediaTypes.add(MediaType.TEXT_HTML);
		supportedMediaTypes.add(MediaType.ALL);
		bean.setSupportedMediaTypes(supportedMediaTypes);
		return bean;
	}

	@Bean // 避免IE执行AJAX时,返回JSON出现下载文件
	public FastJsonHttpMessageConverter fastJsonHttpMessageConverter() {
		FastJsonHttpMessageConverter bean = new FastJsonHttpMessageConverter();
		bean.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON_UTF8, MediaType.APPLICATION_JSON,
			MediaType.TEXT_HTML, MediaType.TEXT_PLAIN, MediaType.ALL));
		FastJsonConfig fastJsonConfig = new FastJsonConfig();
		fastJsonConfig.setCharset(Charset.forName("UTF-8"));
		fastJsonConfig.setSerializerFeatures(
			SerializerFeature.DisableCircularReferenceDetect
			/*
			,SerializerFeature.WriteMapNullValue,
			SerializerFeature.WriteNullListAsEmpty, SerializerFeature.WriteNullStringAsEmpty,
			SerializerFeature.WriteNullNumberAsZero, SerializerFeature.WriteNullBooleanAsFalse
			*/
		);
		bean.setFastJsonConfig(fastJsonConfig);
		return bean;
	}

	@Override
	protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(stringHttpMessageConverter());
		converters.add(fastJsonHttpMessageConverter());
		addDefaultHttpMessageConverters(converters);
		super.configureMessageConverters(converters);
	}

	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		super.extendMessageConverters(converters);
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(new DataArgumentResolver());
		super.addArgumentResolvers(argumentResolvers);
	}

	// 父类已添加默认处理器
	/*
	@Override
	public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
		exceptionResolvers.add(exceptionHandlerExceptionResolver());
	}

	@Bean
	public ExceptionHandlerExceptionResolver exceptionHandlerExceptionResolver() {
		ExceptionHandlerExceptionResolver resolver = new ExceptionHandlerExceptionResolver();
		resolver.setContentNegotiationManager(mvcContentNegotiationManager());
		return resolver;
	}
	*/

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		// registry.addRedirectViewController("/", "redirect:/admin/index");
		registry.addRedirectViewController("/", "/admin/index");
	}


	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
		registry.enableContentNegotiation(new FastJsonJsonView());
		registry.viewResolver(freeMarkerViewResolver());
		registry.viewResolver(jspViewResolver());
	}


	@Bean
	@ConditionalOnClass(freemarker.template.Configuration.class)
	public FreeMarkerConfigurer freeMarkerConfigurer() throws IOException, TemplateException {
		FreeMarkerConfigurationFactory factory = new FreeMarkerConfigurationFactory();
		factory.setTemplateLoaderPath("classpath:/ftl/");
		factory.setDefaultEncoding("UTF-8");
		factory.setPreferFileSystemAccess(false);
		FreeMarkerConfigurer result = new FreeMarkerConfigurer();
		freemarker.template.Configuration configuration = factory.createConfiguration();
		configuration.setClassicCompatible(true);
		result.setConfiguration(configuration);
		Properties settings = new Properties();
		settings.put("template_update_delay", "0");
		settings.put("default_encoding", "UTF-8");
		settings.put("number_format", "0.######");
		settings.put("classic_compatible", true);
		settings.put("template_exception_handler", "ignore");
		result.setFreemarkerSettings(settings);
		return result;
	}

	@Bean
	public ViewResolver freeMarkerViewResolver() {
		FreeMarkerViewResolver resolver = new FreeMarkerViewResolver() {
			ThreadLocal<String> viewNameLocal = new ThreadLocal<>();

			@Override
			protected AbstractUrlBasedView buildView(String viewName) throws Exception {
				try {
					viewNameLocal.set(viewName);
					return super.buildView(viewName);
				} finally {
					viewNameLocal.remove();
				}
			}

			@Override
			protected String getSuffix() {
				String suffix = super.getSuffix();
				String viewName = viewNameLocal.get();
				if (viewName != null) {
					if (viewName.endsWith(suffix)) {
						return "";
					}
				}
				return suffix;
			}
		};
		resolver.setOrder(1);
		resolver.setViewNames("*.ftl");
		resolver.setCache(false);
		resolver.setViewClass(org.springframework.web.servlet.view.freemarker.FreeMarkerView.class);
		resolver.setExposeSpringMacroHelpers(true);
		resolver.setExposeRequestAttributes(true);
		resolver.setExposeSessionAttributes(true);
		resolver.setAllowRequestOverride(true);
		resolver.setAllowSessionOverride(true);
		//resolver.setRequestContextAttribute("request");
		resolver.setRequestContextAttribute("re");
		resolver.setSuffix(".ftl");
		resolver.setContentType("text/html; charset=UTF-8");
		return resolver;
	}

	@Bean

	public InternalResourceViewResolver jspViewResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver() {
			ThreadLocal<String> viewNameLocal = new ThreadLocal<>();

			@Override
			protected AbstractUrlBasedView buildView(String viewName) throws Exception {
				try {
					viewNameLocal.set(viewName);
					return super.buildView(viewName);
				} finally {
					viewNameLocal.remove();
				}
			}

			@Override
			protected String getPrefix() {
				String prefix = super.getPrefix();
				String viewName = viewNameLocal.get();
				if (viewName != null) {
					if (viewName.startsWith(prefix)) {
						return "";
					}
					if (viewName.startsWith("jsp/") && prefix.endsWith("jsp/")) {
						return prefix.substring(0, prefix.length() - "jsp/".length());
					}
				}
				return prefix;
			}

			@Override
			protected String getSuffix() {
				String suffix = super.getSuffix();
				String viewName = viewNameLocal.get();
				if (viewName != null) {
					if (viewName.endsWith(suffix)) {
						return "";
					}
					if (viewName.endsWith(".html")) {
						return "";
					}
				}
				return suffix;
			}
		};
		resolver.setOrder(2);
		// resolver.setViewNames("*.jsp", "jsp/*");
		resolver.setViewClass(org.springframework.web.servlet.view.JstlView.class);
		//resolver.setContentType(MediaType.TEXT_HTML_VALUE);
		resolver.setContentType("text/html; charset=UTF-8");
		resolver.setPrefix("/WEB-INF/jsp/");
		resolver.setSuffix(".jsp");
		return resolver;
	}


	@Override
	public void addInterceptors(InterceptorRegistry registry) {
//		registry.addInterceptor(commonDataInterceptor());
		registry.addWebRequestInterceptor(entityManagerInViewInterceptor())
			.addPathPatterns("/**")
			.excludePathPatterns("/static/**", "/public/**", "/resources/**", "/plugin/**", "/images/**");
		registry.addInterceptor(langInterceptor());
		super.addInterceptors(registry);
	}

	@Bean
	OpenEntityManagerInViewInterceptor entityManagerInViewInterceptor() {
		return new OpenEntityManagerInViewInterceptor();
	}

	@Bean
	public LocaleChangeInterceptor langInterceptor() {
		LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
		interceptor.setParamName("lang");
		return interceptor;
	}


	@EnableConfigurationProperties(MultipartProperties.class)
	@Configuration
	static class MultipartConfig {

		private final MultipartProperties multipartProperties;

		public MultipartConfig(MultipartProperties multipartProperties) {
			this.multipartProperties = multipartProperties;
		}

		@Bean
		public MultipartConfigElement multipartConfigElement() {
			MultipartConfigElement multipartConfig = this.multipartProperties.createMultipartConfig();
			return multipartConfig;
		}

		@Bean(DispatcherServlet.MULTIPART_RESOLVER_BEAN_NAME) // "multipartResolver"
		public CommonsMultipartResolver multipartResolver(MultipartConfigElement multipartConfig) throws IOException {
			CommonsMultipartResolver resolver = new CommonsMultipartResolver();
			resolver.setResolveLazily(this.multipartProperties.isResolveLazily());
			resolver.setMaxUploadSize(multipartConfig.getMaxRequestSize());
			resolver.setMaxUploadSizePerFile(multipartConfig.getMaxFileSize());
			resolver.setMaxInMemorySize(multipartConfig.getFileSizeThreshold());
			File file = new File(multipartConfig.getLocation());
			file.mkdirs();
			resolver.setUploadTempDir(new FileSystemResource(file));
			return resolver;
		}
	}

}
