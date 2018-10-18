package io.microvibe.booster.core.api.txn;

import io.microvibe.booster.commons.spring.ApplicationContextHolder;
import io.microvibe.booster.core.api.ApiService;
import io.microvibe.booster.core.api.support.ApiServiceRegistry;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;

/**
 * 健康测试接口
 *
 * @author Qt
 * @since Oct 09, 2017
 */
public abstract class BaseApiService implements ApiService, InitializingBean {

	private boolean initialized = false;

	public BaseApiService() {
	}

	@PostConstruct
	private void init() {
		if (!initialized) {
			ApplicationContext context = ApplicationContextHolder.getApplicationContext();
			ApiServiceRegistry registry = context.getBean(ApiServiceRegistry.class);
			registry.register(this);
			initialized = true;
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		init();
	}

}
