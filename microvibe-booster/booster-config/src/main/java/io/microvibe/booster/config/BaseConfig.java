package io.microvibe.booster.config;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import io.microvibe.booster.commons.spring.ApplicationContextHolder;
import io.microvibe.booster.commons.spring.MessageResources;
import io.microvibe.booster.core.base.conversion.NumberToDateConverter;
import io.microvibe.booster.core.base.conversion.NumberToTimestampConverter;
import io.microvibe.booster.core.base.conversion.StringToDateConverter;
import io.microvibe.booster.core.base.conversion.StringToTimestampConverter;
import org.hibernate.validator.HibernateValidator;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassRelativeResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.data.repository.support.DomainClassConverter;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.Validator;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Qt
 * @since Aug 12, 2018
 */
@Configuration
public class BaseConfig {
	@Autowired
	ApplicationContext context;
	@Autowired
	Environment env;

	@ConditionalOnMissingBean
	@Bean(ApplicationContextHolder.BEAN_ID)
	public ApplicationContextHolder applicationContextHolder(){
		return new ApplicationContextHolder();
	}

	private String[] fetchBasenames(String messageResourceBase) {
		Set<String> basenames = new HashSet<String>();

		String msgResPath = messageResourceBase.replaceFirst("^[/\\.]?", "/")
			.replaceFirst("[/\\.]?$", "/").replaceAll("\\.", "/");
		String resPath = "classpath*:" + msgResPath + "*";
		try {
			ResourcePatternResolver resolver = ResourcePatternUtils
				.getResourcePatternResolver(new ClassRelativeResourceLoader(BaseConfig.class));
			Pattern[] patterns = new Pattern[]{
				Pattern.compile(msgResPath + "([\\w\\-]+)(_[\\w\\-]+)(_[\\w\\-]+)\\.(properties|class)$"),
				Pattern.compile(msgResPath + "([\\w\\-]+)(_[\\w\\-]+)\\.(properties|class)$"),
				Pattern.compile(msgResPath + "([\\w\\-]+)\\.(properties|class)$")};
			Resource[] resources = resolver.getResources(resPath);
			for (Resource resource : resources) {
				String path = resource.getURI().getPath();
				if (path == null) {
					path = resource.getURI().toString();
				}
				String basename = null;
				for (Pattern pattern : patterns) {
					if (basename != null) {
						break;
					}
					Matcher matcher = pattern.matcher(path);
					if (matcher.find()) {
						basename = matcher.group(1);
					}
				}
				if (basename != null) {
					basenames.add("classpath:" + msgResPath + basename);// ReloadableResourceBundleMessageSource 使用'/'
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return basenames.toArray(new String[basenames.size()]);
	}


	@Bean
	public MessageSource messageSource() {
		// TODO database MessageSource
		ReloadableResourceBundleMessageSource bean = new ReloadableResourceBundleMessageSource();
		bean.addBasenames("classpath:message", "classpath:ValidationMessages");
		for (String s : MessageResources.DEFAULT_MESSAGE_RESOURCE_BASES.split("[,;\\s]+")) {
			bean.addBasenames(fetchBasenames(s));
		}
		bean.setUseCodeAsDefaultMessage(true);
		bean.setDefaultEncoding("UTF-8");
		bean.setCacheSeconds(60);
		return bean;
	}


	@Bean
	public Validator validator() {
		LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
		factory.setProviderClass(HibernateValidator.class);
		factory.setValidationMessageSource(messageSource());
		return factory;
	}

	@Bean
	public ParserConfig fastjsonParserConfig() {
		com.alibaba.fastjson.parser.ParserConfig globalInstance = ParserConfig.getGlobalInstance();
		globalInstance.setAsmEnable(false);
		return globalInstance;
	}

	@Bean
	public SerializeConfig fastjsonSerializeConfig() {
		SerializeConfig globalInstance = SerializeConfig.getGlobalInstance();
		globalInstance.setAsmEnable(false);
		return globalInstance;
	}

	// 类型转换及数据格式化
	@Bean("conversionService")
	public FactoryBean<FormattingConversionService> conversionService() {
		FormattingConversionServiceFactoryBean factory = new FormattingConversionServiceFactoryBean();
		Set<Object> converters = new HashSet<>();
		converters.add(new NumberToDateConverter());
		converters.add(new NumberToTimestampConverter());
		converters.add(new StringToDateConverter());
		converters.add(new StringToTimestampConverter());
		factory.setConverters(converters);
		return factory;
	}

	// 直接把id转换为entity 必须非lazy否则无法注册
	@Bean("domainClassConverter")
	public DomainClassConverter<FormattingConversionService> domainClassConverter() throws Exception {
		return new DomainClassConverter<FormattingConversionService>(conversionService().getObject());
	}

}
