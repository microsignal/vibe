package io.microvibe.booster.system.service;

import io.microvibe.booster.commons.spring.ApplicationContextHolder;
import io.microvibe.booster.core.base.shiro.authc.token.LocalAuthcToken;
import io.microvibe.booster.system.auth.ShiroAuthcService;
import io.microvibe.booster.system.entity.SysDict;
import io.microvibe.booster.system.entity.SysRole;
import io.microvibe.booster.test.BaseTestCase;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.junit.Test;

import java.util.List;

/**
 * @author Qt
 * @since Aug 22, 2018
 */
public class SysServiceTest extends BaseTestCase {

	@Test
	public void test001() {
		SysDictService sysDictService = ApplicationContextHolder.getBean(SysDictService.class);

		List<SysDict> all = sysDictService.findAll();
		System.out.println(all);
	}

	@Test
	public void test002() {
		ShiroAuthcService service = ApplicationContextHolder.getBean(ShiroAuthcService.class);
		AuthenticationToken token = new LocalAuthcToken("admin", "123456");

		Subject subject = SecurityUtils.getSubject();
		subject.login(token);
		System.out.println(subject.isPermitted(""));
//		AuthcSessionPacket<Long> authcSessionPacket = service.doAuthentication(token);
//		AuthorizationInfo authorizationInfo = service.getAuthorizationInfo(authcSessionPacket);
	}

	@Test
	public void test003() {
		SysRoleService service = ApplicationContextHolder.getBean(SysRoleService.class);
		SysRole role = new SysRole();
		role.setIdentity("admin");
		role.setName("admin");
		service.insertSelective(role);
	}
}
