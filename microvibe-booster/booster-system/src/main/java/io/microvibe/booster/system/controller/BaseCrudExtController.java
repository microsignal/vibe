package io.microvibe.booster.system.controller;

import com.alibaba.fastjson.JSONObject;
import io.microvibe.booster.commons.utils.StringUtils;
import io.microvibe.booster.core.api.model.RequestData;
import io.microvibe.booster.core.api.model.ResponseData;
import io.microvibe.booster.core.api.model.SearchModel;
import io.microvibe.booster.core.api.tools.DataKit;
import io.microvibe.booster.core.base.controller.BaseCrudController;
import io.microvibe.booster.core.base.controller.OpType;
import io.microvibe.booster.core.base.controller.annotation.DataParam;
import io.microvibe.booster.core.base.controller.annotation.DataValid;
import io.microvibe.booster.core.base.entity.BaseEntity;
import io.microvibe.booster.core.base.entity.DeletedRecordable;
import io.microvibe.booster.core.base.entity.Entryable;
import io.microvibe.booster.core.base.persistence.EntityMetaData;
import io.microvibe.booster.core.base.persistence.FieldMetaData;
import io.microvibe.booster.core.base.persistence.PersistentRecognizer;
import io.microvibe.booster.core.base.resource.annotation.ResourceIdentity;
import io.microvibe.booster.core.search.SearchOper;
import io.microvibe.booster.core.validation.validator.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Description;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public abstract class BaseCrudExtController<M extends BaseEntity<String>> extends BaseCrudController<M> {

	protected Set<String> entryNames = new LinkedHashSet<>();

	@PostConstruct
	private void afterPropertiesSet() throws Exception {
		/*final Set<Object> candidates = InjectBaseDependencyHelper.findDependencies(this, BaseComponent.class);
		for (Object o : candidates) {
			if (o instanceof SysBaseService) {
				this.sysBaseService = (SysBaseService<M, String>) o;
				break;
			}
		}*/
		EntityMetaData entityMetaData = PersistentRecognizer.entityMetaData(entityClass);
		List<FieldMetaData> allColumnFields = entityMetaData.getAllColumnFields();
		for (FieldMetaData field : allColumnFields) {
			if (field.isEnumerated()) {
				Class<?> javaType = field.getJavaType();
				String entryName = Entryable.entryName(javaType);
				if (entryName != null) {
					entryNames.add(entryName);
				}
			}
		}
	}

	protected void addExtAttributes(Model model) {
		if (entryNames.size() > 0) {
			JSONObject entries = new JSONObject(true);
			for (String entryName : entryNames) {
				Map<String, String> map = Entryable.entries(entryName);
				if (map != null) {
					entries.put(entryName, map);
				}
			}
			model.addAttribute("entries", entries);
		}
	}

	/**
	 * 进入主页面
	 */
	@GetMapping
	public String index() {
		return viewName("/list");
	}


	/**
	 *  列表查看
	 * @param requestData
	 * @return
	 */
	@Override
	@DataValid(key = "limit.pn", value = NotEmpty.class)
	@DataValid(key = "limit.ps", value = NotEmpty.class)
	@ResponseBody
	@GetMapping("list")
///	@ResourceIdentity("list")
	@Description("列表查看")
	public ResponseData list(@DataParam RequestData requestData) {
		if (DeletedRecordable.class.isAssignableFrom(entityClass)) {
			List<SearchModel> list = requestData.getBody().getSearches("deleted");
			if (list.size() == 0) {
				requestData.getBody().addSearch("deleted", SearchOper.eq, false);
			}
		}
		Page<M> page = mybatisBaseService.findByPage(requestData);
		//处理枚举映射
		Page<JSONObject> newPage = page.map(m -> {
			JSONObject json = (JSONObject) JSONObject.toJSON(m);
			PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(m.getClass());
			for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
				Class<?> propertyType = propertyDescriptor.getPropertyType();
				if (Entryable.class.isAssignableFrom(propertyType)) {
					try {
						Method readMethod = propertyDescriptor.getReadMethod();
						readMethod.setAccessible(true);
						Entryable entryable = (Entryable) readMethod.invoke(m);
						json.put(propertyDescriptor.getName() + "Label", entryable.value());
					} catch (Exception e) {
					}
				}
			}
			return json;
		});
		return DataKit.buildResponse(newPage);
	}

	/**
	 * 查询一条
	 *
	 * @param id
	 * @return
	 */
	@GetMapping("view/{id}")
	@ResourceIdentity("view")
	@Description("详情查看")
	public String toView(Model model, @PathVariable("id") String id) {
		M entity = mybatisBaseService.getById(id);
		model.addAttribute("id", id);
		model.addAttribute("m", entity);
		model.addAttribute(OpType.KEY, OpType.VIEW);
		addExtAttributes(model);
		return viewName("/edit");
	}

	/**
	 * 跳转新增页面
	 *
	 * @param model
	 * @return
	 */
	@GetMapping("create")
	@ResourceIdentity("create")
	@Description("新增")
	public String toCreate(Model model) {
		model.addAttribute(OpType.KEY, OpType.CREATE);
		addExtAttributes(model);
		return viewName("/edit");
	}

	@Override
	@ResponseBody
	@PostMapping("create")
	@ResourceIdentity("create")
	@Description("新增")
	public ResponseData create(@DataParam RequestData requestData) {
		return super.create(requestData);
	}

	/**
	 * 跳转修改页面
	 *
	 * @param model
	 * @param id
	 * @return
	 */
	@GetMapping("update/{id}")
	@ResourceIdentity("update")
	@Description("修改")
	public String toUpdate(Model model, @PathVariable("id") String id) {
		M entity = mybatisBaseService.getById(id);
		model.addAttribute("id", id);
		model.addAttribute("m", entity);
		model.addAttribute(OpType.KEY, OpType.UPDATE);
		addExtAttributes(model);
		return viewName("/edit");
	}

	@Override
	@ResponseBody
	@PutMapping("update")
	@ResourceIdentity("update")
	@Description("修改")
	public ResponseData update(@DataParam RequestData requestData) {
		return super.update(requestData);
	}

	/**
	 * 判断关键词是否重复
	 *
	 * @return Boolean
	 **/
	@GetMapping("/available")
	@ResponseBody
//	@ResourceIdentity("available")
	@Description("关键词可用值判断")
	public Boolean available(@DataParam RequestData requestData) {
		boolean exists = mybatisBaseService.exists(DataKit.toSearchParam(requestData));
		if (exists) {// 存在则不可用
			return false;
		}
		return true;
	}

}
