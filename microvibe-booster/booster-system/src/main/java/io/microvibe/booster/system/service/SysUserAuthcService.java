package io.microvibe.booster.system.service;

import io.microvibe.booster.core.base.annotation.BaseComponent;
import io.microvibe.booster.core.base.shiro.authc.AuthcChannel;
import io.microvibe.booster.core.env.CacheNames;
import io.microvibe.booster.system.entity.SysUserAuthc;
import io.microvibe.booster.system.mapper.SysUserAuthcMapper;
import io.microvibe.booster.system.mapper.SysUserMapper;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Map;

@Service
public class SysUserAuthcService extends SysBaseService<SysUserAuthc, Long> {

	@BaseComponent
	@Autowired
	SysUserAuthcMapper sysUserAuthcMapper;
	@Autowired
	SysUserMapper sysUserMapper;

	@Cacheable(cacheNames = CacheNames.SYS_USER_AUTHC_CACHE_NAME, key = "'authc-'+#root.args[0].name()+'-'+#root.args[1]")
	public SysUserAuthc getByAuthcChannelAndUserId(AuthcChannel authcChannel, Long userId) {
		SysUserAuthc authc = sysUserAuthcMapper.getByAuthcChannelAndUserId(authcChannel, userId);
		/*if (authc != null) {
			SysUser user = sysUserMapper.getById(authc.getUserId());
			authc.setSysUser(user);
		}*/
		return authc;
	}

	/**
	 * 通过用户ID查询该用户的所有认证方式信息
	 *
	 * @param userId 用户ID
	 * @return
	 */
	public List<SysUserAuthc> findUserAuthcsByUserId(Long userId) {
		return sysUserAuthcMapper.findByUserId(userId);
	}

	@Override
	public long delete(Long id) {
		SysUserAuthc one = getById(id);
		if (one != null) {
			// 确保进入缓存AOP
			return ((SysUserAuthcService) AopContext.currentProxy()).delete(one);
		} else {
			return 0;
		}
	}

	@Override
	@CleanAuthcChannelAndCodeCache
	public long delete(SysUserAuthc m) {
		return super.delete(m);
	}

	@Override
	@CacheEvict(cacheNames = CacheNames.SYS_USER_AUTHC_CACHE_NAME, allEntries = true)
	public long delete(Map<String, Object> param) {
		return baseMapper.deleteByMap(param);
	}

	@Override
	@CleanAuthcChannelAndCodeCache
	public long saveOrUpdate(SysUserAuthc entity) {
		return super.saveOrUpdate(entity);
	}


	@Override
	@CleanAuthcChannelAndCodeCache
	public long update(SysUserAuthc entity) {
		return super.update(entity);
	}

	@CacheEvict(cacheNames = CacheNames.SYS_USER_AUTHC_CACHE_NAME,
		key = "'authc-'+#root.args[0].authcChannel.name()+'-'+#root.args[0].userId")
	@Target({ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	@interface CleanAuthcChannelAndCodeCache {
	}
}
