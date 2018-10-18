package io.microvibe.booster.core.base.model;

import com.alibaba.fastjson.util.TypeUtils;
import io.microvibe.booster.core.base.entity.Treeable;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * JSTree 模型
 *
 * @author Qt
 * @since Jul 28, 2018
 */
@Data
@Slf4j
public class JsTreeView implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private String parent;
	private String text;
	private String icon;
	private State state;
	private Long orderNo;
	private List<JsTreeView> children;

	public JsTreeView() {
	}

	public <T extends Treeable<T, ID>, ID extends Serializable> JsTreeView(T treeable) {
		ID id = treeable.getId();
		if(id == null){
			setId(null);
		}else if(id instanceof String){
			setId((String) id);
		}else{
			setId(id.toString());
		}
		ID parentId = treeable.getParentId();
		if(parentId == null){
			setParent(null);
		}else if(parentId instanceof String){
			setParent((String) parentId);
		}else{
			setParent(parentId.toString());
		}
		try {
			setText(TypeUtils.castToString(PropertyUtils.getProperty(treeable, "text")));
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
		try {
			setIcon(TypeUtils.castToString(PropertyUtils.getProperty(treeable, "icon")));
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
		try {
			setOrderNo(TypeUtils.castToLong(PropertyUtils.getProperty(treeable, "orderNo")));
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
		Collection<T> children = treeable.getChildren();
		if (children.size() > 0) {
			this.children = new ArrayList<>(children.size());
			for (T child : children) {
				this.children.add(new JsTreeView(child));
			}
		}
	}

	@Data
	public static class State {
		private boolean opened;
		private boolean selected;
		private boolean disabled;

		public State() {
		}

		public State(boolean selected) {
			this.selected = selected;
		}

		public static State selected() {
			return new State(true);
		}
	}
}
