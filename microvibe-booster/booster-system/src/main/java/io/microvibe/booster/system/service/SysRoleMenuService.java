package io.microvibe.booster.system.service;

import io.microvibe.booster.core.base.annotation.BaseComponent;
import io.microvibe.booster.system.entity.SysRoleMenu;
import io.microvibe.booster.system.mapper.SysRoleMenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SysRoleMenuService extends SysBaseService<SysRoleMenu, Long> {
	@Autowired
	@BaseComponent
	private SysRoleMenuMapper roleMenuMapper;

	public void saveRoleMenus(Long roleId, Long[] added, Long[] deleted){
		for(Long id: deleted){
			Map<String,Object> param = new HashMap<>();
			param.put("roleId",roleId);
			param.put("menuId",id);
			SysRoleMenu rel = get(param);
			if(rel != null){
				delete(rel);
			}
		}
		for(Long id: added){
			Map<String,Object> param = new HashMap<>();
			param.put("roleId",roleId);
			param.put("menuId",id);
			SysRoleMenu rel = get(param);
			if(rel != null){
				rel.setDeleted(false);
				updateSelective(rel);
			}else{
				rel = new SysRoleMenu();
				rel.setRoleId(roleId);
				rel.setMenuId(id);
				rel.setDeleted(false);
				insertSelective(rel);
			}
		}
	}

//	public List<SysRoleMenu> selectByCondition(SysRoleMenu sysRoleMenu) {
//		return roleMenuMapper.selectByEntity(sysRoleMenu);
//	}

//	public long selectCountByCondition(SysRoleMenu sysRoleMenu) {
//		return roleMenuMapper.countByEntity(sysRoleMenu);
//	}

//	public long deleteByPrimaryKey(SysRoleMenu sysRoleMenu) {
//		return roleMenuMapper.deleteByEntity(sysRoleMenu);
//	}
}
