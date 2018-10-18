package io.microvibe.booster.system.service;

import io.microvibe.booster.core.base.annotation.BaseComponent;
import io.microvibe.booster.system.entity.SysDictTree;
import io.microvibe.booster.system.mapper.SysDictTreeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 系统树形字典表
 * @since Jun 19, 2018
 * @version 1.0
 * @author Q
 */
@Service
@Slf4j
public class SysDictTreeService extends SysBaseService<SysDictTree, Long> {

    @Autowired
    @BaseComponent
    private SysDictTreeMapper sysDictTreeMapper;

}
