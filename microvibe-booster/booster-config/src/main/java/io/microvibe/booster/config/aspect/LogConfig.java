package io.microvibe.booster.config.aspect;

import io.microvibe.booster.commons.spring.AfterApplicationContextHolder;
import io.microvibe.booster.commons.utils.StringUtils;
import io.microvibe.booster.core.env.SystemEnv;
import io.microvibe.booster.core.log.IMethodTraceService;
import io.microvibe.booster.core.log.NoopMethodTraceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SuppressWarnings("ALL")
@Slf4j
public class LogConfig {

	@Autowired
	SystemEnv systemEnv;

	@Bean
	@ConditionalOnMissingBean(IMethodTraceService.class)
	public IMethodTraceService methodTraceService() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		String methodLogService = systemEnv.getMethodLogService();
		IMethodTraceService bean = null;
		if (StringUtils.isNotBlank(methodLogService)) {
			try {
				bean = (IMethodTraceService) Class.forName(methodLogService.trim()).newInstance();
			} catch (Exception e) {
				log.error("无法初始化: {}", methodLogService);
			}
		}
		if (bean == null) {
			bean = new NoopMethodTraceService();
		}
		return bean;
	}
}
