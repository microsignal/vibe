package io.microvibe.booster.system.service;

import io.microvibe.booster.core.base.annotation.BaseComponent;
import io.microvibe.booster.system.entity.SysMenu;
import io.microvibe.booster.system.mapper.SysMenuMapper;
import io.microvibe.booster.system.mapper.SysRoleMenuMapper;
import io.microvibe.booster.system.model.CurrentUser;
import io.microvibe.booster.system.toolkit.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysMenuService extends SysBaseService<SysMenu, Long> {

	@Autowired
	@BaseComponent
	private SysMenuMapper menuMapper;
	@Autowired
	private SysUserService userService;
	@Autowired
	private SysRoleMenuMapper roleMenuMapper;


	/**
	 * 用户所有导航栏
	 *
	 * @return
	 */
	public List<SysMenu> getUserAllCategories() {
		CurrentUser<Long> user = Users.getRequiredCurrentUser();
		if (user.isAdministrator()) {
			return menuMapper.getAllCategories();
		}
		return menuMapper.getUserAllCategories(user.getId());
	}

	/**
	 * 用户权限内的所有菜单
	 *
	 * @param parentId 上级菜单或导航栏
	 * @return
	 */
	public List<SysMenu> getUserAllMenusByParent(Long parentId) {
		CurrentUser<Long> user = Users.getRequiredCurrentUser();
		if (user.isAdministrator()) {
			return menuMapper.getAllMenus(parentId);
		}
		return menuMapper.getUserAllMenusByParent(user.getId(), parentId);
	}

	/**
	 * 用户权限内的所有按钮
	 *
	 * @param parentId 上级菜单或导航栏
	 * @return
	 */
	public List<SysMenu> getUserButtons(Long parentId) {
		CurrentUser<Long> user = Users.getRequiredCurrentUser();
		if (user.isAdministrator()) {
			return menuMapper.getSubButtons(parentId);
		}
		return menuMapper.getUserButtons(user.getId(), parentId);
	}

	/**
	 * 根导航栏
	 *
	 * @return
	 */
	public List<SysMenu> getRootCategories() {
		return menuMapper.getRootCategories();
	}

	/**
	 * 下级导航栏
	 *
	 * @param parentId 上级菜单或导航栏
	 * @return
	 */
	public List<SysMenu> getSubCategories(Long parentId) {
		return menuMapper.getSubCategories(parentId);
	}

	/**
	 * 下级菜单
	 *
	 * @param parentId 上级菜单或导航栏
	 * @return
	 */
	public List<SysMenu> getSubMenus(Long parentId) {
		return menuMapper.getSubMenus(parentId);
	}

	/**
	 * 下级按钮
	 *
	 * @param parentId 上级菜单或导航栏
	 * @return
	 */
	public List<SysMenu> getSubButtons(Long parentId) {
		return menuMapper.getSubButtons(parentId);
	}

	// region


//	public List<SysMenu> getMenuNotSuper() {
//		return menuMapper.getMenuNotSuper();
//	}
//
//
//	public SysMenu getById(String id) {
//		return menuMapper.getById(id);
//	}
//
//	public List<SysMenu> getMenuChildren(String id) {
//		return menuMapper.getMenuChildren(id);
//	}
//
//	public JSONArray getMenuJson(String roleId) {
//		List<SysMenu> mList = menuMapper.getMenuNotSuper();
//		JSONArray jsonArr = new JSONArray();
//		int pNum = 1000, num = 0;
//		for (SysMenu sysMenu : mList) {
//			SysMenu menu = getChild(sysMenu.getId(), true, pNum, num);
//			jsonArr.add(menu);
//			pNum += 1000;
//		}
//		System.out.println(jsonArr);
//		return jsonArr;
//	}
//
//	public JSONArray getMenuJsonList() {
//		List<SysMenu> mList = menuMapper.getMenuNotSuper();
//		JSONArray jsonArr = new JSONArray();
//		for (SysMenu sysMenu : mList) {
//			SysMenu menu = getChild(sysMenu.getId(), false, 0, 0);
//			jsonArr.add(menu);
//		}
//		System.out.println(jsonArr);
//		return jsonArr;
//	}
//
//
//	public JSONArray getMenuJsonByUser(List<SysMenu> menuList) {
//		//List<SysMenu> menuListOne=new ArrayList<>();//获取第一级别
//		JSONArray jsonArr = new JSONArray();
//		Collections.sort(menuList, new Comparator<SysMenu>() {
//			@Override
//			public int compare(SysMenu o1, SysMenu o2) {
//				if (o1.getOrderNo() == null || o2.getOrderNo() == null) {
//					return -1;
//				}
//				if (o1.getOrderNo() > o2.getOrderNo()) {
//					return 1;
//				}
//				if (o1.getOrderNo().equals(o2.getOrderNo())) {
//					return 0;
//				}
//				return -1;
//			}
//		});
//		int pNum = 1000;
//		for (SysMenu menu : menuList) {
//			if (StringUtils.isEmpty(menu.getParentId())) {
//				SysMenu sysMenu = getChilds(menu, pNum, 0, menuList);
//				jsonArr.add(sysMenu);
//				pNum += 1000;
//			}
//		}
//		return jsonArr;
//	}
//
//	public SysMenu getChilds(SysMenu menu, int pNum, int num, List<SysMenu> menuList) {
//		for (SysMenu menus : menuList) {
//			if (menu.getId().equals(menus.getParentId())) {
////				++num;
//				SysMenu m = getChilds(menus, pNum, num, menuList);
////				m.setNum(pNum + num);
//				menu.addChild(m);
//			}
//		}
//		return menu;
//
//	}
//
//	public List<SysMenu> getMenuChildrenAll(String id) {
//		return menuMapper.getMenuChildrenAll(id);
//	}
//
//	/**
//	 * @param id   父菜单id
//	 * @param flag true 获取非按钮菜单 false 获取包括按钮在内菜单 用于nemuList展示
//	 * @param pNum 用户控制侧拉不重复id tab 父循环+1000
//	 * @param num  用户控制侧拉不重复id tab 最终效果 1001 10002 2001 2002
//	 * @return
//	 */
//	public SysMenu getChild(String id, boolean flag, int pNum, int num) {
//		SysMenu sysMenu = menuMapper.getById(id);
//		List<SysMenu> mList = null;
//		if (flag) {
//			mList = menuMapper.getMenuChildren(id);
//		} else {
//			mList = menuMapper.getMenuChildrenAll(id);
//		}
//		for (SysMenu menu : mList) {
////			++num;
//			SysMenu m = getChild(menu.getId(), flag, pNum, num);
////			if (flag)
////				m.setNum(pNum + num);
//			sysMenu.addChild(m);
//		}
//		return sysMenu;
//	}
//
//	public JSONArray getTreeUtil(String roleId) {
//		TreeUtil treeUtil = null;
//		List<SysMenu> mList = menuMapper.getMenuNotSuper();
//		JSONArray jsonArr = new JSONArray();
//		for (SysMenu sysMenu : mList) {
//			treeUtil = getChildByTree(sysMenu.getId(), false, 0, null, roleId);
//			jsonArr.add(treeUtil);
//		}
//		System.out.println(jsonArr);
//		return jsonArr;
//
//	}
//
//	public TreeUtil getChildByTree(String id, boolean flag, int layer, String pId, String roleId) {
//		layer++;
//		SysMenu sysMenu = menuMapper.getById(id);
//		List<SysMenu> mList = null;
//		if (flag) {
//			mList = menuMapper.getMenuChildren(id);
//		} else {
//			mList = menuMapper.getMenuChildrenAll(id);
//		}
//		TreeUtil treeUtil = new TreeUtil();
//		treeUtil.setId(sysMenu.getId());
//		treeUtil.setName(sysMenu.getName());
//		treeUtil.setLayer(layer);
//		treeUtil.setPId(pId);
//		/**判断是否存在*/
//		if (!StringUtils.isEmpty(roleId)) {
//			SysRoleMenu sysRoleMenu = new SysRoleMenu();
//			sysRoleMenu.setMenuId(sysMenu.getId());
//			sysRoleMenu.setRoleId(roleId);
//			long count = roleMenuMapper.countByEntity(sysRoleMenu);
//			if (count > 0)
//				treeUtil.setChecked(true);
//		}
//		for (SysMenu menu : mList) {
//			TreeUtil m = getChildByTree(menu.getId(), flag, layer, menu.getId(), roleId);
//			treeUtil.getChildren().add(m);
//		}
//		return treeUtil;
//	}

	// endregion
}
