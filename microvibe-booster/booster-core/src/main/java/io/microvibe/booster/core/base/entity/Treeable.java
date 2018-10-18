package io.microvibe.booster.core.base.entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author Qt
 * @since Jul 07, 2018
 */
public interface Treeable<T extends Treeable<T, ID>, ID extends Serializable> {

	String SEPARATOR = "/";
	String PARENT_ID_FIELD = "parentId";
	String PARENT_PATH_FIELD = "parentPath";

	public default String parentIdField() {
		return PARENT_ID_FIELD;
	}

	public default String parentPathField() {
		return PARENT_PATH_FIELD;
	}

	public ID getId();

	public void setId(ID id);

	public ID getParentId();

	public void setParentId(ID parentId);

	public String getParentPath();

	public void setParentPath(String parentPath);

	public Collection<T> getChildren();

	public default void addChild(T childTree) {
		Collection<T> children = getChildren();
		if (children != null) {
			children.add(childTree);
		}
	}

	/**
	 * 是否是根节点
	 *
	 * @return
	 */
	public default boolean isRoot() {
		return getParentId() == null;
	}

	/**
	 * 是否是叶子节点
	 *
	 * @return
	 */
	public default boolean isLeaf() {
		Collection<T> children = getChildren();
		return children == null || children.size() == 0;
	}


	/**
	 * 是否有孩子节点
	 *
	 * @return
	 */
	public default boolean isHasChildren() {
		Collection<T> children = getChildren();
		return children != null && children.size() > 0;
	}


}
