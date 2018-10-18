package io.microvibe.booster.system.mapper;

import io.microvibe.booster.core.base.mybatis.mapping.BaseMapper;
import io.microvibe.booster.system.entity.SysDictTree;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统树形字典表
 * @since Jun 19, 2018
 * @version 1.0
 * @author Q
 */
@Mapper
public interface SysDictTreeMapper extends BaseMapper<SysDictTree, Long> {

}
