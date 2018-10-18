package io.microvibe.booster.system.service;

import io.microvibe.booster.core.base.annotation.BaseComponent;
import io.microvibe.booster.core.base.service.MybatisBaseService;
import io.microvibe.booster.system.entity.SysLog;
import io.microvibe.booster.system.mapper.SysLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 系统日志信息
 *
 * @author Q
 * @version 1.0
 * @since Jun 22, 2018
 */
@Service
@Slf4j
public class SysLogService extends MybatisBaseService<SysLog, Long> {

	@Autowired
	@BaseComponent
	private SysLogMapper sysLogMapper;

	@Async
	public void doLogAsync(SysLog sysLog) {
		super.insertSelective(sysLog);
	}
}
