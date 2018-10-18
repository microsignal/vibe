package io.microvibe.booster.core.api.support;

import io.microvibe.booster.commons.utils.StringUtils;
import io.microvibe.booster.core.api.ApiService;
import io.microvibe.booster.core.api.annotation.ApiAuthz;
import io.microvibe.booster.core.api.annotation.ApiIgnored;
import io.microvibe.booster.core.api.annotation.ApiName;
import io.microvibe.booster.core.api.annotation.SessionAuthcRequired;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author Qt
 * @since Aug 28, 2018
 */
@Component
public class ApiServiceRegistry implements InitializingBean {
	@Autowired
	ApplicationContext context;

	private Map<String, ApiServiceWrapper> apiServices = Collections.synchronizedMap(new LinkedHashMap<>());

	public ApiServiceRegistry() {
		super();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Map<String, ApiService> beans = context.getBeansOfType(ApiService.class);
		Set<Entry<String, ApiService>> entrySet = beans.entrySet();
		for (Entry<String, ApiService> entry : entrySet) {
			String beanName = entry.getKey();
			ApiService apiService = entry.getValue();
			register(apiService, fetchTxnCode(beanName));
		}
	}

	public ApiServiceWrapper register(ApiService apiService) {
		return register(apiService, null);
	}

	private String fetchTxnCode(String beanName) {
		return beanName.replaceFirst("^txn", "").replaceFirst("ApiService$", "");
	}

	private String fetchTxnCode(ApiService apiService) {
		Class<?> targetClass = AopUtils.getTargetClass(apiService);
		String[] beanNames = context.getBeanNamesForType(targetClass);
		if (beanNames != null && beanNames.length > 0) {
			for (String beanName : beanNames) {
				Object bean = context.getBean(beanName);
				if (bean == apiService) {
					return fetchTxnCode(beanName);
				}
			}
		}
		return null;
	}

	public ApiServiceWrapper register(ApiService apiService, String defaultTxnCode) {
		Class<? extends ApiService> clazz = apiService.getClass();
		ApiIgnored apiIgnored = AnnotationUtils.findAnnotation(clazz, ApiIgnored.class);
		if (apiIgnored != null && apiIgnored.value()) {
			return null;
		}

		SessionAuthcRequired sessionAuthcRequired = AnnotationUtils.findAnnotation(clazz, SessionAuthcRequired.class);
		boolean isSessionAuthcRequired = sessionAuthcRequired != null && sessionAuthcRequired.value();

		ApiAuthz apiAuthz = AnnotationUtils.findAnnotation(clazz, ApiAuthz.class);
		boolean isAuthzRequired = apiAuthz != null && apiAuthz.value();
		String authzPermission = apiAuthz == null ? ApiAuthz.ANY : apiAuthz.permission().trim();

		ApiServiceWrapperImpl wrapper = new ApiServiceWrapperImpl();
		wrapper.setApiService(apiService);
		wrapper.setSessionAuthcRequired(isSessionAuthcRequired);
		wrapper.setAuthzRequired(isAuthzRequired);
		wrapper.setAuthzPermission(authzPermission);

		// detault txnCode
		defaultTxnCode = StringUtils.trimToNull(defaultTxnCode);
		if (defaultTxnCode == null) {
			defaultTxnCode = fetchTxnCode(apiService);
		}
		if (defaultTxnCode != null) {
			register(wrapper, defaultTxnCode);
		}
		ApiName apiName = AnnotationUtils.findAnnotation(clazz, ApiName.class);
		if (apiName != null) {
			String[] txnCodes = apiName.value();
			register(wrapper, txnCodes);
		}
		register(wrapper, AopUtils.getTargetClass(apiService).getName());

		return wrapper;
	}

	private void register(ApiServiceWrapper wrapper, String... txnCodes) {
		for (String txnCode : txnCodes) {
			apiServices.put(txnCode, wrapper);
		}
	}

	public ApiServiceWrapper getApiService(String txnCode) {
		ApiServiceWrapper wrapper = apiServices.get(txnCode);
        /*if (wrapper == null) {
            try {
                String beanId = "txn" + txnCode + "ApiService";
                ApiService apiService = ApplicationContextHolder.getBean(beanId, ApiService.class);
                wrapper = register(apiService, txnCode);
            } catch (BeansException e) {
            }
        }*/
		return wrapper;
	}

	private static class ApiServiceWrapperImpl implements ApiServiceWrapper {

		private ApiService apiService;
		private boolean sessionAuthcRequired = false;
		private boolean authzRequired = false;
		private String authzPermission = ApiAuthz.ANY;

		@Override
		public boolean isSessionAuthcRequired() {
			return sessionAuthcRequired;
		}

		public void setSessionAuthcRequired(boolean sessionAuthcRequired) {
			this.sessionAuthcRequired = sessionAuthcRequired;
		}

		@Override
		public ApiService getApiService() {
			return apiService;
		}

		public void setApiService(ApiService apiService) {
			this.apiService = apiService;
		}

		@Override
		public boolean isAuthzRequired() {
			return authzRequired;
		}

		public void setAuthzRequired(boolean authzRequired) {
			this.authzRequired = authzRequired;
		}

		@Override
		public String getAuthzPermission() {
			return authzPermission;
		}

		public void setAuthzPermission(String authzPermission) {
			this.authzPermission = authzPermission;
		}

	}
}
