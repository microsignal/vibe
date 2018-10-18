package io.microvibe.booster.core.accessor;

import io.microvibe.booster.commons.spring.ApplicationContextHolder;
import io.microvibe.booster.core.base.conversion.NumberToDateConverter;
import io.microvibe.booster.core.base.conversion.NumberToTimestampConverter;
import io.microvibe.booster.core.base.conversion.StringToDateConverter;
import io.microvibe.booster.core.base.conversion.StringToTimestampConverter;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConversionServiceAccessor {

	static ConversionService conversionService;

	public static ConversionService getConversionService() {
		if (conversionService == null) {
			synchronized (ConversionServiceAccessor.class) {
				if (conversionService == null) {
					ApplicationContext context = ApplicationContextHolder.getApplicationContext();
					if (context != null) {
						try {
							conversionService = context.getBean(ConversionService.class);
						} catch (Exception e) {
							Map<String, ConversionService> beans = context.getBeansOfType(ConversionService.class);
							if (!beans.isEmpty()) {
								conversionService = beans.entrySet().iterator().next().getValue();
							}
						}
					}
				}
				if (conversionService == null) {
					FormattingConversionServiceFactoryBean factory = new FormattingConversionServiceFactoryBean();
					Set<Object> converters = new HashSet<>();
					converters.add(new NumberToDateConverter());
					converters.add(new NumberToTimestampConverter());
					converters.add(new StringToDateConverter());
					converters.add(new StringToTimestampConverter());
					factory.setConverters(converters);
					factory.afterPropertiesSet();
					FormattingConversionService conv = factory.getObject();
					conversionService = conv;
				}
			}
		}
		return conversionService;
	}
}
