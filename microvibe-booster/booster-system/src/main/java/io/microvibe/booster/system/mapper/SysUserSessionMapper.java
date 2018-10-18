package io.microvibe.booster.system.mapper;

import io.microvibe.booster.core.base.mybatis.mapping.BaseMapper;
import io.microvibe.booster.core.base.mybatis.annotation.AfterEntityScanner;
import io.microvibe.booster.system.entity.SysUserSession;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统用户在线会话信息表
 * @author Qt
 * @since Aug 01, 2018
 */
@Mapper
@AfterEntityScanner
public interface SysUserSessionMapper extends BaseMapper<SysUserSession, String> {

}
