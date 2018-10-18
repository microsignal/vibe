package io.microvibe.booster.config.aspect;

import io.microvibe.booster.commons.spring.AfterApplicationContextHolder;
import io.microvibe.booster.core.api.ApiException;
import io.microvibe.booster.core.api.ReplyCode;
import com.baidu.unbiz.fluentvalidator.DefaultValidateCallback;
import com.baidu.unbiz.fluentvalidator.ValidateCallback;
import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.Validator;
import com.baidu.unbiz.fluentvalidator.interceptor.FluentValidateInterceptor;
import com.baidu.unbiz.fluentvalidator.registry.Registry;
import com.baidu.unbiz.fluentvalidator.registry.impl.SpringApplicationContextRegistry;
import com.baidu.unbiz.fluentvalidator.validator.element.ValidatorElementList;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ValidationConfig {
	@Autowired
	ApplicationContext context;

	@Bean
	public Registry fluentValidatorRegistry() {
		SpringApplicationContextRegistry registry = new SpringApplicationContextRegistry();
		registry.setApplicationContext(context);
		return registry;
	}

	@Bean
	public FluentValidateInterceptor fluentValidateInterceptor() {
		FluentValidateInterceptor bean = new FluentValidateInterceptor();
		bean.setApplicationContext(context);
		bean.setCallback(validateCallback());
		return bean;
	}

	@Bean
	public BeanNameAutoProxyCreator fluentValidateInterceptorAutoProxy() {
		BeanNameAutoProxyCreator bean = new BeanNameAutoProxyCreator();
		bean.setProxyTargetClass(true);
		bean.setBeanNames("*Service", "*ServiceImpl", "*Controller", "*Ctrler");
		bean.setInterceptorNames("fluentValidateInterceptor");
		return bean;
	}

	private ValidateCallback validateCallback() {
		return new DefaultValidateCallback() {
			@Override
			public void onFail(ValidatorElementList validatorElementList, List<ValidationError> errors) {
				if (errors.size() > 0) {
					ValidationError validationError = errors.get(0);
					String field = validationError.getField();
					String errorMsg = validationError.getErrorMsg();
					StringBuilder sb = new StringBuilder();
					if (field != null) {
						sb.append("字段 [ ").append(field).append(" ] 有误.");
					}
					sb.append(errorMsg);
					throw new ApiException(ReplyCode.RequestParamError, sb.toString());
				}
			}

			@Override
			public void onUncaughtException(Validator validator, Exception e, Object target) throws Exception {
				throw new ApiException(e);
			}
		};
	}
}
