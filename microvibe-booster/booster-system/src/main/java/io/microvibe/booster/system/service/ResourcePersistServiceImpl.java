package io.microvibe.booster.system.service;

import io.microvibe.booster.core.base.resource.ResourcePersistService;
import io.microvibe.booster.core.base.resource.ScannedResource;
import io.microvibe.booster.core.base.utils.EntityKit;
import io.microvibe.booster.system.entity.SysResource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResourcePersistServiceImpl implements ResourcePersistService {

	@Autowired
	private SysResourceService service;

	@Override
	public void doPersist(ScannedResource root) {
		doSave(root);
	}

	private void doSave(ScannedResource<Long> parent) {
		parent.children().forEach(res -> {
			SysResource param = EntityKit.createEmpty(SysResource.class);
			param.setIdentity(res.identity());
			SysResource entity = service.get(param);
			if (entity == null) {//新增
				entity = new SysResource();
				entity.setDeleted(false);
				entity.setParentId(parent.id());
				entity.setIdentity(res.identity());
				if (StringUtils.isNotBlank(res.description())) {
					entity.setName(res.description());
					entity.setIntro(res.description());
				} else {
					entity.setName(res.identity());
				}
				if (StringUtils.isNotBlank(res.uri())) {
					entity.setUrl(res.uri());
				} else {
					entity.setUrl("uri:///");
				}
				service.insert(entity);
				res.id(entity.getId());//保存ID
			} else {//修改
				res.id(entity.getId());
				entity.setIdentity(res.identity());
				entity.setDeleted(false);
				entity.setParentId(parent.id());
				entity.setIntro(res.description());
				if (StringUtils.isNotBlank(res.description())) {
					entity.setName(res.description());
					entity.setIntro(res.description());
				} else {
					entity.setName(res.identity());
				}
				if (StringUtils.isNotBlank(res.uri())) {
					entity.setUrl(res.uri());
				}
				service.updateSelective(entity);
			}

			// persist children
			if (res.children().size() > 0) {
				doSave(res);
			}
		});
	}
}
