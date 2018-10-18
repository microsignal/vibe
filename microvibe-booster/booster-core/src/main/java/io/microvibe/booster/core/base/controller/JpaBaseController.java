package io.microvibe.booster.core.base.controller;

import io.microvibe.booster.commons.utils.ReflectionUtils;
import io.microvibe.booster.commons.utils.StringUtils;
import io.microvibe.booster.commons.utils.property.PropertyUtil;
import io.microvibe.booster.core.base.entity.BaseEntity;
import io.microvibe.booster.core.base.utils.InjectBaseDependencyHelper;
import io.microvibe.booster.core.base.service.JpaBaseService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 基础控制器
 *
 * @author Qt
 */
@Deprecated
public abstract class JpaBaseController<M extends BaseEntity<ID>, ID extends Serializable>
	implements InitializingBean {

	public static final String BEAR_BABY_CARD_BLUE_BACKGROUND_IMAGE = "http://ombs0u763.bkt.clouddn.com/bear_baby_card_blue.jpg";
	public static final String BEAR_BABY_CARD_RED_BACKGROUND_IMAGE = "http://ombs0u763.bkt.clouddn.com/bear_baby_card_red.jpg";
	public static final String SERVICE_CARD_BACKGROUND_IMAGE = "http://ody2utsvz.bkt.clouddn.com/cardbg.jpg";
	public static final String QN_URL_PRE = "http://ody2utsvz.bkt.clouddn.com/";
	public static final String QN_HEAD_SUFFIX = "-";
	public static String front = "";
	protected final Class<M> entityClass;
	protected JpaBaseService<M, ID> jpaBaseService;

	private String viewPrefix;

	@SuppressWarnings("unchecked")
	protected JpaBaseController() {
		this.entityClass = (Class<M>) ReflectionUtils.firstParameterizedType(getClass());
		setViewPrefix(defaultViewPrefix());
	}

	/**
	 * 合并数据把 source 的数据 合并到 target上 要求字段名一致，类型一致
	 *
	 * @param targetBean
	 * @param sourceBean
	 * @return
	 */
	public static Object mergeEntity(Object targetBean, Object sourceBean) {
		return PropertyUtil.merge(targetBean, sourceBean);
	}

	/**
	 * 传入List 返回list ids 1,2,3,4
	 *
	 * @param list
	 * @param <E>
	 * @return
	 */
	public static <E extends BaseEntity> List<Object> getIds(List<E> list, String property) {

		ArrayList<Object> arrayList = new ArrayList<>();
		for (E elem : list) {
			try {
				Method mtg = elem.getClass().getMethod("get" + StringUtils.upperFirst(property));
				Object propertyValue = mtg.invoke(elem);
				if (propertyValue != null) {
					arrayList.add(mtg.invoke(elem));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return arrayList;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (this.entityClass != null) {
			jpaBaseService = InjectBaseDependencyHelper.findBaseComponent(this, JpaBaseService.class);
		}
	}

	private String defaultViewPrefix() {
		String currentViewPrefix = "";
		RequestMapping requestMapping = AnnotationUtils.findAnnotation(getClass(), RequestMapping.class);
		if (requestMapping != null && requestMapping.value().length > 0) {
			currentViewPrefix = requestMapping.value()[0];
		}

		if (StringUtils.isEmpty(currentViewPrefix)) {
			currentViewPrefix = this.entityClass.getSimpleName();
		}

		return currentViewPrefix;
	}

	public JpaBaseService<M, ID> getJpaBaseService() {
		return jpaBaseService;
	}

	public void setJpaBaseService(JpaBaseService<M, ID> jpaBaseService) {
		this.jpaBaseService = jpaBaseService;
	}

	/**
	 * 设置通用数据
	 *
	 * @param model
	 */
	protected void setCommonData(Model model) {
	}

	public String getViewPrefix() {
		return viewPrefix;
	}

	/**
	 * 当前模块 视图的前缀 默认 1、获取当前类头上的@RequestMapping中的value作为前缀 2、如果没有就使用当前模型小写的简单类名
	 */
	public void setViewPrefix(String viewPrefix) {
		if (viewPrefix.startsWith("/")) {
			viewPrefix = viewPrefix.substring(1);
		}
		this.viewPrefix = viewPrefix;
	}

	protected M newModel() {
		try {
			return entityClass.newInstance();
		} catch (Exception e) {
			throw new IllegalStateException("can not instantiated viewmodel : " + this.entityClass, e);
		}
	}

	/**
	 * 获取视图名称：即prefixViewName + "/" + suffixName
	 *
	 * @return
	 */
	public String viewName(String suffixName) {
		if (!suffixName.startsWith("/")) {
			suffixName = "/" + suffixName;
		}
		return getViewPrefix() + suffixName;
	}

	/**
	 * 共享的验证规则 验证失败返回true
	 *
	 * @param m
	 * @param result
	 * @return
	 */
	protected boolean hasError(M m, BindingResult result) {
		Assert.notNull(m);
		return result.hasErrors();
	}

	/**
	 * @param backURL null 将重定向到默认getViewPrefix()
	 * @return
	 */
	protected String redirectToUrl(String backURL) {
		if (StringUtils.isEmpty(backURL)) {
			backURL = getViewPrefix();
		}
		if (!backURL.startsWith("/") && !backURL.startsWith("http")) {
			backURL = "/" + backURL;
		}
		return "redirect:" + backURL;
	}
}
