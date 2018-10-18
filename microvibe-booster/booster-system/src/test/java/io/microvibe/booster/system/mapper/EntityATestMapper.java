package io.microvibe.booster.system.mapper;

import io.microvibe.booster.core.base.mybatis.mapping.BaseMapper;
import io.microvibe.booster.core.base.mybatis.annotation.AfterEntityScanner;
import io.microvibe.booster.core.base.mybatis.annotation.AutoMapper;
import io.microvibe.booster.system.entity.A;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Qt
 * @since Aug 25, 2018
 */
@Mapper
@AfterEntityScanner
@AutoMapper
public interface EntityATestMapper extends BaseMapper<A, Long> {
}
