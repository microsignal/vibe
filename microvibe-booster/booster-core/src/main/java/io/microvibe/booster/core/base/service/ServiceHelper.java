package io.microvibe.booster.core.base.service;

import io.microvibe.booster.core.base.entity.BaseEntity;
import io.microvibe.booster.core.base.entity.Treeable;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @author Qt
 * @since Aug 27, 2018
 */
@Slf4j
class ServiceHelper {

	/**
	 * 将树结构实体对象的`id`值拼接到`treepath`尾部
	 *
	 * @param treePath 树节点路径
	 * @param tree     树结构实体
	 * @return
	 */
	static String appendTreePath(String treePath, Treeable tree) {
		if (treePath != null) {
			if (!treePath.endsWith(Treeable.SEPARATOR)) {
				treePath += Treeable.SEPARATOR;
			}
			treePath += tree.getId() + Treeable.SEPARATOR;
		} else {
			//treePath = Treeable.SEPARATOR + tree.getId() + Treeable.SEPARATOR;// 以`/`开头,需要同步改数据
			treePath = tree.getId() + Treeable.SEPARATOR;
		}
		return treePath;
	}

	static <ID extends Serializable> void makeParentToNull(BaseEntity<ID> entity) {
		((Treeable) entity).setParentId(null);
		((Treeable) entity).setParentPath(null);
		if (entity.getUpdatingNullFields() != null) {
			if (entity.getUpdatingNullFields().contains(((Treeable) entity).parentIdField())) {
				entity.getUpdatingNullFields().add(((Treeable) entity).parentPathField());
			}
		}
	}
}
