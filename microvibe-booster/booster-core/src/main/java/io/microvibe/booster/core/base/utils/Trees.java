package io.microvibe.booster.core.base.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.microvibe.booster.commons.utils.StringUtils;
import io.microvibe.booster.commons.utils.property.PropertyUtil;
import io.microvibe.booster.core.base.entity.Treeable;
import io.microvibe.booster.core.base.model.JsTreeView;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;

/**
 * @author Qt
 * @since Jul 05, 2018
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Trees {

	public static <T extends Treeable<T, ID>, ID extends Serializable> JsTreeView asJsTreeView(T treeable) {
		return new JsTreeView(treeable);
	}

	public static <T extends Treeable<T, ID>, ID extends Serializable> List<JsTreeView> asFlatJsTreeView(Collection<T> trees) {
		List<JsTreeView> list = new ArrayList<>();
		if (trees.size() > 0) {
			if (!(trees instanceof SortedSet)) {
				T t1 = trees.iterator().next();
				if (t1 instanceof Comparable) {
					// 重排序
					TreeSet<T> sortedSet = new TreeSet<>();
					sortedSet.addAll(trees);
					trees = sortedSet;
				}
			}
			for (T t : trees) {
				list.add(asJsTreeView(t));
			}
		}
		return list;
	}

	public static <T extends Treeable<T, ID>, ID extends Serializable> List<JsTreeView> asJsTreeView(Collection<T> trees) {
		return asJsTreeView((ID) null, trees);
	}

	public static <T extends Treeable<T, ID>, ID extends Serializable> List<JsTreeView> asJsTreeView(ID parentId, Collection<T> trees) {
		if (trees.size() > 0) {
			trees = asTreeStructWithParentId(parentId, trees);
			return asFlatJsTreeView(trees);
		} else {
			return new ArrayList<>();
		}
	}

	public static <T extends Treeable<T, ID>, ID extends Serializable> Collection<T> asTreeStruct(Collection<T> trees) {
		return asTreeStruct(tree -> isBlank(tree.getParentId()), trees);
	}

	private static boolean isBlank(Serializable o) {
		return o instanceof String ? StringUtils.isBlank((String) o) : o == null;
	}

	public static <T extends Treeable<T, ID>, ID extends Serializable> Collection<T> asTreeStructWithParentId(ID parentId, Collection<T> trees) {
		return asTreeStruct(tree -> isBlank(parentId) && isBlank(tree.getParentId())
			|| parentId.equals(tree.getParentId()), trees);
	}

	public static <T extends Treeable<T, ID>, ID extends Serializable> Collection<T> asTreeStruct(Function<T, Boolean> isRoot, Collection<T> trees) {
		// region mapping && resort
		Map<ID, Treeable<T, ID>> all = new HashMap<>();
		if (!(trees instanceof SortedSet)) {
			Iterator<T> iter = trees.iterator();
			T t1 = iter.next();
			all.put(t1.getId(), t1);
			if (t1 instanceof Comparable) {
				// 重排序
				TreeSet<T> sortedSet = new TreeSet<>();
				sortedSet.add(t1);
				while (iter.hasNext()) {
					T tree = iter.next();
					sortedSet.add(tree);
					all.put(tree.getId(), tree);
				}
				trees = sortedSet;
			} else {
				while (iter.hasNext()) {
					T tree = iter.next();
					all.put(tree.getId(), tree);
				}
			}
		} else {
			for (Treeable<T, ID> tree : trees) {
				all.put(tree.getId(), tree);
			}
		}
		// endregion

		List<T> top = new ArrayList<>();
		// region calculate top nodes
		for (T tree : trees) {
			ID parentId = tree.getParentId();
			if (all.containsKey(parentId)) {
				all.get(parentId).addChild(tree);
			} else {
				if (isRoot.apply(tree)) {
					top.add(tree);
				}
			}
		}
		// endregion
		return top;
	}

	/**
	 * 包装为json格式,返回给前端
	 * <pre>
	 * [
	 *   {
	 *     "id": 1,
	 *     "children": [
	 *     ]
	 *   }
	 *   ...
	 * ]
	 * </pre>
	 *
	 * @param treeList
	 * @return
	 */
	public static <T extends Treeable<T, ID>, ID extends Serializable> JSONArray asJson(Collection<T> treeList, String... includedProperties) {
		return asJsonWithParentId((ID) null, treeList, includedProperties);
	}

	public static <T extends Treeable<T, ID>, ID extends Serializable> JSONArray asJsonWithParentId(ID topParentId, Collection<T> treeList, String... includedProperties) {
		return asJson(tree -> (isBlank(topParentId) && isBlank(tree.getParentId())
				|| topParentId.equals(tree.getParentId()))
			, treeList, includedProperties);
	}

	public static <T extends Treeable<T, ID>, ID extends Serializable> JSONArray asJson(Function<T, Boolean> isRoot, Collection<T> treeList, String... includedProperties) {
		Map<ID, Treeable<T, ID>> all = new HashMap<>();
		for (Treeable<T, ID> tree : treeList) {
			all.put(tree.getId(), tree);
		}

		List<T> top = new ArrayList<>();

		for (T tree : treeList) {
			ID parentId = tree.getParentId();
			if (all.containsKey(parentId)) {
				all.get(parentId).addChild(tree);
			} else {
				if (isRoot.apply(tree)) {
					top.add(tree);
				}
			}
		}

		JSONArray json = new JSONArray();
		for (Treeable tree : top) {
			json.add(asJson(tree, includedProperties));
		}
		return json;
	}

	public static <T extends Treeable<T, ID>, ID extends Serializable> JSONObject asJson(T tree, String... includedProperties) {
		JSONObject json = new JSONObject(true);
		json.put("id", tree.getId());
		if (includedProperties == null || includedProperties.length == 0) {
			json = (JSONObject) JSONObject.toJSON(tree);
		} else {
			for (String include : includedProperties) {
				try {
					json.put(include, PropertyUtil.getProperty(tree, include));
				} catch (Exception e) {
				}
			}
		}
		Collection<T> children = tree.getChildren();
		if (children != null && children.size() > 0) {
			JSONArray jsonChildren = new JSONArray();
			for (Treeable child : children) {
				jsonChildren.add(asJson(child, includedProperties));
			}
			json.put("children", jsonChildren);
		}
		return json;
	}

}
