package io.microvibe.booster.system.auth;

import io.microvibe.booster.core.base.shiro.authc.AuthcSessionPacket;
import io.microvibe.booster.core.base.shiro.authc.IShiroAuthcService;
import io.microvibe.booster.system.entity.*;
import io.microvibe.booster.system.err.*;
import io.microvibe.booster.system.mapper.SysMenuMapper;
import io.microvibe.booster.system.mapper.SysResourceMapper;
import io.microvibe.booster.system.mapper.SysRoleMapper;
import io.microvibe.booster.system.model.CurrentUser;
import io.microvibe.booster.system.model.GroupOrgHolder;
import io.microvibe.booster.system.service.SysAuthService;
import io.microvibe.booster.system.toolkit.Users;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class ShiroAuthcService implements IShiroAuthcService<Long> {

	@Autowired
	private SysAuthService authService;

	@Autowired
	private SysRoleMapper sysRoleMapper;
	@Autowired
	private SysResourceMapper sysResourceMapper;
	@Autowired
	private SysMenuMapper sysMenuMapper;

	@Override
	public AuthcSessionPacket<Long> doAuthentication(AuthenticationToken token) {
		long start = System.currentTimeMillis();
		try {
			return authService.login(token);
		} catch (UserNotExistsException e) {
			throw new UnknownAccountException(e.getMessage(), e);
		} catch (UserPasswordNotMatchException e) {
			throw new AuthenticationException(e.getMessage(), e);
		} catch (UserPasswordRetryLimitExceedException e) {
			throw new ExcessiveAttemptsException(e.getMessage(), e);
		} catch (UserBlockedException e) {
			throw new LockedAccountException(e.getMessage(), e);
		} catch (UserException e) {
			log.error(e.getMessage(), e);
			throw new AuthenticationException(e.getMessage(), e);
		} catch (AuthenticationException e) {
			throw e;
		} catch (Exception e) {
			log.error("login error", e);
			throw new AuthenticationException(e.getMessage(), e);
		} finally {
			long end = System.currentTimeMillis();
			log.info("用户登录认证,执行时间: {}ms", (end - start));
		}
	}

	@Override
	public AuthorizationInfo getAuthorizationInfo(AuthcSessionPacket<Long> sessionKey) {
		long start = System.currentTimeMillis();
		try {
			SystemAuthorizationInfo authorizationInfo = new SystemAuthorizationInfo();

			Set<String> stringRoles = new LinkedHashSet<>();
			Long userId = sessionKey.getUserId();

			// 角色
			List<SysRole> roles = sysRoleMapper.getRolesByUserId(userId);
			roles.forEach(role -> {
				stringRoles.add(role.getIdentity());
			});
			authorizationInfo.setRoles(stringRoles);// 角色

			CurrentUser currentUser = Users.getCurrentUser();
			if (currentUser.isAdministrator()) {//admin
				authorizationInfo.addStringPermission("**");
			}
			authorizationInfo.addStringPermission("default");// default

			// 权限
			{
				Set<String> stringPermissions = new LinkedHashSet<>();
				List<SysResource> userResources = sysResourceMapper.getUserResources(userId);
				userResources.forEach(sysResource -> {
					String resourceIdentity = sysResource.getIdentity();
					if (StringUtils.isNotBlank(resourceIdentity)) {
						stringPermissions.add(resourceIdentity);// 资源权限
					}
				});

				List<SysMenu> userMenus = sysMenuMapper.getUserAllMenusForAuth(userId);
				userMenus.forEach(menu -> {
					String resourceIdentity = StringUtils.trimToEmpty(menu.getResourceIdentity());
					if (StringUtils.isNotBlank(resourceIdentity)) {
						stringPermissions.add(resourceIdentity);// 资源权限
					}
				});
				authorizationInfo.addStringPermissions(stringPermissions);// 权限
			}
			/*// 公司权限
			{
				if (currentUser instanceof GroupOrgHolder) {
					authorizationInfo.addOrgPermission(new SysOrganization(((GroupOrgHolder) currentUser).getOrgId()));
					authorizationInfo.addGroupPermission(new SysGroup(((GroupOrgHolder) currentUser).getGroupId()));
				}
				List<SysOrganization> userOrgs = sysOrganizationMapper.getUserOrgs(userId);
				userOrgs.forEach(org -> authorizationInfo.addOrgPermission(org));
			}
			// 化验单权限
			{
				List<SysViewQcAssayFormat> userAssays = sysViewQcAssayFormatMapper.getUserAssays(userId);
				userAssays.forEach(assay -> authorizationInfo.addAssayPermission(assay));

			}*/
			return authorizationInfo;
		} finally {
			long end = System.currentTimeMillis();
			log.info("查询用户角色及权限,执行时间: {}ms", (end - start));
		}

	}


}
