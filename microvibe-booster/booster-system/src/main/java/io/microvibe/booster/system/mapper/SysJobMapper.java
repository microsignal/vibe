package io.microvibe.booster.system.mapper;

import io.microvibe.booster.core.base.mybatis.mapping.BaseMapper;
import io.microvibe.booster.core.base.mybatis.annotation.AfterEntityScanner;
import io.microvibe.booster.system.entity.SysJob;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@AfterEntityScanner
public interface SysJobMapper extends BaseMapper<SysJob, String> {
}
