package io.microvibe.booster.system.mapper;

import io.microvibe.booster.core.base.mybatis.mapping.BaseMapper;
import io.microvibe.booster.core.base.mybatis.annotation.AfterEntityScanner;
import io.microvibe.booster.system.entity.SysFile;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通用文件表
 * @since Jul 14, 2018
 * @version 1.0
 * @author Q
 */
@Mapper
@AfterEntityScanner
public interface SysFileMapper extends BaseMapper<SysFile, String> {

}
