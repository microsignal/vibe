package io.microvibe.booster.system.toolkit;

import com.alibaba.fastjson.JSONArray;
import io.microvibe.booster.core.base.utils.Trees;
import io.microvibe.booster.system.entity.SysMenu;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Qt
 * @since Jul 05, 2018
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Menus {

	/**
	 * 包装为json格式,返回给前端
	 * <pre>
	 * [
	 *   {
	 *     "id": 1,
	 *     "title": "管理菜单",
	 *     "url": "forms/advanced.html",
	 *     "icon": "fa-cube",
	 *     "children": [
	 *     ]
	 *   }
	 *   ...
	 * ]
	 * </pre>
	 *
	 * @param menuList
	 * @return
	 */
	public static JSONArray asJson(List<SysMenu> menuList) {
		return asJson(null, menuList);
	}

	public static JSONArray asJson(Long topParentId, List<SysMenu> menuList) {
		// sort
		Collections.sort(menuList, new Comparator<SysMenu>() {
			@Override
			public int compare(SysMenu o1, SysMenu o2) {
				if (o1.getOrderNo() == null || o2.getOrderNo() == null) {
					return -1;
				}
				if (o1.getOrderNo() > o2.getOrderNo()) {
					return 1;
				}
				if (o1.getOrderNo().equals(o2.getOrderNo())) {
					return 0;
				}
				return -1;
			}
		});

		return Trees.asJsonWithParentId(topParentId, menuList, "name", "text", "url", "icon", "visible");
	}

//	public static void main(String[] args) {
//		List<SysMenu> list = new ArrayList<>();
//
//		for (int i = 0; i < 6; i++) {
//			SysMenu m = new SysMenu();
//			m.setId("m"+i);
//			m.setName("m"+i);
//			if(i > 1) m.setParentId("m"+(i%2));
//			list.add(m);
//		}
//		System.out.println(JSON.toJSONString(asJson(list),true));
//	}

}
