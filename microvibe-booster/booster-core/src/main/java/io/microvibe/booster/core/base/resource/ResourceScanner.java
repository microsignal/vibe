package io.microvibe.booster.core.base.resource;


import io.microvibe.booster.core.base.resource.annotation.ResourceIdentity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Description;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 系统资源扫描器
 *
 * @author Qt
 * @since Jun 06, 2018
 */
@Slf4j
public class ResourceScanner {

	private static final AtomicBoolean scanning = new AtomicBoolean(false);

	// scanning controller methods
	public static void scan(ApplicationContext context) {
		// 须有 RequestMappingHandlerMapping
		try {
			context.getBean(RequestMappingHandlerMapping.class);
		} catch (BeansException e) {
			return;
		}
		if (!scanning.get()) {
			synchronized (ResourceScanner.class) {
				if (!scanning.get()) {
					try {
						scanning.set(true);
						log.info("开始扫描所有系统资源");
						Map<String, ScannedResource> resources = new HashMap<>();
						log.info("读取自定义基础资源");
						readResourceSupplier(context, resources);//读取自定义基础资源
						log.info("读取控制器类声明的资源");
						readControllerResource(context, resources);//读取控制器类声明的资源
						log.info("读取请求映射方法声明的资源");
						readMappingResource(context, resources);//读取请求映射方法声明的资源
						log.info("结构化资源信息");
						ScannedResource root = organize(resources); // 结构化资源信息
						log.info("保存信息至数据库");
						persist(context, root);// 保存信息至数据库
						log.info("扫描所有系统资源完成");
					} catch (BeansException e) {
						log.warn(e.getMessage(), e);
					} finally {
						scanning.set(false);
					}
				}
			}
		}
	}

	public static <T> Map<String, T> findBeansOfType(ApplicationContext context, Class<T> type) {
		Map<String, T> beans = new HashMap<>();
		while (context != null) {
			beans.putAll(context.getBeansOfType(type));
			context = context.getParent();
		}
		return beans;
	}

	public static Map<String, Object> findBeansWithAnnotation(ApplicationContext context, Class<? extends Annotation> annotationType) {
		Map<String, Object> beans = new HashMap<>();
		while (context != null) {
			beans.putAll(context.getBeansWithAnnotation(annotationType));
			context = context.getParent();
		}
		return beans;
	}

	private static void readResourceSupplier(ApplicationContext context, Map<String, ScannedResource> resources) {
		Map<String, ResourceSupplier> suppliers = findBeansOfType(context, ResourceSupplier.class);
		suppliers.entrySet().forEach(entry -> {
			ResourceSupplier supplier = entry.getValue();
			Collection<ScannedResource> reses = supplier.supply();
			if (reses != null) {
				reses.forEach(res -> {
					resources.put(res.identity(), res);
				});
			}
		});
	}

	private static void readControllerResource(ApplicationContext context, Map<String, ScannedResource> resources) {
		Map<String, Object> ctrllers = findBeansWithAnnotation(context, Controller.class);

		ctrllers.entrySet().forEach(entry -> {
			Object val = entry.getValue();
			Class<? extends Object> ctrller = val.getClass();
			Set<ResourceIdentity> resIdentities = AnnotationUtils.getRepeatableAnnotations(ctrller, ResourceIdentity.class);
			for (ResourceIdentity identity : resIdentities) {
				String name = identity.value();
				if (StringUtils.isNotBlank(identity.description())) {
					addScannedResource(resources, name, identity.parent(), "", identity.description());
				} else {
					Description description = AnnotationUtils.findAnnotation(ctrller, Description.class);
					String defaultDescription = description != null ? description.value() : "";
					addScannedResource(resources, name, identity.parent(), "", defaultDescription);
				}
			}
		});
	}

	private static void readMappingResource(ApplicationContext context, Map<String, ScannedResource> resources) {
		// Map<String, RequestMappingHandlerMapping> beans = findBeansOfType(context,RequestMappingHandlerMapping.class);*
		RequestMappingHandlerMapping requestMappingHandlerMapping = context.getBean(RequestMappingHandlerMapping.class);
		if (requestMappingHandlerMapping != null) {
			Map<RequestMappingInfo, HandlerMethod> handlerMethodMap = requestMappingHandlerMapping.getHandlerMethods();
			handlerMethodMap.entrySet().forEach(entry -> {
				RequestMappingInfo key = entry.getKey();
				Set<String> patterns = key.getPatternsCondition().getPatterns();
				Iterator<String> iter = patterns.iterator();
				String uri = "";
				if (iter.hasNext()) {
					uri = iter.next();
				}
				// Set<RequestMethod> methods = key.getMethodsCondition().getMethods();
				HandlerMethod val = entry.getValue();
				Method controllerMethod = val.getMethod();
				Set<ResourceIdentity> resIdentities = AnnotationUtils.getRepeatableAnnotations(controllerMethod, ResourceIdentity.class);
				for (ResourceIdentity identity : resIdentities) {
					String name = identity.value();
					String parent = identity.parent();
					if (identity.inherited() && StringUtils.isBlank(parent)) {
						Set<ResourceIdentity> classAnnos = AnnotationUtils.getRepeatableAnnotations(val.getBeanType(), ResourceIdentity.class);
						if (classAnnos.size() > 0) {
							ResourceIdentity parentIdentity = classAnnos.iterator().next();
							name = parentIdentity.value() + ResourceIdentity.SEPARATOR + name;
							parent = parentIdentity.value();
						} else {
							// 需要但未配置父资源时, 忽略
							continue;
						}
					}
					if (StringUtils.isNotBlank(identity.description())) {
						addScannedResource(resources, name, parent, uri, identity.description());
					} else {
						Description description = AnnotationUtils.findAnnotation(controllerMethod, Description.class);
						String defaultDescription = description != null ? description.value() : "";
						addScannedResource(resources, name, parent, uri, defaultDescription);
					}
				}
			});
		}
	}

	private static void addScannedResource(Map<String, ScannedResource> resources, String name, String parent, String uri, String description) {
		if (resources.containsKey(name)) {
			log.warn("注意: 已存在同名资源 [{}] ", name);
		}
		ScannedResource res = ScannedResource.create(name, parent, uri).description(description);
		resources.put(name, res);
	}

	private static ScannedResource organize(Map<String, ScannedResource> resources) {
		ScannedResource root = ScannedResource.create("").id(null);
		resources.entrySet().forEach(entry -> {
			ScannedResource res = entry.getValue();
			String parentIdentity = res.parentIdentity();
			ScannedResource parent = null;

			if (parentIdentity == null || parentIdentity.equals(root.identity())) {
				int i = res.identity().lastIndexOf(ResourceIdentity.SEPARATOR);
				if (i > 0) {
					String maymeParent = res.identity().substring(0, i);
					parent = resources.get(maymeParent);
				}
				if (parent == null) {
					parent = root;
				}
			} else {
				parent = resources.get(parentIdentity);
			}
			if (parent == null) {
				log.warn("注意: 上级资源 [{}] 不存在", parentIdentity);
			} else {
				parent.children().add(res);
				res.parent(parent);
			}
		});
		return root;
	}


	private static void persist(ApplicationContext context, ScannedResource root) throws BeansException {
		ResourcePersistService service = context.getBean(ResourcePersistService.class);
		service.doPersist(root);
	}
}
