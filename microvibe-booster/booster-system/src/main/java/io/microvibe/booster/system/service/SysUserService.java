package io.microvibe.booster.system.service;

import io.microvibe.booster.commons.err.ValidationException;
import io.microvibe.booster.core.base.annotation.BaseComponent;
import io.microvibe.booster.system.entity.SysUser;
import io.microvibe.booster.system.entity.SysUserRole;
import io.microvibe.booster.system.mapper.SysUserMapper;
import io.microvibe.booster.system.mapper.SysUserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysUserService extends SysBaseService<SysUser, Long> {

	@BaseComponent
	@Autowired
	SysUserMapper sysUserMapper;

	@Autowired
	SysUserRoleMapper sysUserRoleMapper;

	@Autowired
	SysRoleService roleService;

	@Autowired
	SysUserRoleService roleUserService;


	public SysUser getByUserName(String username) {
		return sysUserMapper.getByUserName(username);
	}

	public SysUser getByEmail(String email) {
//		return sysUserMapper.getByEmail(email);
		return null;
	}

	public SysUser getByMobilePhone(String mobilePhone) {
//		return sysUserMapper.getByMobilePhone(mobilePhone);
		return null;
	}
 	public List<SysUserRole> selectByCondition(SysUserRole sysRoleUser) {
		return sysUserRoleMapper.selectByEntity(sysRoleUser);
	}
	public long add(SysUser user) {
		//密码加密
//		String pwd= Md5Util.getMD5(user.getPassword().trim(),user.getUserName().trim());
//		user.setPassword(pwd);
		return sysUserMapper.insertByEntity(user);
	}

	@Override
	protected boolean requiredDeleteChecking() {
		return true;
	}

	@Override
	protected void doDeleteChecking(SysUser entity) {
		if(entity.isAdministrator()){
			throw new ValidationException("超管禁止删除");
		}
		SysUserRole roleUser = new SysUserRole();
		roleUser.setUserId(entity.getId());
		long count = sysUserRoleMapper.countByEntity(roleUser);
		if (count > 0) {
			throw new ValidationException("已经绑定角色，无法删除");
		}
	}

	public boolean existsByUserName(String username) {
		return sysUserMapper.countByUserName(username)>0;
	}
	public boolean existsByEmail(String email) {
//		return sysUserMapper.countByEmail(email)>0;
		return false;
	}
	public boolean existsByMobilePhone(String mobilePhone) {
//		return sysUserMapper.countByMobilePhone(mobilePhone)>0;
		return false;
	}

//	public List<Checkbox> getUserRoleByJson(String id) {
//		List<SysRole> roleList = roleService.findAll(new SysRole());
//		SysUserRole sysRoleUser = new SysUserRole();
//		sysRoleUser.setUserId(id);
//		List<SysUserRole> kList = selectByCondition(sysRoleUser);
//		System.out.println(kList.size());
//		List<Checkbox> checkboxList = new ArrayList<>();
//		Checkbox checkbox = null;
//		for (SysRole sysRole : roleList) {
//			checkbox = new Checkbox();
//			checkbox.setId(sysRole.getId());
//			checkbox.setName(sysRole.getRoleName());
//			for (SysUserRole sysRoleUser1 : kList) {
//				if (sysRoleUser1.getRoleId().equals(sysRole.getId())) {
//					checkbox.setCheck(true);
//				}
//			}
//			checkboxList.add(checkbox);
//		}
//		return checkboxList;
//	}
//
//	public long rePass(SysUser user) {
////		return sysUserMapper.rePass(user);
//		// fixme 修改密码
//		return 0L;
//	}
}
