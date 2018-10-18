package io.microvibe.booster.system.mapper;

import io.microvibe.booster.core.base.mybatis.mapping.BaseMapper;
import io.microvibe.booster.core.base.mybatis.lang.VelocityLangDriver;
import io.microvibe.booster.core.base.shiro.authc.AuthcChannel;
import io.microvibe.booster.system.entity.SysUserAuthc;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SysUserAuthcMapper extends BaseMapper<SysUserAuthc, Long> {

	@Select({"{{",
		"select $sql.columnsAlias('SysUserAuthc') ",
		"from $sql.table('SysUserAuthc') ",
		"where $sql.column('SysUserAuthc','authcChannel')=#{channel} ",
		"and $sql.column('SysUserAuthc','userId') = #{userId} ",
		"}}"})
	@Lang(VelocityLangDriver.class)
	@ResultType(SysUserAuthc.class)
	SysUserAuthc getByAuthcChannelAndUserId(@Param("channel") AuthcChannel authcChannel, @Param("userId") Long userId);

	@Select({"{{",
		"select $sql.columnsAlias('SysUserAuthc') ",
		"from $sql.table('SysUserAuthc') ",
		"where $sql.column('SysUserAuthc','userId') = #{userId} ",
		"}}"})
	@Lang(VelocityLangDriver.class)
	@ResultType(SysUserAuthc.class)
	List<SysUserAuthc> findByUserId(@Param("userId") Long userId);

}
