package io.microvibe.booster.core.base.controller.scanner;

import io.microvibe.booster.core.base.mybatis.annotation.AfterMybatisScanner;
import io.microvibe.booster.core.base.mybatis.configuration.PersistentEnhancerScanner;
import io.microvibe.booster.core.base.resource.ResourceScanner;
import io.microvibe.booster.core.env.SystemEnv;
import lombok.Data;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.*;

@Component
@Slf4j
@AfterMybatisScanner
public class ControllerRequestMappingScanner implements ApplicationListener<ContextRefreshedEvent> {

	private boolean executeOnLoad;

	public boolean isExecuteOnLoad() {
		return executeOnLoad;
	}

	public void setExecuteOnLoad(boolean executeOnLoad) {
		this.executeOnLoad = executeOnLoad;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (!(event instanceof ContextRefreshedEvent)) {
			return;
		}
		// root application context 没有parent.
		if (((ContextRefreshedEvent) event).getApplicationContext().getParent() != null) {
			return;
		}
		/*if(!event.getApplicationContext().getDisplayName().equals("Root WebApplicationContext")){
			return;
		}*/

		// scanning controller methods
		try {
			ApplicationContext context = ((ContextRefreshedEvent) event).getApplicationContext();

			SystemEnv systemEnv = context.getBean(SystemEnv.class);
			if(systemEnv.isResourcesAutoScan()){
				ResourceScanner.scan(context);
				// scan(context);
			}
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
		}
	}

	private void scan(ApplicationContext context) {
		RequestMappingHandlerMapping requestMappingHandlerMapping = context.getBean("requestMappingHandlerMapping",RequestMappingHandlerMapping.class);
		if (requestMappingHandlerMapping != null) {
			scan(requestMappingHandlerMapping);
		}
	}

	public void scan(RequestMappingHandlerMapping requestMappingHandlerMapping) {
		List<ResourceMapping> list = new ArrayList<>();
		//list.add(new String[]{"序号", "URL路径", "请求方法", "类名", "方法名"});
		Map<RequestMappingInfo, HandlerMethod> handlerMethodMap = requestMappingHandlerMapping.getHandlerMethods();

		final int[] seq = new int[]{0};
		handlerMethodMap.entrySet().forEach(entry -> {
			RequestMappingInfo key = entry.getKey();
			Set<String> patterns = key.getPatternsCondition().getPatterns();
			Set<RequestMethod> methods = key.getMethodsCondition().getMethods();
			ResourceMapping resourceMapping = new ResourceMapping();
			resourceMapping.setHttpUrls(patterns);
			resourceMapping.setHttpMethods(methods);
			HandlerMethod val = entry.getValue();
			resourceMapping.setControllerType(val.getBeanType());
			resourceMapping.setControllerMethod(val.getMethod());;
			list.add(resourceMapping);
		});
	}

	@Data
	public static class ResourceMapping{
		private Set<String> httpUrls = new LinkedHashSet<>();;
		private Set<RequestMethod> httpMethods = new LinkedHashSet<>();
		private Class<?> controllerType;
		private Method controllerMethod;
	}
}
