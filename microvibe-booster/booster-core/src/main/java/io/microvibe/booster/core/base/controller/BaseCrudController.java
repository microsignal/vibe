package io.microvibe.booster.core.base.controller;

import io.microvibe.booster.core.api.model.BodyModel;
import io.microvibe.booster.core.api.model.RequestData;
import io.microvibe.booster.core.api.model.ResponseData;
import io.microvibe.booster.core.api.model.SearchModel;
import io.microvibe.booster.core.api.tools.DataKit;
import io.microvibe.booster.core.base.controller.annotation.DataParam;
import io.microvibe.booster.core.base.controller.annotation.DataValid;
import io.microvibe.booster.core.base.entity.BaseEntity;
import io.microvibe.booster.core.base.entity.DeletedRecordable;
import io.microvibe.booster.core.base.resource.annotation.ResourceIdentity;
import io.microvibe.booster.core.base.annotation.BaseComponent;
import io.microvibe.booster.core.base.utils.InjectBaseDependencyHelper;
import io.microvibe.booster.core.search.SearchOper;
import io.microvibe.booster.core.validation.validator.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;

/**
 * @author Qt
 * @since Jun 04, 2018
 */
@Slf4j
public abstract class BaseCrudController<M extends BaseEntity<String>> extends BaseController<M, String> {

	protected String viewPrefix = "";

	@PostConstruct
	private void init() throws Exception {
		final Set<Object> candidates = InjectBaseDependencyHelper.findDependencies(this, BaseComponent.class);
		for (Object o : candidates) {
		}
		RequestMapping requestMapping = AnnotationUtils.findAnnotation(getClass(), RequestMapping.class);
		if (requestMapping != null) {
			String[] path = requestMapping.value();
			if (path != null && path.length > 0) {
				this.viewPrefix = path[0].replaceFirst("/$", "");
			}
		}
	}

	protected String viewPrefix() {
		return viewPrefix;
	}

	protected String viewName(String name) {
		if (name == null || (name = name.trim()).length() == 0) {
			return viewPrefix();
		} else if (name.startsWith("/")) {
			return viewPrefix() + name;
		} else {
			return viewPrefix() + "/" + name;
		}
	}

	/*
	 *  列表查看
	 * @param requestData
	 * @return
	 */
	@DataValid(key = "limit.pn", value = NotEmpty.class)
	@DataValid(key = "limit.ps", value = NotEmpty.class)
	@ResponseBody
	@GetMapping("list")
//	@ResourceIdentity("list")
	@Description("列表查看")
	public ResponseData list(@DataParam RequestData requestData) {
		if (DeletedRecordable.class.isAssignableFrom(entityClass)) {
			List<SearchModel> list = requestData.getBody().getSearches("deleted");
			if (list.size() == 0) {
				requestData.getBody().addSearch("deleted", SearchOper.eq, false);
			}
		}
		Page<M> page = mybatisBaseService.findByPage(requestData);
		return DataKit.buildResponse(page);
	}

	/**
	 * 查询一条
	 *
	 * @param id
	 * @return
	 */
	@GetMapping("/{id}")
	@ResponseBody
	@ResourceIdentity("view")
	@Description("详情查看")
	public ResponseData view(@PathVariable("id") String id) {
		M entity = mybatisBaseService.getById(id);
		return DataKit.buildResponse("entity", entity);
	}

	@PostMapping
	@ResponseBody
	@ResourceIdentity("create")
	@Description("新增")
	public ResponseData create(@DataParam RequestData requestData) {
		BodyModel body = requestData.getBody();
		M entity = body.getObject("entity", entityClass);
		mybatisBaseService.insertSelective(entity);
		return DataKit.buildResponse("entityId", entity.getId());
	}

	@PutMapping
	@ResponseBody
	@ResourceIdentity("update")
	@Description("修改")
	public ResponseData update(@DataParam RequestData requestData) {
		BodyModel body = requestData.getBody();
		M entity = body.getObject("entity", entityClass);
		mybatisBaseService.updateSelective(entity);
		return DataKit.buildResponse("entityId", entity.getId());
	}

	/**
	 * 查询一条
	 *
	 * @param id
	 * @return
	 */
	@DeleteMapping("/{id}")
	@ResponseBody
	@ResourceIdentity("delete")
	@Description("删除")
	public ResponseData delete(@PathVariable("id") String id) {
		long row = mybatisBaseService.delete(id);
		return DataKit.buildResponse(new String[]{"row"}
			, new Object[]{row});
	}

	@DeleteMapping
	@ResponseBody
	@ResourceIdentity("delete")
	@Description("删除")
	public ResponseData delete(@DataParam RequestData requestData, @RequestParam(value = "ids", required = false) String[] ids) {
		if (ids == null) {
			ids = requestData.getBody("ids", String[].class);
		}
		if (ids == null) {
			ids = new String[0];
		}
		long[] rs = mybatisBaseService.delete(ids);
		int fail = 0;
		for (int i = 0; i < ids.length; i++) {
			if (rs[i] != 1) {
				fail++;
			}
		}
		return DataKit.buildResponse(new String[]{"total", "fail"}
			, new Object[]{rs.length, fail});
	}

}
